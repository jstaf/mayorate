package data.scripts.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.combat.DefenseType;
import org.lazywizard.lazylib.combat.DefenseUtils;
import org.lwjgl.util.vector.Vector2f;

/** Utilities regarding damage */
public class ilk_DamageUtils {

  private static final Logger logger = Global.getLogger(ilk_DamageUtils.class);

  /**
   * Calculates the damage to apply against armor to approximate dealing baseDamage damage at
   * hitStrength strength, e.g. when applying beam damage.
   *
   * @param baseDamage The nominal damage, before armor reduction.
   * @param type The type of the damage.
   * @param strength The hit strength at which to calculate damage.
   * @param armor The armor at which to calculate damage.
   * @return The damage to apply.
   */
  public static float getDamageAtHitStrength(
      float baseDamage, DamageType type, float strength, float armor) {
    // TODO: handle other modifiers.
    // TODO: handle hull damage correctly.
    float multiplier = 1.0f;
    switch (type) {
      case FRAGMENTATION:
        multiplier = 0.25f;
        break;
      case HIGH_EXPLOSIVE:
        multiplier = 2.0f;
        break;
      case KINETIC:
        multiplier = 0.5f;
        break;
      case ENERGY:
      case OTHER:
        multiplier = 1.0f;
        break;
    }

    float expectedDamage =
        baseDamage
            * multiplier
            * Math.max(0.15f, strength * multiplier / (strength * multiplier + armor));

    // Wolfram swears this is correct.
    if (armor >= 340.0f * expectedDamage / 9.0f) {
      return 20.0f * expectedDamage / (3.0f * multiplier);
    } else {
      return (multiplier
                  * (float)
                      Math.sqrt(
                          expectedDamage
                              * (4.0f * armor + expectedDamage)
                              / (multiplier * multiplier))
              + expectedDamage)
          / (2.0f * multiplier);
    }
  }

  /**
   * Gets the effective armor value at a point, as used in armor-reduction calculations.
   *
   * @param ship The ship whose armor grid to use.
   * @param loc The world location to check the armor value at.
   * @return The armor value at loc.
   */
  public static float getEffectiveArmor(ShipAPI ship, Vector2f loc) {
    int[] cell = ship.getArmorGrid().getCellAtLocation(loc);
    if (cell == null) {
      return DefenseUtils.NOT_IN_GRID;
    }

    float total = 0.0f;
    for (int x = cell[0] - 2; x <= cell[0] + 2; ++x) {
      for (int y = cell[1] - 2; y <= cell[1] + 2; ++y) {
        if (Math.abs(x - cell[0]) <= 1 || Math.abs(y - cell[1]) <= 1) {
          float cellArmor = ship.getArmorGrid().getArmorValue(x, y);
          if (Math.abs(x - cell[0]) > 1 || Math.abs(y - cell[1]) > 1) {
            total += 0.5f * cellArmor;
          } else {
            total += cellArmor;
          }
        }
      }
    }
    return Math.max(total, 0.05f * ship.getArmorGrid().getArmorRating());
  }

  public static void applyDamageAtHitStrength(
      CombatEngineAPI engine,
      CombatEntityAPI entity,
      Vector2f point,
      float damageAmount,
      float damageStrength,
      DamageType damageType,
      float empAmount,
      boolean bypassShields,
      boolean dealsSoftFlux,
      java.lang.Object source) {
    if (entity instanceof ShipAPI) {
      final ShipAPI ship = (ShipAPI) entity;
      final DefenseType defense = DefenseUtils.getDefenseAtPoint(ship, point);
      if (defense == DefenseType.ARMOR || defense == DefenseType.HULL) {
        final float armor = getEffectiveArmor(ship, point);
        damageAmount = getDamageAtHitStrength(damageAmount, damageType, damageStrength, armor);
      }
    }
    engine.applyDamage(
        entity, point, damageAmount, damageType, empAmount, bypassShields, dealsSoftFlux, source);
  }
}
