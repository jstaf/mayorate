package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.input.InputEventAPI;

import java.awt.*;
import java.util.*;
import java.util.List;

import data.scripts.plugins.beamRenderer.BeamRendererPlugin;
import data.scripts.plugins.beamRenderer.BeamSpec;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class ilk_ShotgunSpecialBehavior extends BaseEveryFrameCombatPlugin {

    private static final Set<String> SHOTGUNPROJ_IDS = new HashSet<>();
    static {
        //add Projectile IDs here.
        SHOTGUNPROJ_IDS.add("ilk_shotgun_shot");
        SHOTGUNPROJ_IDS.add("ilk_laserhead_shot");
    }

    CombatEngineAPI engine;

    @Override
    public void init(CombatEngineAPI engine) {
        this.engine = engine;
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {    

        if (engine == null || engine.isPaused()) {
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
                        Vector2f fireLoc = proj.getLocation();
                        BeamRendererPlugin.addBeam(new BeamSpec(engine, proj.getSource(), fireLoc, 700f, proj.getFacing(),
                                1000f, DamageType.ENERGY, 0f,
                                0.7f, 0.1f, 0.2f, //duration, in, out
                                "beams", "ilk_fakeBeamFX", 27, new Color(224,184,225,175)));
                        Global.getSoundPlayer().playSound("ilk_graser_fire", 1f, 1f, fireLoc, proj.getVelocity());

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
