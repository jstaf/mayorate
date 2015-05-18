package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Level;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

public class ilk_DisruptorPlugin extends BaseEveryFrameCombatPlugin {

    private static final Color EMP_CORE_COLOR = new Color(255, 255, 255, 255);
    private static final Color EMP_FRINGE_COLOR = new Color(232, 14, 86, 200);

    private float interval = 0f;
    private static final float THRESHOLD = 0.1f;

    private HashMap<DamagingProjectileAPI, Vector2f> projMap = new HashMap<>();

    CombatEngineAPI engine;

    @Override
    public void init(CombatEngineAPI engine) {
        this.engine = engine;
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if (engine.isPaused()) {
            return;
        }

        interval += amount;

        if (interval > THRESHOLD) {
            interval = 0f;

            //look for projectiles and add them to the map!
            for (DamagingProjectileAPI proj : engine.getProjectiles()) {
                String spec = proj.getProjectileSpecId();
                if (spec == null) {
                    return;
                }

                if (spec.contains("ilk_disruptor_shot") && !projMap.containsKey(proj)) {
                    projMap.put(proj, proj.getLocation());
                }
            }

            //now iterate through the map and make emparcs
            Iterator iter = projMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry pair = (Map.Entry) iter.next();
                DamagingProjectileAPI projectile = (DamagingProjectileAPI) pair.getKey();

                //remove projectiles that no longer are in the engine
                if (projectile == null) {
                    iter.remove();
                } else {
                    //update projectiles
                    Vector2f loc = (Vector2f) pair.getValue();
                    //Global.getLogger(ilk_DisruptorPlugin.class).log(Level.DEBUG, "Made it! loc:" + loc.x + ", " + loc.y);
                    engine.spawnEmpArc(projectile.getSource(), projectile.getLocation(), null, new SimpleEntity(loc), //owner, location, anchor, target
                            DamageType.ENERGY, 0f, 0f, //damage stats
                            1000000f, null, 12f, EMP_FRINGE_COLOR, EMP_CORE_COLOR); //maxrange, sfx, width, fringe, core

                    pair.setValue(projectile.getLocation());
                }
            }
        }
    }

    @Override
    public void renderInUICoords(ViewportAPI viewport) {
        //???
    }

    @Override
    public void renderInWorldCoords(ViewportAPI viewport) {
        //???
    }
}
