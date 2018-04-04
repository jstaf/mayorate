package data.scripts.util;

import com.fs.starfarer.api.combat.DamageType;

/** Utilities regarding damage */
public class ilk_DamageUtils {

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
        multiplier = 1f;
        break;
    }

    // Ignore the negative solution
    return (baseDamage * multiplier * strength
            + (float) Math.sqrt(baseDamage)
                * (float) Math.sqrt(strength)
                * (float)
                    Math.sqrt(
                        4.0f * armor * armor
                            + 4.0f * armor * multiplier * strength
                            + baseDamage * multiplier * multiplier * strength))
        / (2.0f * armor + 2.0f * multiplier * strength);
  }
}
