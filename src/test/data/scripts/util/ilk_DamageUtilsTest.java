package data.scripts;

import static com.google.common.truth.Truth.assertThat;

import com.fs.starfarer.api.combat.DamageType;
import data.scripts.util.ilk_DamageUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ilk_DamageUtilsTest {

  private float applyArmorReduction(float damage, float strength, float armor) {
    return damage * strength / (strength + armor);
  }

  @Test
  public void testGetDamageAtHitStrengthHighArmor() {
    final float baseDamage = 10;
    final float strength = 100;
    final float armor = 1000;
    final float modifiedDamage =
        ilk_DamageUtils.getDamageAtHitStrength(baseDamage, DamageType.ENERGY, strength, armor);
    assertThat(applyArmorReduction(baseDamage, strength, armor))
        .isWithin(.01f)
        .of(applyArmorReduction(modifiedDamage, modifiedDamage, armor));
  }

  @Test
  public void testGetDamageAtHitStrengthLowArmor() {
    final float baseDamage = 10;
    final float strength = 100;
    final float armor = 1;
    final float modifiedDamage =
        ilk_DamageUtils.getDamageAtHitStrength(baseDamage, DamageType.ENERGY, strength, armor);
    assertThat(applyArmorReduction(baseDamage, strength, armor))
        .isWithin(.01f)
        .of(applyArmorReduction(modifiedDamage, modifiedDamage, armor));
  }

  @Test
  public void testGetDamageAtHitStrengthHeDamage() {
    final float baseDamage = 10;
    final float strength = 100;
    final float armor = 50;
    final float modifiedDamage =
        ilk_DamageUtils.getDamageAtHitStrength(
            baseDamage, DamageType.HIGH_EXPLOSIVE, strength, armor);
    assertThat(applyArmorReduction(2 * baseDamage, 2 * strength, armor))
        .isWithin(.01f)
        .of(applyArmorReduction(2 * modifiedDamage, 2 * modifiedDamage, armor));
  }

  @Test
  public void testGetDamageAtHitStrengthKineticDamage() {
    final float baseDamage = 10;
    final float strength = 100;
    final float armor = 50;
    final float modifiedDamage =
        ilk_DamageUtils.getDamageAtHitStrength(baseDamage, DamageType.KINETIC, strength, armor);
    assertThat(applyArmorReduction(0.5f * baseDamage, 0.5f * strength, armor))
        .isWithin(.01f)
        .of(applyArmorReduction(0.5f * modifiedDamage, 0.5f * modifiedDamage, armor));
  }
}
