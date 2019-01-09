package data.scripts.weapons;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import org.apache.log4j.Logger;

import data.scripts.plugins.beamRenderer.ilk_BeamRendererPlugin;
import data.scripts.plugins.beamRenderer.ilk_BeamSpec;

/** Runs every frame to properly manage particle fx and such */
public class ilk_RamdriveEveryFrameEffect implements EveryFrameWeaponEffectPlugin {

  //static Logger logger = Global.getLogger(ilk_RamdriveEveryFrameEffect.class);

  private final Color RAMDRIVE_COLOR = new Color(244, 41, 109);
  private static final float PARTICLE_SPAWN_DELAY = 0.05f;
  private float particleDelta = 0;

  private boolean initialized = false;
  private boolean hasfired = false;
  private float maxCharge = 0f;
  private float postFireDuration = 0f;

  @Override
  public void advance(float delta, CombatEngineAPI engine, WeaponAPI weapon) {
    if (engine == null || engine.isPaused()) {
      return;
    }

    if (!initialized) {
      init(weapon);
    }

    switch (WeaponState.getState(weapon)) {
      case Charging:
        maxCharge = weapon.getChargeLevel();

        particleDelta += delta;
        if (particleDelta >= PARTICLE_SPAWN_DELAY) {
          particleDelta = 0f;
          engine.addSmoothParticle(
            weapon.getLocation(),
            weapon.getShip().getVelocity(),
            15f,
            0.6f,
            1.0f,
            RAMDRIVE_COLOR);
        }
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
      default:
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
    ilk_BeamRendererPlugin.addBeam(
      new ilk_BeamSpec(
        engine,
        weapon.getShip(),
        weapon.getLocation(),
        weapon.getRange(),
        weapon.getArcFacing(),
        1000f,
        DamageType.HIGH_EXPLOSIVE,
        500f,
        1f, 0.05f, 0.2f,
        "beams", "ilk_fakeBeamFX",
        40,
        RAMDRIVE_COLOR)
    );
  }
}
