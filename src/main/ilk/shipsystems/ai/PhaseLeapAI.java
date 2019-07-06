package ilk.shipsystems.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.FluxTrackerAPI;
import com.fs.starfarer.api.combat.ShieldAPI.ShieldType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.IntervalUtil;

import org.apache.log4j.Logger;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

import ilk.shipsystems.PhaseLeapStats;

// Note: this AI assumes the ship is somewhat similar to the Cimiterre's default variants--a
// high-alpha ship deleter that does not want to hang around in combat for too long.
// It ignores the possibility that the AI would rather broadside because putting Phase Leap on a
// broadside ship is sadistic.
public class PhaseLeapAI implements ShipSystemAIScript {

  private static final Logger logger = Global.getLogger(PhaseLeapAI.class);

  private static final float CLOSE_ENEMY_RANGE = 2000.0f;
  private static final float FLANKING_MARGIN_DEGREES = 20.0f;
  private static final float FLANKING_OPPORTUNITY_MULTIPLIER = 2.0f;
  // A buffer for range to account for weapons and bounds not being at the edge
  // of the collision circle.
  private static final float IN_RANGE_MARGIN = 150.0f;
  private static final float CRITICAL_THREAT_THRESHOLD = 3.0f;
  private static final float CRITICAL_TARGET_FLUX = 1500.0f;
  private static final float SIDE_ANGLE = 60.0f;
  private static final float SIDE_THREAT_MULTIPLIER = 0.67f;
  private static final float REAR_ANGLE = 120.0f;
  private static final float REAR_THREAT_MULTIPLIER = 0.33f;

  private ShipAPI ship;
  private ShipSystemAPI system;
  private ShipwideAIFlags flags;
  private CombatEngineAPI engine;
  private IntervalUtil interval = new IntervalUtil(0.2f, 0.3f);

  // Per-ship stats.
  // The angle off heading at which an enemy is considered flanking.
  private double flankAngle;

  // Per-invocation state.
  private Vector2f jumpDestination;
  private float engagementRange;
  private List<ShipAPI> nearbyEnemies;

  @Override
  public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
    this.ship = ship;
    this.system = system;
    this.flags = flags;
    this.engine = engine;

