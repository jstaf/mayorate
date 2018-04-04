package data.shipsystems.scripts.ai;

import com.fs.starfarer.api.combat.*;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

/** Created by Jeff on 2015-08-08. */
public class ilk_MinelayerAI implements ShipSystemAIScript {

  private List<ShipAPI> enemies;
  private float enemyCount = 0;
  private boolean hasOrders = false;
  private CombatTaskManagerAPI manager;
  CombatFleetManagerAPI.AssignmentInfo orders;

  private float interval = 0f;
  private static final float THRESHOLD = 0.25f;

  // initialization variables
  private CombatEngineAPI engine;
  private ShipAPI ship;
  private ShipSystemAPI system;
  private ShipwideAIFlags aiFlags;
  private boolean objectivesExist;

  @Override
  public void init(
      ShipAPI shipAPI,
      ShipSystemAPI shipSystemAPI,
      ShipwideAIFlags shipwideAIFlags,
      CombatEngineAPI combatEngineAPI) {
    engine = combatEngineAPI;
    ship = shipAPI;
    system = shipSystemAPI;
    aiFlags = shipwideAIFlags;
    objectivesExist = !engine.getObjectives().isEmpty();
    manager =
        engine.getFleetManager(ship.getOwner()).getTaskManager(false); // wtf is the ally argument?
  }

  @Override
  public void advance(float v, Vector2f vector2f, Vector2f vector2f1, ShipAPI shipAPI) {
    // should we even be running this AI script?
    if (engine == null) return;
    interval += v;
    if (interval < THRESHOLD) return;
    if (!AIUtils.canUseSystemThisFrame(ship)) return;
    if (ship.getSystem().getAmmo() == 0) return;
    if (ship.isHoldFire()) return;

    // if we've gotten here, we need to evaluate threats and our current situation

    // step 1: are we desperate?
    if (ship.isRetreating()) {
      // deploy mines if there's an enemy anywhere within sight! (hopefully this will slow them the
      // fuck down.)
      enemies = AIUtils.getNearbyEnemies(ship, 1200f);
      if (!enemies.isEmpty()) ship.useSystem();
    }

    // if running away/backing off and enemies are nearby, deploy mines for breathing room
    if (aiFlags.hasFlag(ShipwideAIFlags.AIFlags.BACK_OFF)
        || aiFlags.hasFlag(ShipwideAIFlags.AIFlags.RUN_QUICKLY)) {
      enemies = AIUtils.getNearbyEnemies(ship, 800f);
      if (!enemies.isEmpty()) {
        // evaluate nearby targets
        for (ShipAPI enemy : enemies) {
          // are they targeting us?
          if (enemy.getShipTarget() == null) continue;
          if (enemy.getShipTarget().equals(ship)) {
            ship.useSystem();
            break;
          }
        }
      }
    }

    // mine nearby objectives / order locations if they exist
    // are we defending something
    orders = manager.getAssignmentFor(ship);
    if (orders != null) {
      if (orders.getType().equals(CombatAssignmentType.DEFEND)
          || orders.getType().equals(CombatAssignmentType.CONTROL)) {
        staticObjectiveEval(orders.getTarget().getLocation());
      }
    }
    if (objectivesExist) {
      staticObjectiveEval(AIUtils.getNearestObjective(ship).getLocation());
    }

    // if there's a chance to absolutely gank someone or we are under pressure
    enemies = AIUtils.getNearbyEnemies(ship, 900f);
    enemyCount = 0;
    for (ShipAPI enemy : enemies) {
      if (MathUtils.getDistance(ship, enemy) < 500) {
        if (enemy.getFluxTracker().isOverloadedOrVenting()
            || (enemy.getShield() == null)
            || (enemy.getShield().isOff())) {
          ship.useSystem();
          break;
        }
      }
      // are we getting swarmed?
      if (!enemy.isFighter() && !enemy.isDrone()) {
        enemyCount++;
      } else {
        enemyCount += 0.2;
      }
      if (enemyCount >= 3 && (ship.getFluxTracker().getFluxLevel() > 0.4f)) {
        ship.useSystem();
        break;
      }
    }
  }

  private void staticObjectiveEval(Vector2f objectiveLoc) {
    if (MathUtils.getDistance(ship, objectiveLoc) < 500f) {
      // have we mined this objective obsessively yet?
      if ((system.getAmmo() > 1)
          && (CombatUtils.getMissilesWithinRange(objectiveLoc, 500f).size() < 50)) {
        // NEEDS MORE MINES
        ship.useSystem();
      }
    }
  }
}
