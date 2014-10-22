package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import data.scripts.util.ilk_AnamorphicFlare;
import java.awt.Color;
//import org.dark.shaders.distortion.DistortionShader;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
//import org.dark.shaders.distortion.RippleDistortion;
//import org.dark.shaders.light.LightShader;
//import org.dark.shaders.light.StandardLight;
 

public class ilk_NukeOnHitEffect implements OnHitEffectPlugin {

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine) {
        if (projectile.getSource() != null) {
            ilk_AnamorphicFlare.createFlare(projectile.getSource(), new Vector2f(projectile.getLocation()), Global.getCombatEngine(), 1f, 0.02f, (float) Math.random() * 5f - 2.5f, 4f, 2f, new Color(255, 165, 0, 255), new Color(255, 225, 150, 255));
        }

        Global.getCombatEngine().spawnExplosion(point, new Vector2f(), new Color(255, 121, 117, 255), 500f, 0.5f);
        Global.getCombatEngine().addHitParticle(point, new Vector2f(), 400f, 1f, 2f, new Color(255, 255, 255, 200));
        Global.getCombatEngine().addHitParticle(point, new Vector2f(), 1000f, 1f, 2f, new Color(255, 121, 117, 255));
        
        /* Commented out for now until shaderlib is 0.65a compatible.
        RippleDistortion shockwave = new RippleDistortion();
        shockwave.setLocation(point);
        shockwave.setIntensity(8f);
        shockwave.setLifetime(2f);
        shockwave.setSize(500f);
        shockwave.setFrameRate(84f);
        shockwave.setCurrentFrame(10);
        shockwave.fadeOutIntensity(1f);
        DistortionShader.addDistortion(shockwave);
        
        StandardLight light = new StandardLight();
        light.setLocation(point);
        light.setColor(new Color(255, 121, 117, 255));
        light.setSize(1500f);
        light.setIntensity(2f);
        light.fadeOut(0.5f);
        LightShader.addLight(light);
                */

        Global.getSoundPlayer().playSound("ilk_nuke_detonate", 1.1f, 0.8f, point, new Vector2f());
        
        //spawn stuff to cause damage
        Global.getCombatEngine().spawnProjectile(projectile.getSource(), projectile.getWeapon(), "ilk_nuke_primaryDet", point, 0f, null);
        Global.getCombatEngine().applyDamage(projectile, point, projectile.getHitpoints() * 100f, DamageType.FRAGMENTATION, 0f, false, false, projectile);
        for (int i = 0; i < 30; i++) {
            float angle = (float) (i - 1) * 12f;
            Vector2f location = MathUtils.getPointOnCircumference(projectile.getLocation(), 10f, angle);
            Global.getCombatEngine().spawnProjectile(projectile.getSource(), projectile.getWeapon(), "ilk_nuke_damage", location, angle, null);
        }
    }
}
