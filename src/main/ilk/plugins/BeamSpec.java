package ilk.plugins;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatAsteroidAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;

import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import ilk.util.CollisionUtilsEX;
import ilk.util.DamageUtils;

public class BeamSpec {

  // initialization variables
  CombatEngineAPI engine;
  ShipAPI source;
  float dps;
  DamageType type;
  float empDps;
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
  private static final float RECALC_INTERVAL = 0.1f;

  // renderer variables
  Vector2f startLoc;
  Vector2f hitLoc;
  boolean isDone;
  float intensity;
  float opacity;
  CombatEntityAPI target;

  public BeamSpec(CombatEngineAPI combatEngineAPI, ShipAPI setSource, Vector2f startLocSet, float rangeSet,
      float aimSet, float damageTotal, DamageType damageType, float empDamageTotal, float time, float fadeInTime,
      float fadeOutTime, String spriteKey, String spriteName, float wide, Color colorSet) {
    final float effectiveTime = time + 0.5f * (fadeInTime + fadeOutTime);
    engine = combatEngineAPI;
    source = setSource;
    startLoc = startLocSet;
    dps = damageTotal / effectiveTime;
    type = damageType;
    empDps = empDamageTotal / effectiveTime;
    duration = time;
    fadeOut = fadeOutTime;
    fadeIn = fadeInTime;
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

  /**
   * Recalculate dps and hit location based on updated time since last frame
   *
   * @param amount delta time
   */
  public void update(float amount) {

    delta += amount;

    // where are we at in the current beam firing cycle
    if (delta <= fadeIn) {
      intensity = delta / fadeIn;
      opacity = (float) (FastTrig.sin(intensity * Math.PI / 2));
      // second condition for elseif not necessary for next as values lower than
      // fadeIn have already
      // been caught
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

    // only recalc hitpoint after a certain update interval to avoid wasting cpu for
    // no reason
    interval += amount;
    if (interval > RECALC_INTERVAL) {
      interval = 0f;
      calcImpactPoint();
    }

    // recalc dps if we've hit something
    if (target != null) {
      float currDamage = dps * amount * intensity;
      float currEmp = empDps * amount * intensity;

      if (target instanceof ShipAPI) {
        ShipAPI ship = (ShipAPI) target;
        // Check for beam dps reduction--this is not taken into account by applyDamage
        // because it
        // does not know this is a beam.
        currDamage *= ship.getMutableStats().getBeamDamageTakenMult().getModifiedValue();
      }
      DamageUtils.applyDamageAtHitStrength(engine, target, hitLoc, currDamage, 0.5f * dps * intensity, type, currEmp,
          false, true, source);
    }
  }

  // did we hit something?
  void calcImpactPoint() {
    // default end point
    Vector2f end = MathUtils.getPointOnCircumference(startLoc, range, aim);
    CombatEntityAPI theTarget = null;

    // list all nearby entities that could be hit
    for (CombatEntityAPI entity : CombatUtils.getEntitiesWithinRange(startLoc, range + 500f)) {

      // ignore un-hittable stuff like phased ships
      if (entity.getCollisionClass() == CollisionClass.NONE) {
        continue;
      }

      // ignore friendlies
      if (entity.getOwner() == source.getOwner())
        continue;

      // check for collision
      if (CollisionUtils.getCollides(startLoc, end, entity.getLocation(), entity.getCollisionRadius())) {
        Vector2f collide = null;

        // ship collision?
        if (entity instanceof ShipAPI) {
          // find the collision point with shields/hull
          Vector2f hitPoint = CollisionUtilsEX.getShipCollisionPoint(startLoc, end, (ShipAPI) entity);
          if (hitPoint != null)
            collide = hitPoint;

          // asteroid collision?
        } else if (entity instanceof CombatAsteroidAPI) {
          Vector2f hitPoint = CollisionUtilsEX.getCollisionPointOnCircumference(startLoc, end, entity.getLocation(),
              entity.getCollisionRadius());
          if (hitPoint != null)
            collide = hitPoint;
        }

        // if impact is closer than the curent beam end point, set it as the new end
        // point and save
        // target
        if ((collide != null) && (MathUtils.getDistance(startLoc, collide) < MathUtils.getDistance(startLoc, end))) {
          end = collide;
          theTarget = entity;
        }
      }
    }

    // okay update variables
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
