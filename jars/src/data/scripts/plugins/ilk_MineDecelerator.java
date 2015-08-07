package data.scripts.plugins;

import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.input.InputEventAPI;

import java.util.List;

/**
 * Created by jeff on 07/08/15.
 */
public class ilk_MineDecelerator extends BaseEveryFrameCombatPlugin {

    CombatEngineAPI engine;

    private float interval = 0f;
    private static final float THRESHOLD = 0.1f;
    private static final float DECEL_CHANCE = 0.15f;
    private static final float DECEL_AMT = 0.2f;

    @Override
    public void init(CombatEngineAPI engine) {
        this.engine = engine;
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if (engine == null || engine.isPaused()) {
            return;
        }

        interval += amount;

        if (interval > THRESHOLD) {
            interval = 0f;

            for (DamagingProjectileAPI proj : engine.getProjectiles()) {
                String spec = proj.getProjectileSpecId();
                if (spec == null) {
                    return;
                }

                if (spec.contains("ilk_mine_mirv")) {
                    //randomly decelerate our mine projectiles to give a nicer spread
                    if (Math.random() < DECEL_CHANCE) {
                        proj.getVelocity().scale(DECEL_AMT);
                    }
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
