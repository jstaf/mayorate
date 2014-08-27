package data.scripts.plugins;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class ilk_ShotgunSpecialBehavior implements EveryFrameCombatPlugin {

    private static final Set<String> SHOTGUNPROJ_IDS = new HashSet<>();

    static {
        //add Projectile IDs here.
        SHOTGUNPROJ_IDS.add("ilk_shotgun_shot");
    }

    private CombatEngineAPI engine;

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if (engine.isPaused()) {
            return;
        }

        for (DamagingProjectileAPI proj : engine.getProjectiles()) {
            String spec = proj.getProjectileSpecId();

            if (SHOTGUNPROJ_IDS.contains(spec)) {
                Vector2f loc = proj.getLocation();
                Vector2f vel = proj.getVelocity();
                for (int i = 0; i < 18; i++) {
                    Vector2f randomVel = MathUtils.getRandomPointOnCircumference(null, MathUtils.getRandomNumberInRange(0f, 120f));
                    randomVel.x += vel.x;
                    randomVel.y += vel.y;
                    //spec + "_clone" means is, if its got the same name in its name (except the "_clone" part) then it must be that weapon.
                    engine.spawnProjectile(proj.getSource(), null, spec + "_clone", loc, proj.getFacing(), randomVel);
                }
                engine.removeEntity(proj);
            }
        }
    }

    @Override
    public void init(CombatEngineAPI engine) {
        this.engine = engine;
    }
}
