package data.scripts.weapons.ai;

import com.fs.starfarer.api.combat.*;
import data.scripts.weapons.ilk_DisruptorOnHitEffect;
import java.util.ArrayList;
import java.util.List;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

/** Created by Jeff on 2015-08-13. */
public class ilk_DisruptorAI implements AutofireAIPlugin {

  private WeaponAPI weapon;
  private ShipAPI myShip;
  private FluxTrackerAPI myFlux;
  private float maxFlux;
  private float fluxDamage;

  private float fluxCost;

  private boolean shouldFire; // evidently the ship will open fire with the weapon if this is true
  private ShipAPI target; // actual target
  private Vector2f loc; // target lead

  public ilk_DisruptorAI(WeaponAPI weaponAPI) {
    weapon = weaponAPI;
    myShip = weapon.getShip();
    myFlux = myShip.getFluxTracker();
    maxFlux = myFlux.getMaxFlux();
    fluxCost = weapon.getFluxCostToFire();

    switch (weapon.getId()) {
      case "ilk_disruptor":
        fluxDamage = ilk_DisruptorOnHitEffect.FLUX_DAMAGE;
      case "ilk_disruptor_heavy":
        fluxDamage = ilk_DisruptorOnHitEffect.HEAVY_FLUX_DAMAGE;
    }
  }

  /**
   * Eval targets and decide whether or not to fire
   *
   * @param v delta time
   */
  @Override
  public void advance(float v) {
    // do we even have enough flux to fire?
    if (maxFlux - myFlux.getCurrFlux() < fluxCost) return;

    // update range every frame, as we could have a dynamic range-boosting mod like sensor drones
    float range =
        weapon.getRange() + 100f; // a little extra, to see if we can hit people for partial damage

    // do we already have a target we're supposed to be shooting at on the ship level?
    if (myShip.getShipTarget() != null
        && MathUtils.getDistance(myShip.getShipTarget(), myShip) < range) {
      target = myShip.getShipTarget();
    } else {
      target = evalThreats(AIUtils.getNearbyEnemies(myShip, range));
    }

    // are we pointing at the right target?
    if (target != null && !target.isHulk()) {
      // ignore the fighters whatver happens
      if (target.isFighter()) return;

      // lead target here
      loc = Vector2f.add(target.getLocation(), target.getVelocity(), new Vector2f());

      if (loc != null) {
        // projected hit location
        Vector2f curLoc =
            Vector2f.add(
                weapon.getLocation(),
                new Vector2f(
                    (float) FastTrig.cos(weapon.getCurrAngle()) * (weapon.getRange() + 100f), // x
                    (float) FastTrig.sin(weapon.getCurrAngle()) * (weapon.getRange() + 100f)), // y
                new Vector2f());
        // true if projected to hit
        shouldFire =
            CollisionUtils.getCollides(
                weapon.getLocation(), curLoc, loc, target.getCollisionRadius() / 2f);
      }
    } else {
      // reset aimpoint and to fire- nothing to shoot at
      loc = null;
      shouldFire = false;
    }
  }

  private ShipAPI evalThreats(List<ShipAPI> threats) {

    // remove bogus stuff
    List<ShipAPI> toRemove = new ArrayList<>();
    for (ShipAPI threat : threats) {

      // apparently asteroids got included somehow, which is dumb
      if (threat instanceof CombatAsteroidAPI) {
        toRemove.add(threat);
        continue;
      }
      // ignore the small ships
      if (threat.isDrone() || threat.isFighter()) {
        toRemove.add(threat);
        continue;
      }
      // ignore phased ships
      if (threat.isPhased()) {
        toRemove.add(threat);
        continue;
      }
    }
    threats.removeAll(toRemove);

    // check for few nearby ships
    if (threats.isEmpty()) return null;
    if (threats.size() == 1) return threats.get(0);

    // higher ranking = greater threat/opportunity
    int[] rankings = new int[threats.size()];
    // determine "opportunity level"
    for (ShipAPI threat : threats) {
      int index = threats.indexOf(threat);

      // can we overload someone?
      FluxTrackerAPI enemyFlux = threat.getFluxTracker();
      if (enemyFlux.getMaxFlux() - enemyFlux.getCurrFlux() < fluxDamage) {
        return threat;
      }

      // do they have vulnerable/no shield?
      if (shieldIsVuln(threat, myShip)) {
        return threat;
      }

      // they close by?
      if (MathUtils.getDistance(threat, myShip) < 500f) {
        rankings[index] = 3;
        continue;
      }
      // booooring...
      rankings[index] = 1;
    }
    // now eval size
    for (ShipAPI threat : threats) {
      int index = threats.indexOf(threat);
      switch (threat.getHullSpec().getHullSize()) {
        case CAPITAL_SHIP:
          rankings[index] += 5;
          break;
        case CRUISER:
          rankings[index] += 4;
          break;
        case DESTROYER:
          rankings[index] += 3;
          break;
        case FRIGATE:
          rankings[index] += 2;
          break;
        case FIGHTER:
          // rankings[index] = 0;
          break;
        default:
          // rankings[index] = 0;
          break;
      }
    }

    // now get and return highest priority element
    int highest = 0;
    for (int i = 1; i < rankings.length; i++) {
      if (rankings[i] > highest) {
        highest = i;
      } else if (rankings[i] == highest) {
        // break ties by finding closer target to current weapon angle
        if (VectorUtils.getAngle(myShip.getLocation(), threats.get(i).getLocation())
                - weapon.getArcFacing()
            < VectorUtils.getAngle(myShip.getLocation(), threats.get(highest).getLocation())
                - weapon.getArcFacing()) {
          highest = i;
        }
      }
    }
    if (highest != 0) {
      return threats.get(highest);
    } else {
      return null;
    }
  }

  /**
   * Determines if we can hit a ship where it's shield isn't covering
   *
   * @param enemy
   * @param ship
   * @return
   */
  public boolean shieldIsVuln(ShipAPI enemy, ShipAPI ship) {
    ShieldAPI shield = enemy.getShield();
    // do they even have a shield?
    if (shield == null || shield.isOff()) return true;

    // do we have LOS to where its shield isn't covering?
    float facing = shield.getFacing();
    float arc = shield.getActiveArc();
    float firingSolution = VectorUtils.getAngle(enemy.getLocation(), ship.getLocation());
    return (facing + arc > firingSolution) && (facing - arc < firingSolution);
  }

  @Override
  public boolean shouldFire() {
    return shouldFire;
  }

  @Override
  public void forceOff() {
    shouldFire = false;
    target = null;
  }

  /**
   * Target location to aim at
   *
   * @return
   */
  @Override
  public Vector2f getTarget() {
    return loc;
  }

  /**
   * Target ship to aim at
   *
   * @return
   */
  @Override
  public ShipAPI getTargetShip() {
    return target;
  }

  @Override
  public WeaponAPI getWeapon() {
    return weapon;
  }
}
