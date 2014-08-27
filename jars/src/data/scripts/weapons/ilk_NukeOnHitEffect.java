package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import data.scripts.util.ilk_AnamorphicFlare;
import java.awt.Color;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class ilk_NukeOnHitEffect implements OnHitEffectPlugin {

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine) {
        if (projectile.getSource() != null) {
            ilk_AnamorphicFlare.createFlare(projectile.getSource(), new Vector2f(projectile.getLocation()), Global.getCombatEngine(), 1f, 0.02f, (float) Math.random() * 5f - 2.5f, 4f, 2f, new Color(255, 165, 0, 255), new Color(255, 225, 150, 255));
        }

        Global.getCombatEngine().addHitParticle(projectile.getLocation(), new Vector2f(), 1200f, 1f, 2f, new Color(255, 165, 0, 255));
        Global.getCombatEngine().spawnExplosion(projectile.getLocation(), new Vector2f(), new Color(255, 225, 150, 255), 1250f, 0.15f);
        Global.getCombatEngine().spawnExplosion(projectile.getLocation(), new Vector2f(), new Color(255, 180, 60, 255), 750f, 1.5f);

        Global.getSoundPlayer().playSound("ilk_nuke_detonate", 1.1f, 0.8f, projectile.getLocation(), new Vector2f());

        Global.getCombatEngine().applyDamage(projectile, projectile.getLocation(), projectile.getHitpoints() * 100f, DamageType.FRAGMENTATION, 0f, false, false, projectile);
        for (int i = 0; i < 30; i++) {
            float angle = (float) (i - 1) * 12f;
            Vector2f location = MathUtils.getPointOnCircumference(projectile.getLocation(), 10f, angle);
            Global.getCombatEngine().spawnProjectile(projectile.getSource(), projectile.getWeapon(), "ilk_nuke_damage", location, angle, null);
        }
    }
}
