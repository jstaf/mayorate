package ilk.shipsystems.ai;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.util.IntervalUtil;

import org.apache.log4j.Logger;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

public class FighterCoprocessingAI implements ShipSystemAIScript {

  private static final Logger logger = Global.getLogger(FighterCoprocessingAI.class);

  // The range at which a fighter under regroup orders will be considered
  // regrouping.
  private final float REGROUP_RANGE = 500.0f;
  // The range within which enemy ships and missiles will be considered a threat
  // to the carrier.
  private final float THREAT_RANGE = 1000.0f;
  // How much threat missiles add.
  private final float THREAT_PER_MISSILE_DAMAGE = 0.001f;
  // The threat at which the carrier will deactivate iff its wings are not
  // themselves engaging an
  // enemy.
  private final float BASE_THREAT_THRESHOLD = 0.5f;
  // The threat at which the carrier will unconditionally deactivate.
  private final float CRITICAL_THREAT_THRESHOLD = 2.0f;

  private ShipAPI ship;
  private ShipSystemAPI system;
  private ShipwideAIFlags flags;
  private CombatEngineAPI engine;
  private IntervalUtil interval = new IntervalUtil(0.2f, 0.3f);

  @Override
  public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
    this.ship = ship;
    this.system = system;
    this.flags = flags;
    this.engine = engine;
  }

  @Override
  public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
    interval.advance(amount);
    if (engine.isPaused() || !interval.intervalElapsed()) {
      return;
    }

    // Deactivate If the ship AI does not want to use flux, or this is the only
    // thing standing in
    // the way of the zero-flux engine boost.
    if (flags.hasFlag(AIFlags.DO_NOT_USE_FLUX)
        || (ship.getMutableStats().getZeroFluxMinimumFluxLevel().getModifiedValue() == 0.0f
            && (ship.getShield() == null || ship.getShield().isOff())) && ship.isPullBackFighters()) {
      logger.debug("Deactivating to avoid flux usage.");
      deactivate();
      return;
    }

    // If the AI wants agility, disable the system.
    if (flags.hasFlag(AIFlags.RUN_QUICKLY) || flags.hasFlag(AIFlags.TURN_QUICKLY)) {
      logger.debug("Deactivating to maintain carrier mobility.");
      deactivate();
      return;
    }

    final List<ShipAPI> fighters = getFighters();

    // Deactivate if fighters are regrouping. This checks the fighters' distance as
    // well as their
    // orders because the mobility boost can help distant fighters survive the
    // return to the
    // carrier.
    // TODO: consider situations where regrouping fighters can defend the carrier.
    float maxFighterRange = 0.0f;
    for (ShipAPI fighter : fighters) {
      maxFighterRange = Math.max(maxFighterRange, MathUtils.getDistance(ship.getLocation(), fighter.getLocation()));
    }
    if (ship.isPullBackFighters() && maxFighterRange < REGROUP_RANGE) {
      logger.debug("Deactivating because wings are regrouping.");
      deactivate();
      return;
    }

    final float threat = getThreat();

    // Deactivate if the carrier is under critical threat.
    if (threat >= CRITICAL_THREAT_THRESHOLD) {
      logger.debug("Deactivating because the carrier is under critical threat.");
      deactivate();
      return;
    }

    if (threat >= BASE_THREAT_THRESHOLD) {
      boolean fightersEngaged = false;
      for (ShipAPI fighter : fighters) {
        if (fighter.getAIFlags().hasFlag(AIFlags.IN_ATTACK_RUN)) {
          fightersEngaged = true;
          break;
        }
      }
      if (!fightersEngaged) {
        logger.debug("Deactivating because the carrier is under mild threat and fighters are not engaged.");
        deactivate();
        return;
      }
    }

    // Having got this far, we know fighters are out and the carrier is not under
    // threat--activate!
    logger.debug("Activating system.");
    activate();
  }

  // Toggles the system if it is inactive, or does nothing if it is already
  // active.
  private void activate() {
    if (!system.isActive()) {
      ship.useSystem();
    }
  }

  // Toggles the system if it is active, or does nothing if it is already
  // inactive.
  private void deactivate() {
    if (system.isActive()) {
      ship.useSystem();
    }
  }

  private float getThreat() {
    float threat = 0.0f;

    for (ShipAPI enemyShip : AIUtils.getNearbyEnemies(ship, THREAT_RANGE)) {
      switch (enemyShip.getHullSize()) {
      case CAPITAL_SHIP:
        threat += 8.0f;
        break;
      case CRUISER:
        threat += 4.0f;
        break;
      case DESTROYER:
        threat += 2.0f;
        break;
      case FRIGATE:
        threat += 2.0f;
        break;
      case FIGHTER:
        threat += (enemyShip.getWing() == null ? 0.5f : 1 / enemyShip.getWing().getSpec().getNumFighters());
        break;
      default:
        // nada
        break;
      }
    }

    for (MissileAPI missile : AIUtils.getNearbyEnemyMissiles(ship, THREAT_RANGE)) {
      threat += missile.getDamageAmount() * THREAT_PER_MISSILE_DAMAGE;
    }

    return threat;
  }

  private List<ShipAPI> getFighters() {
    // We need to do this the roundabout way to catch fighters returning for refit.
    List<ShipAPI> fighters = new ArrayList<ShipAPI>();
    for (ShipAPI otherShip : Global.getCombatEngine().getShips()) {
      if (!otherShip.isHulk() && otherShip.isFighter() && otherShip.getWing() != null
          && otherShip.getWing().getSourceShip() == ship) {
        fighters.add(otherShip);
      }
    }
    return fighters;
  }
}
