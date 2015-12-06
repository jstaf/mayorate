package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import java.awt.Color;
import org.lazywizard.lazylib.FastTrig;
import org.lwjgl.util.vector.Vector2f;

public class ilk_IonBombEffect implements OnHitEffectPlugin {

    // The sound played when the warhead detonates
    private static final String SOUND_ID = "ilk_ionbomb_detonate";

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine) {
        int numberOfExplosions = 40; // More for smoother ring, fewer for better performance
        float radiusExpansionRate = 500f; // In pixels per second (I think)
        float explosionRadius = 40f; // In pixels
        float duration = 0.4f; // In seconds
        Color color = new Color(188, 239, 84, 10);

        for (float angle = 0; angle < Math.PI * 2; angle += (Math.PI * 2) / numberOfExplosions) {
            engine.spawnExplosion(point, new Vector2f((float) FastTrig.cos(angle) * radiusExpansionRate, (float) FastTrig.sin(angle) * radiusExpansionRate), color, explosionRadius, duration); // Duration of explosion
        }

        // Sound follows enemy that was hit
        Global.getSoundPlayer().playSound(SOUND_ID, 1f, 1f, target.getLocation(), target.getVelocity());
    }
}
