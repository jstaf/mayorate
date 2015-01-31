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

public class ilk_ShotgunSpecialBehavior extends BaseEveryFrameCombatPlugin {

    private static final Set<String> SHOTGUNPROJ_IDS = new HashSet<>();
    
    private static final String SOUND_ID = "ilk_laserhead_detonate";
    private static final float SPREAD = 20f;
    private static final Color LASER_COLOR = new Color(241, 139, 114, 5);
    
    static {
        //add Projectile IDs here.
        SHOTGUNPROJ_IDS.add("ilk_shotgun_shot");
        SHOTGUNPROJ_IDS.add("ilk_laserhead_shot");
    }

    @Override
    public void init(CombatEngineAPI engine) {
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        CombatEngineAPI engine = Global.getCombatEngine();

        if (engine.isPaused()) {
            return;
        }

        for (DamagingProjectileAPI proj : engine.getProjectiles()) {
            String spec = proj.getProjectileSpecId();

            if (SHOTGUNPROJ_IDS.contains(spec)) {
                Vector2f loc = proj.getLocation();
                Vector2f vel = proj.getVelocity();
                switch (spec) {
                    case "ilk_shotgun_shot":
                        for (int i = 0; i < 18; i++) {
                            Vector2f randomVel = MathUtils.getRandomPointOnCircumference(null, MathUtils.getRandomNumberInRange(0f, 120f));
                            randomVel.x += vel.x;
                            randomVel.y += vel.y;
                            //spec + "_clone" means is, if its got the same name in its name (except the "_clone" part) then it must be that weapon.
                            engine.spawnProjectile(proj.getSource(), proj.getWeapon(), spec + "_clone", loc, proj.getFacing(), randomVel);
                        }
                        engine.removeEntity(proj);
                        break;
                        
                    case "ilk_laserhead_shot":
                        for (int i = 0; i < 6; i++) {
                            Vector2f randomVel = MathUtils.getRandomPointOnCircumference(null, MathUtils.getRandomNumberInRange(60f, 200f));
                            //randomVel.x += vel.x;
                            randomVel.y += vel.y;
                            engine.spawnProjectile(proj.getSource(), proj.getWeapon(), spec + "_clone", loc, 
                                    proj.getFacing()+ MathUtils.getRandomNumberInRange(-SPREAD, SPREAD), randomVel);

                            Global.getSoundPlayer().playSound(SOUND_ID, 1f, 1f, proj.getLocation(), proj.getVelocity());

                            engine.spawnExplosion(loc, new Vector2f(0f, 0f), LASER_COLOR, 25, 0.5f);
                        }
                        engine.removeEntity(proj);
                        break;
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
