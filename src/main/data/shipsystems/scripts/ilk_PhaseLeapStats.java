package data.shipsystems.scripts;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

import org.dark.shaders.light.LightShader;
import org.dark.shaders.light.StandardLight;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class ilk_PhaseLeapStats extends BaseShipSystemScript {

  // balance constants
  // The base jump distance
  private static final float LEAP_DISTANCE = 600f;
  // The minimum distance from other entities at the end of the jump.
  private static final float MINIMUM_DISTANCE = 50f;
  // The increment of collision avoidance extensions.
  private static final float FAIL_DISTANCE = 100f;
  private static final float IMPACT_DAMAGE = 500f;
  private static final DamageType IMPACT_DAMAGE_TYPE = DamageType.ENERGY;

  // visual constants
  private static final String IMPACT_SOUND = "hit_solid";
  private static final Color EXPLOSION_COLOR = new Color(166, 50, 91);
  private static final float EXPLOSION_VISUAL_RADIUS = 200f;

  // Instance data
  private boolean isActive = false;
  private Vector2f startLoc;

  @Override
  public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
    if (!(stats.getEntity() instanceof ShipAPI)) {
      return;
    }
    ShipAPI ship = (ShipAPI) stats.getEntity();

    switch (state) {
    case IN:
      if (isActive) {
        return;
      }
      isActive = true;
      // find ship location before teleport
      startLoc = new Vector2f(ship.getLocation());
      if (ship.getVelocity().lengthSquared() > 0f) {
        for (int i = 0; i < 75; i++) {
          Global.getCombatEngine().addSmoothParticle(
              MathUtils.getRandomPointInCircle(startLoc, 1f * ship.getCollisionRadius()),
              (Vector2f) (new Vector2f(ship.getVelocity())).normalise()
                  .scale(LEAP_DISTANCE * 5f * (float) Math.random()),
              (float) Math.random() * 15f + 15f, (float) Math.random() * 0.2f + 0.8f,
              (float) Math.random() * 0.1f + 0.35f, EXPLOSION_COLOR);
        }
      }
      break;
    case OUT:
      if (!isActive) {
        return;
      }
      Vector2f endLoc = calculateEndLocation(ship, startLoc);
      ship.getLocation().set(endLoc);

      // teleport and face to target
      ShipAPI target = ship.getShipTarget();
      if ((target != null) && (!target.isHulk())) {
        Vector2f difference = Vector2f.sub(target.getLocation(), endLoc, null);
        double newDirection = Math.atan2(difference.getY(), difference.getX());
        newDirection = Math.toDegrees(newDirection);
        if (newDirection < 0) {
          newDirection += 360;
        }
        ship.setFacing((float) newDirection);
        ship.setAngularVelocity(0.0f);
      }

      StandardLight light = new StandardLight();
      light.setLocation(ship.getLocation());
      light.setColor(new Color(255, 121, 117, 255));
      light.setSize(500f);
      light.setIntensity(2f);
      light.fadeOut(0.5f);
      LightShader.addLight(light);

      applyJumpDamage(ship);

      isActive = false;
      break;
    case ACTIVE:
    case COOLDOWN:
    case IDLE:
      // Nothing to do.
    }
  }

  @Override
  public StatusData getStatusData(int index, State state, float effectLevel) {
    if (index == 0) {
      return new StatusData("ripping through space", false);
    }
    return null;
  }

  public static Vector2f calculateEndLocation(ShipAPI ship, Vector2f start) {
    // this ridiculous conversion is needed to make .getfacing() meaningful
    double direction = Math.toRadians(450 - ship.getFacing());
    if (direction > Math.PI * 2) {
      direction -= Math.PI * 2;
    }

    // calculate jump coordinates
    final float startLocX = start.getX();
    final float startLocY = start.getY();
    float jumpDistance = LEAP_DISTANCE;
    float endLocX;
    float endLocY;
    Vector2f endLoc;
    // check to see if we end up inside anything...
    while (true) {
      endLocX = startLocX + (float) FastTrig.sin(direction) * jumpDistance;
      endLocY = startLocY + (float) FastTrig.cos(direction) * jumpDistance;
      endLoc = new Vector2f(endLocX, endLocY);

      boolean collides = false;
      for (CombatEntityAPI inRangeObject : CombatUtils.getEntitiesWithinRange(endLoc, jumpDistance)) {
        if (inRangeObject == ship) {
          // don't do anything if its the ship activating the system
          continue;
        }

        if (MathUtils.isWithinRange(inRangeObject, endLoc, ship.getCollisionRadius() + MINIMUM_DISTANCE)) {
          collides = true;
          break;
        }
      }
      if (collides) {
        jumpDistance += FAIL_DISTANCE;
      } else {
        break;
      }
    }

    return endLoc;
  }

  // Applies damage to non-friendlies between startLoc and the ship's location.
  private void applyJumpDamage(ShipAPI ship) {
    final Vector2f endLoc = ship.getLocation();
    final float distance = MathUtils.getDistance(startLoc, endLoc);
    for (CombatEntityAPI inRangeObject : CombatUtils.getEntitiesWithinRange(startLoc, distance)) {
      // Don't do anything to friendlies
      if (inRangeObject.getOwner() == ship.getOwner()) {
        continue;
      }

      // Find point of impact between start and end location of jump
      Vector2f pointOfImpact = CollisionUtils.getCollisionPoint(startLoc, endLoc, inRangeObject);
      for (int i = 0; i < 5; i++) {
        if (pointOfImpact == null) {
          pointOfImpact = CollisionUtils.getCollisionPoint(
              MathUtils.getRandomPointInCircle(startLoc, ship.getCollisionRadius()),
              MathUtils.getRandomPointInCircle(endLoc, ship.getCollisionRadius()), inRangeObject);
        } else {
          break;
        }
      }

      if (pointOfImpact != null) {
        // Only proceed if getCollisionPoint returned a point of impact
        Global.getCombatEngine().spawnExplosion(pointOfImpact, inRangeObject.getVelocity(), EXPLOSION_COLOR,
            EXPLOSION_VISUAL_RADIUS * 2f, 0.1f);
        Global.getCombatEngine().spawnExplosion(pointOfImpact, inRangeObject.getVelocity(), EXPLOSION_COLOR,
            EXPLOSION_VISUAL_RADIUS, 0.5f);
        Global.getSoundPlayer().playSound(IMPACT_SOUND, 1f, 1f, pointOfImpact, inRangeObject.getVelocity());
        Global.getCombatEngine().applyDamage(inRangeObject, pointOfImpact, IMPACT_DAMAGE, IMPACT_DAMAGE_TYPE, 0, false,
            true, ship);
      }
    }
  }
}
