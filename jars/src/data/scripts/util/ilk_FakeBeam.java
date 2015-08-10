// By Tartiflette and Deathfly
package data.scripts.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatAsteroidAPI;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import data.scripts.plugins.ilk_FakeBeamPlugin;
import java.util.List;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;

public class ilk_FakeBeam {

    //
    //Fake beam generator. 
    //
    //Create a visually convincing beam from arbitrary coordinates.
    //It however has several limitation:
    // - It deal damage instantly and is therefore only meant to be used for burst beams.
    // - It cannot be "cut" by another object passing between the two ends, a very short duration is thus preferable.
    // - Unlike vanilla, it deals full damage to armor, be carefull when using HIGH_EXPLOSIVE damage type.
    //
    // Most of the parameters are self explanatory but just in case:
    //
    //engine : Combat Engine
    //start : source point of the beam
    //range : maximum effective range (the beam will visually fade a few pixels farther)
    //aim : direction of the beam
    //width : width of the beam
    //fading : duration of the beam
    //normalDamage : nominal burst damage of the beam (don't forget to calculate the skill modifiers before that)
    //               will potentially be modified when fighting some modded factions like Exigency.
    //type : damage type of the beam
    //emp : nominal emp damage if any
    //source : ship dealing the damage
    //size : glow size on the impact point
    //duration : duration of the impact glow (should be at least as long as the beam fading)
    //color : color of the impact glow
    //
    //Note that there is no control over the beam's color, you'll have to directly modify the fakeBeamFX.png for that
    //    
    /////////////////////////////////////////
    //                                     //
    //             FAKE BEAM               //
    //                                     //
    /////////////////////////////////////////    
    public static void applyFakeBeamEffect(CombatEngineAPI engine, Vector2f start, float range, float aim, float width, float fading, float normalDamage, DamageType type, float emp, ShipAPI source, float size, float duration, Color color) {
        CombatEntityAPI theTarget = null;
        float damage = normalDamage;
        // beam sprite to darw.
        SpriteAPI beamSprite = Global.getSettings().getSprite("beams", "SCY_fakeBeamFX");

        //default end point
        Vector2f end = MathUtils.getPointOnCircumference(start, range, aim);
       

        //list all nearby entities that could be hit
        List<CombatEntityAPI> entity = CombatUtils.getEntitiesWithinRange(start, range + 500);
        if (!entity.isEmpty()) {
            for (CombatEntityAPI e : entity) {

                //ignore un-hittable stuff like phased ships
                if (e.getCollisionClass() == CollisionClass.NONE) {
                    continue;
                }
                
                

                //damage can be reduced against some modded ships
                float newDamage = normalDamage;

                Vector2f col = new Vector2f(1000000, 1000000);
                //ignore everything but ships...
                if (e instanceof ShipAPI
                        && CollisionUtils.getCollides(start, end, e.getLocation(), e.getCollisionRadius())) {
                    //check for a shield impact, then hull and take the closest one                  
                    ShipAPI s = (ShipAPI) e;

                    //find the collision point with shields/hull
                    Vector2f hitPoint = ilk_CollisionUtilsEX.getShipCollisionPoint(start, end, s);
                    if (hitPoint != null) {
                        col = hitPoint;
                    }

                    //check for modded ships with damage reduction (not in use)
                    if (s.getHullSpec().getBaseHullId().startsWith("exigency_")) {
                        newDamage = normalDamage / 2;
                    }
                    
                    //check for beam damage reduction
                    newDamage = normalDamage * s.getMutableStats().getBeamDamageTakenMult().getModifiedValue();

                } else //...and asteroids!
                if (e instanceof CombatAsteroidAPI
                        && CollisionUtils.getCollides(start, end, e.getLocation(), e.getCollisionRadius())) {
                    Vector2f cAst = ilk_CollisionUtilsEX.getCollisionPointOnCircumference(start, end, e.getLocation(), e.getCollisionRadius());
                    if (cAst != null) {
                        col = cAst;
                    }
                }

                //if there was an impact and it is closer than the curent beam end point, set it as the new end point and store the target to apply damage later damage
                if (col != new Vector2f(1000000, 1000000) && MathUtils.getDistance(start, col) < MathUtils.getDistance(start, end)) {
                    end = col;
                    theTarget = e;
                    damage = newDamage;
                }
            }

            //if the beam impacted something, apply the damage
            if (theTarget != null) {

                //damage
                engine.applyDamage(
                        theTarget,
                        end,
                        damage,
                        type,
                        emp,
                        false,
                        true,
                        source
                );
                //impact flash
                engine.addHitParticle(
                        end,
                        theTarget.getVelocity(),
                        (float) Math.random() * size / 2 + size,
                        1,
                        (float) Math.random() * duration / 2 + duration,
                        color
                );
                engine.addHitParticle(
                        end,
                        theTarget.getVelocity(),
                        (float) Math.random() * size / 4 + size / 2,
                        1,
                        0.1f,
                        Color.WHITE
                );
            }
            //Add the beam to the plugin
            ilk_FakeBeamPlugin.renderFakeBeam(start, MathUtils.getDistance(start, end) + 10, aim, width, duration, beamSprite, null);
        }
    }
}
