package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.DefenseUtils;
import org.lwjgl.util.vector.Vector2f;

public class ilk_HEAP_OnHitEffect implements OnHitEffectPlugin {

    private static final float RADIUS = 10f;
    private static final float RATIO = 0.1f;
    
    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine) {
        // only activate vs. ships
        if (target instanceof ShipAPI) {
            ShipAPI ship = (ShipAPI) target;
            //ignore shieldhits!
            if (!shieldHit) { 
                for (int i = 0; i < 5; i++) {
                    Vector2f hitPoint = MathUtils.getRandomPointInCircle(point, RADIUS);
                    if (CollisionUtils.isPointWithinBounds(hitPoint, target)) {
                        // get armor value and deal bonus damage based on 
                        float value = DefenseUtils.getArmorValue(ship, hitPoint);
                        engine.applyDamage(ship, hitPoint, value * RATIO, DamageType.HIGH_EXPLOSIVE, 0, true, false, projectile.getSource());
                    }
                }
            }
        }
    }
}
