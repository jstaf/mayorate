package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class ilk_LaserheadSpecialBehavior extends BaseEveryFrameCombatPlugin {

    private static final String SOUND_ID = "ilk_laserhead_detonate";

    private static final Set<String> LASERHEADPROJ_IDS = new HashSet<>();

    static {
        //add Projectile IDs here.
        LASERHEADPROJ_IDS.add("ilk_laserhead_shot");
    }

    private CombatEngineAPI engine;

    public void advance(float amount, List<InputEventAPI> events) {
        if (engine.isPaused()) {
            return;
        }

        for (DamagingProjectileAPI proj : engine.getProjectiles()) {
            String spec = proj.getProjectileSpecId();

            if (LASERHEADPROJ_IDS.contains(spec)) {
                Vector2f loc = proj.getLocation();
                Vector2f vel = proj.getVelocity();
                for (int i = 0; i < 20; i++) {
                    Color color = new Color(241, 139, 114, 5);

                    Vector2f randomVel = MathUtils.getRandomPointOnCircumference(null, MathUtils.getRandomNumberInRange(60f, 200f));
                    //randomVel.x += vel.x;
                    randomVel.y += vel.y;
                    //spec + "_clone" means is, if its got the same name in its name (except the "_clone" part) then it must be that weapon.
                    engine.spawnProjectile(proj.getSource(), null, spec + "_clone", loc, MathUtils.getRandomNumberInRange(0f, 360f), randomVel);

                    Global.getSoundPlayer().playSound(SOUND_ID, 1f, 1f, proj.getLocation(), proj.getVelocity());

                    engine.spawnExplosion(loc, new Vector2f(0f, 0f), color, 25, 0.5f);
                }
                engine.removeEntity(proj);
            }
        }
    }

    public void init(CombatEngineAPI engine) {
        this.engine = engine;
    }
    
    public void renderInUICoords(ViewportAPI viewport) {
        //???
    }
    
    public void renderInWorldCoords(ViewportAPI viewport) {
        //???
    }
}
