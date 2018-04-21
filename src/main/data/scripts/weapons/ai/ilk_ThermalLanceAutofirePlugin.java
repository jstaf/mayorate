package data.scripts.weapons.ai;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.AutofireAIPlugin;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.FluxTrackerAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import java.util.List;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.DefenseType;
import org.lazywizard.lazylib.combat.DefenseUtils;
import org.lwjgl.util.vector.Vector2f;

// An autofire plugin for the Thermal Lance as a built-in weapon. Does not aim, just decides whether
// to fire.
public class ilk_ThermalLanceAutofirePlugin implements AutofireAIPlugin {

  private static Logger logger = Global.getLogger(ilk_ThermalLanceAutofirePlugin.class);

  private final float SECONDS_OF_FLUX_NEEDED = 0.25f;
  private final float SOFT_FLUX_TO_START_FIRING_AT_SHIELDS = 0.05f;
  private final float SOFT_FLUX_TO_STOP_FIRING_AT_SHIELDS = 0.2f;

  private final ShipAPI ship;
  private final FluxTrackerAPI fluxTracker;
  private final WeaponAPI weapon;

  private boolean firing = false;
  private Vector2f target = null;
  private ShipAPI targetShip = null;

  public ilk_ThermalLanceAutofirePlugin(WeaponAPI weapon) {
    this.weapon = weapon;
    ship = weapon.getShip();
    fluxTracker = ship.getFluxTracker();
  }

  @Override
  public void advance(float amount) {
    // Recalculate most stats in case they are affected by dynamic modifiers.

    // Can we fire?
    final float fluxRequired = weapon.getFluxCostToFire() * SECONDS_OF_FLUX_NEEDED;
    if (weapon.isDisabled()
        || fluxTracker.getFluxLevel() + fluxRequired > fluxTracker.getMaxFlux()) {
      firing = false;
      return;
    }

    // What will we hit if we fire?
    double direction = Math.toRadians(450 - ship.getFacing());
    if (direction > Math.PI * 2) {
      direction -= Math.PI * 2;
    }
    final Vector2f start = weapon.getLocation();
    final Vector2f end =
        new Vector2f(
            start.getX() + (float) FastTrig.sin(direction) * weapon.getRange(),
            start.getY() + (float) FastTrig.cos(direction) * weapon.getRange());
    List<CombatEntityAPI> entities =
        CombatUtils.getAsteroidsWithinRange(weapon.getLocation(), weapon.getRange());
    for (ShipAPI targetShip : Global.getCombatEngine().getShips()) {
      // Disregard phased enemies.
      if (targetShip != ship
          && (targetShip.getPhaseCloak() == null || !targetShip.getPhaseCloak().isActive())
          && MathUtils.isWithinRange(targetShip, weapon.getLocation(), weapon.getRange())) {
        entities.add(targetShip);
      }
    }

    Vector2f firstLocationHit = null;
    CombatEntityAPI firstEntityHit = null;
    float closestHitRange = Float.POSITIVE_INFINITY;
    for (CombatEntityAPI entity : entities) {
      final Vector2f collisionPoint = CollisionUtils.getCollisionPoint(start, end, entity);
      if (collisionPoint == null) {
        continue;
      }
      final float distance = MathUtils.getDistance(start, collisionPoint);
      if (distance < closestHitRange) {
        firstLocationHit = collisionPoint;
        firstEntityHit = entity;
        closestHitRange = distance;
      }
    }

    // Avoid friendly fire, and wasting flux on nothing, asteroids, or hulks.
    if (firstEntityHit == null
        || !(firstEntityHit instanceof ShipAPI)
        || ((ShipAPI) firstEntityHit).isHulk()
        || firstEntityHit.getOwner() == ship.getOwner()) {
      firing = false;
      return;
    }

    // Fire on shields only if we are in a favorable flux situation.
    targetShip = (ShipAPI) firstEntityHit;
    final DefenseType defense = DefenseUtils.getDefenseAtPoint(targetShip, firstLocationHit);
    if (defense == DefenseType.SHIELD) {
      // If the shield is down we should dissipate hard flux rather than fire.
      final float fluxFloor = ship.getShield().isOff() ? 0.0f : fluxTracker.getHardFlux();
      final float fluxLevelAboveFloor =
          (fluxTracker.getCurrFlux() - fluxFloor) / fluxTracker.getMaxFlux();
      if ((firing && fluxLevelAboveFloor > SOFT_FLUX_TO_STOP_FIRING_AT_SHIELDS)
          || fluxLevelAboveFloor > SOFT_FLUX_TO_START_FIRING_AT_SHIELDS) {
        firing = false;
        return;
      }
    }

    target = firstLocationHit;
    firing = true;
  }

  @Override
  public void forceOff() {
    firing = false;
  }

  @Override
  public Vector2f getTarget() {
    return target;
  }

  @Override
  public ShipAPI getTargetShip() {
    return targetShip;
  }

  @Override
  public WeaponAPI getWeapon() {
    return weapon;
  }

  @Override
  public boolean shouldFire() {
    return firing;
  }
}
