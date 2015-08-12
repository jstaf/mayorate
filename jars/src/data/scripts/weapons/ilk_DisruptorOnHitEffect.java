package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

public class ilk_DisruptorOnHitEffect implements OnHitEffectPlugin {

    private static final Color EMP_CORE_COLOR = new Color(255, 255, 255, 255);
    private static final Color EMP_FRINGE_COLOR = new Color(232, 14, 86, 200);

    private static final float HEAVY_FLUX_DAMAGE = 1500f;
    private static final float FLUX_DAMAGE = 600f;

    float fluxGenerated;

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine) {

        for (int i = 1; i < 5; i++) {
            Global.getCombatEngine().spawnEmpArc(projectile.getSource(), point, target, target, DamageType.ENERGY,
                    0f, 50f, 300f, null, 12,
                    EMP_FRINGE_COLOR,
                    EMP_CORE_COLOR);
        }

        if (target instanceof ShipAPI) {
            ShipAPI ship = (ShipAPI) target;
            String spec = projectile.getProjectileSpecId();
            switch (spec) {
                case "ilk_disruptor_shot":
                    fluxGenerated = FLUX_DAMAGE;
                    break;
                case "ilk_heavy_disruptor_shot":
                    fluxGenerated = HEAVY_FLUX_DAMAGE;
                    break;
            }
            // be nicer to those unfortunate souls who take this shot on armor
            if (!shieldHit) {
                fluxGenerated *= 0.5;
            }
            ship.getFluxTracker().increaseFlux(fluxGenerated, true);
        }
    }

}
