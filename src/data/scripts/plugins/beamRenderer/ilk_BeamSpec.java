package data.scripts.plugins.beamRenderer;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import data.scripts.util.ilk_CollisionUtilsEX;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;

/**
 * Created by Jeff on 2015-08-09.
 */
public class ilk_BeamSpec {

    // initialization variables
    CombatEngineAPI engine;
    ShipAPI source;
    float damage;
    DamageType type;
    float empDamage;
    float duration;
    float fadeOut;
    float fadeIn;
    float aim;
    float range;
    float width;
    SpriteAPI sprite;
    float size;
    Color beamColor;

    // dynamic variables calculated by update method
    private float interval;
    private float delta;
    private static final float RECALC_INTERVAL = 0.125f;

    //renderer variables
    Vector2f startLoc;
    Vector2f hitLoc;
    boolean isDone;
    float intensity;
    float opacity;
    CombatEntityAPI target;

    public ilk_BeamSpec(CombatEngineAPI combatEngineAPI, ShipAPI setSource, Vector2f startLocSet, float rangeSet, float aimSet, float damageAmt,
                        DamageType damageType, float empDamageAmt, float time, float fadeInSet, float fadeOutSet,
                        String spriteKey, String spriteName, float wide, Color colorSet) {
        engine = combatEngineAPI;
        source = setSource;
        startLoc = startLocSet;
        damage = damageAmt;
        type = damageType;
        empDamage = empDamageAmt;
        duration = time;
        fadeOut = fadeOutSet;
        fadeIn = fadeInSet;
        range = rangeSet;
        aim = aimSet;
        width = wide;
        size = 2 * width;
        sprite = Global.getSettings().getSprite(spriteKey, spriteName);
        sprite.setAdditiveBlend();
        beamColor = colorSet;

        interval = 0f;
        intensity = 0f;
        opacity = 0f;
        hitLoc = new Vector2f();
        isDone = false;
    }

    /** Recalculate damage and hit location based on updated time since last frame
     *
     * @param amount delta time
     */
    public void update(float amount) {

        delta += amount;

        // where are we at in the current beam firing cycle
        if (delta <= fadeIn) {
            intensity = delta / fadeIn;
            opacity = (float) (FastTrig.sin(intensity * Math.PI / 2));
            //second condition for elseif not necessary for next as values lower than fadeIn have already been caught
        } else if (delta <= duration + fadeIn) {
            intensity = 1f;
            opacity = 1f;
        } else if (delta <= fadeIn + duration + fadeOut) {
            intensity = 1f - ((delta - fadeIn - duration) / fadeOut);
            opacity = (float) (FastTrig.sin(intensity * Math.PI / 2));
        } else {
            intensity = 0f;
            opacity = 0f;
            isDone = true;
        }

        // only recalc hitpoint after a certain update interval to avoid wasting cpu for no reason
        interval += amount;
        if (interval > RECALC_INTERVAL) {
            interval = 0f;
            calcImpactPoint();
        }

        // recalc damage if we've hit something
        if (target != null) {
            float currDamage = damage * amount * intensity;
            float currEmp = empDamage * amount * intensity;

            if (target instanceof ShipAPI) {
                ShipAPI ship = (ShipAPI) target;
                //check for modded ships with damage reduction
                //if (ship.getHullSpec().getBaseHullId().startsWith("exigency_") ) currDamage /= 2;

                //check for beam damage reduction
                currDamage *= ship.getMutableStats().getBeamDamageTakenMult().getModifiedValue();

                //check for emp damage reduction
                currEmp *= ship.getMutableStats().getEmpDamageTakenMult().getModifiedValue();
            }
            // DEAL DE DAMAGE!
            engine.applyDamage(target, hitLoc, currDamage, type, currEmp, false, true, source);
        }
    }


    // did we hit something?
    void calcImpactPoint() {
        //default end point
        Vector2f end = MathUtils.getPointOnCircumference(startLoc, range, aim);
        CombatEntityAPI theTarget = null;

        //list all nearby entities that could be hit
        for (CombatEntityAPI entity : CombatUtils.getEntitiesWithinRange(startLoc, range + 500f)) {

            // ignore un-hittable stuff like phased ships
            if (entity.getCollisionClass() == CollisionClass.NONE) {
                continue;
            }

            // ignore friendlies
            if (entity.getOwner() == source.getOwner()) continue;

            // check for collision
            if (CollisionUtils.getCollides(startLoc, end, entity.getLocation(), entity.getCollisionRadius())) {
                Vector2f collide = null;

                // ship collision?
                if (entity instanceof ShipAPI) {
                    //find the collision point with shields/hull
                    Vector2f hitPoint = ilk_CollisionUtilsEX.getShipCollisionPoint(startLoc, end, (ShipAPI) entity);
                    if (hitPoint != null) collide = hitPoint;

                    // asteroid collision?
                } else if (entity instanceof CombatAsteroidAPI) {
                    Vector2f hitPoint = ilk_CollisionUtilsEX.getCollisionPointOnCircumference(startLoc, end, entity.getLocation(), entity.getCollisionRadius());
                    if (hitPoint != null) collide = hitPoint;
                }

                //if impact is closer than the curent beam end point, set it as the new end point and save target
                if ((collide != null) && (MathUtils.getDistance(startLoc, collide) < MathUtils.getDistance(startLoc, end))) {
                    end = collide;
                    theTarget = entity;
                }
            }
        }

        //okay update variables
        target = theTarget;
        hitLoc = end;
    }

    public void setColor(Color color) {
        sprite.setColor(color);
        beamColor = color;
    }

    public void setWidth(float wide) {
        width = wide;
    }

    public void setStartLoc(Vector2f startLoc) {
        this.startLoc = startLoc;
    }
}
