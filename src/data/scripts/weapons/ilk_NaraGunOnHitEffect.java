package data.scripts.weapons;

import com.fs.starfarer.api.combat.*;
import data.scripts.plugins.ilk_PenetratorManager;
import org.lwjgl.util.vector.Vector2f;

/**
 * On hit effect for narayana main gun
 */
public class ilk_NaraGunOnHitEffect implements OnHitEffectPlugin {

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine) {
        if (shieldHit) {
            ShipAPI victim = (ShipAPI) target;
            victim.setDefenseDisabled(true);
        } else {
            ilk_PenetratorManager.addPenetrator(point, projectile.getVelocity(), target, projectile.getSource(),
                    projectile.getDamageAmount(), projectile.getEmpAmount(), projectile.getDamageType(), 0.9f);
        }
    }
}
