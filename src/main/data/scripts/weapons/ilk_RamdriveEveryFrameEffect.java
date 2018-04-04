package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import org.apache.log4j.Logger;

/** Runs every frame to properly manage particle fx and such */
public class ilk_RamdriveEveryFrameEffect implements EveryFrameWeaponEffectPlugin {

  static Logger logger = Global.getLogger(ilk_RamdriveEveryFrameEffect.class);
  private boolean initialized = false;
  private boolean hasfired = false;
  private float maxCharge = 0f;
  private float postFireDuration = 0f;

  @Override
  public void advance(float delta, CombatEngineAPI engine, WeaponAPI weapon) {
    if (!initialized) {
      init(weapon);
    }

    switch (WeaponState.getState(weapon)) {
      case Charging:
        maxCharge = weapon.getChargeLevel();
        break;
      case Cooldown:
        if (!hasfired) {
          fire(engine, weapon);
        }
        postFireDuration += delta;
        break;
      case Idle:
        hasfired = false;
        break;
    }
  }

  /**
   * Run some initialization code the first time this script runs.
   *
   * @param weapon
   */
  private void init(WeaponAPI weapon) {
    // ShipAPI parent = weapon.getShip();
    weapon.setAmmo(1000);
    initialized = true;
  }

  private void fire(CombatEngineAPI engine, WeaponAPI weapon) {
    postFireDuration = 0f;
    hasfired = true;
  }
}