    if (ship.getShield() == null || ship.getShield().getType() == ShieldType.NONE
        || ship.getShield().getType() == ShieldType.PHASE) {
      flankAngle = 180.0f;
    } else if (ship.getShield().getType() == ShieldType.FRONT) {
      flankAngle = 0.5f * ship.getShield().getArc() - FLANKING_MARGIN_DEGREES;
    } else {
      flankAngle = ship.getShield().getArc() - FLANKING_MARGIN_DEGREES;
    }
  }

  @Override
  public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
    interval.advance(amount);
    if (engine.isPaused() || !interval.intervalElapsed() || !AIUtils.canUseSystemThisFrame(ship)
        || flags.hasFlag(AIFlags.DO_NOT_USE_FLUX)) {
      return;
    }

    jumpDestination = PhaseLeapStats.calculateEndLocation(ship, ship.getLocation());
    if (jumpDestination == null) {
      logger.error("No jump destination returned.");
      return;
    }
    nearbyEnemies = AIUtils.getNearbyEnemies(ship, CLOSE_ENEMY_RANGE);
    final float headingAngleFromVelocity = Math.abs(VectorUtils.getFacing(ship.getVelocity()) - ship.getFacing());
    final float headingAngleFromTarget = target == null ? 0.0f
        : Math.abs(MathUtils.getShortestRotation(VectorUtils.getAngle(ship.getLocation(), target.getLocation()),
            ship.getFacing()));

    // Use up to one charge to boost speed when moving forward fast with no nearby
    // enemies (so we can regenerate charges before needing them in combat).
    if (system.getAmmo() == system.getMaxAmmo()
        && ship.getVelocity().lengthSquared() >= 0.9f * ship.getMaxSpeedWithoutBoost() * ship.getMaxSpeedWithoutBoost()
        && headingAngleFromVelocity < 30.0f && headingAngleFromTarget < 30.0f && nearbyEnemies.isEmpty()) {
      logger.debug("Jumping for out-of-combat mobility.");
      activate(target);
      return;
    }

    engagementRange = getEngagementRange();

    final boolean backingOff = flags.hasFlag(AIFlags.BACK_OFF) || flags.hasFlag(AIFlags.BACKING_OFF)
        || flags.hasFlag(AIFlags.DO_NOT_PURSUE);
    final float baseThreat = getThreat(ship.getLocation(), ship.getFacing());
    float bestThreat = baseThreat;
    float baseOpportunity;
    if (target == null) {
      // TODO: auto-select a suitable target.
      baseOpportunity = 0.0f;
    } else if (headingAngleFromTarget > 30.0f) {
      baseOpportunity = 0.0f;
    } else {
      baseOpportunity = assessOpportunity(target, ship.getLocation());
    }

    ShipAPI bestTarget = null;
    float bestOpportunity = baseOpportunity;

    for (ShipAPI candidate : getTargetsForConsideration(target)) {
      if (candidate == null || candidate.getLocation() == null) {
        // TODO: Determine why this happens.
        continue;
      }
      // This is recomputed because different targets mean different facings and
      // possibly differences in which enemies are flanking.
      final float threat = getThreat(jumpDestination, VectorUtils.getAngle(candidate.getLocation(), jumpDestination));
      // Rather be here than there.
      if (threat > Math.max(baseThreat, CRITICAL_THREAT_THRESHOLD)) {
        logger.debug(String.format("Disregarding a jump toward %s: critical exposure (%f vs %f).", candidate.getName(),
            threat, Math.max(baseThreat, CRITICAL_THREAT_THRESHOLD)));
        continue;
      }

      // We still consider use when the AI is backing off because sometimes jumping is
      // a more efficient way to get out of trouble.
      if (backingOff && threat > baseThreat + 1.0) {
        logger.debug(String.format(
            "Disregarding a jump toward %s: AI is backing off and jumping would increase exposure (%f vs %f).",
            candidate.getName(), threat, baseThreat));
        continue;
      }

      final float opportunity = assessOpportunity(candidate, jumpDestination);
      logger.debug(String.format("Opportunity/threat for %s: %f/%f (best %f/%f)", candidate.getName(), opportunity,
          threat, bestOpportunity, bestThreat));
      // TODO: In low-opportunity situations, only use the system against smaller
      // ships or when outranged.
      if (opportunity > bestOpportunity || (opportunity == bestOpportunity && threat < bestThreat)) {
        bestOpportunity = opportunity;
        bestThreat = threat;
        bestTarget = candidate;
      }
    }
    if (bestTarget != null) {
      logger.debug("Jumping toward " + bestTarget.getName());
      activate(bestTarget);
    }

    logger.debug("Not jumping: no worthwhile opportunity.");
  }

  private void activate(ShipAPI target) {
    if (target != ship.getShipTarget()) {
      ship.setShipTarget(target);
    }
    ship.useSystem();
  }

  // Returns the minimum range of all non-pd weapons minus a buffer.
  private float getEngagementRange() {
    float range = Float.POSITIVE_INFINITY;
    for (WeaponAPI weapon : ship.getAllWeapons()) {
      if (!weapon.hasAIHint(WeaponAPI.AIHints.PD) && !weapon.hasAIHint(WeaponAPI.AIHints.PD_ONLY)) {
        range = Math.min(range, weapon.getRange());
      }
    }
    return range - IN_RANGE_MARGIN;
  }

  // Returns all eligible targets--either the
  private List<ShipAPI> getTargetsForConsideration(ShipAPI target) {
    // TODO: Investigate how sticky these targets are--e.g. if the ship already has
    // a target and we change it here, will it stick to the new target or just
    // revert targets and turn away?
    if (target != null && !target.isHulk()) {
      return Collections.singletonList(ship.getShipTarget());
    }

    List<ShipAPI> targets = new ArrayList<>();
    for (ShipAPI enemy : nearbyEnemies) {
      if (enemy.getLocation() == null || enemy.getHullSize() == HullSize.FIGHTER) {
        continue;
      }
      if (MathUtils.getDistance(enemy, jumpDestination) < engagementRange) {
        targets.add(enemy);
      }
    }
    return targets;
  }

  // Returns a rough estimate of how "hot" a particular position and facing is.
  private float getThreat(Vector2f position, float facing) {
    float threat = 0.0f;
    for (ShipAPI enemy : nearbyEnemies) {
      float shipThreat = getEnemyThreatAtRange(enemy, MathUtils.getDistance(enemy, position));
      if (enemy.getFluxTracker().isOverloadedOrVenting()) {
        continue;
      }
      // Increase threat if the enemy would be behind our shields.
      if (Math.abs(MathUtils.getShortestRotation(VectorUtils.getAngle(position, enemy.getLocation()),
          ship.getFacing())) > flankAngle) {
        shipThreat *= FLANKING_OPPORTUNITY_MULTIPLIER;
      }
      // Reduce threat if the enemy would not be pointed at us.
      float angleOffEnemy = Math
          .abs(MathUtils.getShortestRotation(VectorUtils.getAngle(enemy.getLocation(), position), enemy.getFacing()));
      if (angleOffEnemy >= REAR_ANGLE) {
        shipThreat *= REAR_THREAT_MULTIPLIER;
      } else if (angleOffEnemy >= SIDE_ANGLE) {
        shipThreat *= SIDE_THREAT_MULTIPLIER;
      }
      threat += shipThreat;
    }
    return threat;
  }

  // Returns a number representing roughly how much damage we can expect from the
  // enemy at the given
  // range.
  private float getEnemyThreatAtRange(ShipAPI enemy, float range) {
    // TODO: Actually look at weapons/flux.
    switch (enemy.getHullSize()) {
    case FIGHTER:
      return range > 500.0f ? 0.0f : (enemy.getWing() == null ? 0.5f : 1 / enemy.getWing().getSpec().getNumFighters());
    case FRIGATE:
      return range > 1000.0f ? 0.0f : 1.0f;
    case DESTROYER:
      return range > 1200.0f ? 0.0f : 2.0f;
    case CRUISER:
      return range > 1400.0f ? 0.0f : 4.0f;
    case CAPITAL_SHIP:
      return range > 1600.0f ? 0.0f : 8.0f;
    default:
      return 0.0f;
    }
  }

  // Scale:
  // - 0.0: out of range
  // - 1.0-2.0: In range, capable of shielding and decent flux remaining.
  // - 2.0: In range, no shield, venting, or flanked.
  // - 3.0-4.0: In range, capable of shielding but high flux.
  // - 4.0: In range, overloaded.
  // TODO: consider flanking of omni-shield ships being engaged.
  private float assessOpportunity(ShipAPI target, Vector2f position) {
    final FluxTrackerAPI flux = target.getFluxTracker();
    if (MathUtils.getDistance(target, position) > engagementRange) {
      return 0.0f;
    }
    if (flux.isOverloaded()) {
      return 4.0f;
    }
    if (flux.isVenting()) {
      return 2.0f;
    }
    if (target.getShield() == null || target.getShield().getType() == ShieldType.NONE) {
      return 2.0f;
    }
    if (target.getShield().getType() == ShieldType.FRONT
        && Math.abs(MathUtils.getShortestRotation(VectorUtils.getAngle(target.getLocation(), position),
            target.getFacing())) < 0.5f * target.getShield().getArc()) {
      return 2.0f;
    }
    if (flux.getMaxFlux() - flux.getCurrFlux() < CRITICAL_TARGET_FLUX) {
      return 3.0f + flux.getFluxLevel();
    }
    return 1.0f + flux.getFluxLevel();
  }
}
