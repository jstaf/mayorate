package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.apache.log4j.Logger;

public class ilk_FighterCoprocessing extends BaseShipSystemScript {

  private static Logger logger = Global.getLogger(ilk_FighterCoprocessing.class);

  // Balance constants
  private static final float DAMAGE_MULT = 1.1f;
  private static final float RANGE_MULT = 1.25f;
  private static final float AIM_ACCURACY = 0.25f;
  private static final float WEAPON_TURN_RATE_MULT = 1.25f;
  private static final float SPEED_MULT = 1.1f;
  private static final float AGILITY_MULT = 1.25f;

  // Display constants
  public static final Object JITTER_KEY = new Object();
  private static final Color BUFF_WEAPON_GLOW_COLOR = new Color(166, 31, 37, 255);
  private static final Color BUFF_JITTER_COLOR = new Color(166, 31, 37, 75);
  private static final Color BUFF_JITTER_UNDER_COLOR = new Color(166, 31, 37, 125);
  private static final String BUFF_AUDIO = "system_targeting_feed_loop";

  @Override
  public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
    if (!(stats.getEntity() instanceof ShipAPI)) {
      return;
    }
    ShipAPI ship = (ShipAPI) stats.getEntity();

    if (effectLevel > 0) {
      final float jitterLevel = effectLevel;
      final float jitterRange = jitterLevel * 5.0f;

      modifyStats(stats, id, /* buff */ false);
      // TODO: Add a visual indication of activation on the carrier.

      for (ShipAPI fighter : getFighters(ship)) {
        if (fighter.isHulk()) continue;
        modifyStats(fighter.getMutableStats(), id, /* buff */ true);

        fighter.setWeaponGlow(effectLevel, BUFF_WEAPON_GLOW_COLOR, EnumSet.allOf(WeaponType.class));
        fighter.setJitterUnder(JITTER_KEY, BUFF_JITTER_COLOR, jitterLevel, 5, jitterRange);
        fighter.setJitter(JITTER_KEY, BUFF_JITTER_UNDER_COLOR, jitterLevel, 2, jitterRange);

        Global.getSoundPlayer()
            .playLoop(BUFF_AUDIO, ship, 1.0f, 1.0f, fighter.getLocation(), fighter.getVelocity());
      }
    }
  }

  @Override
  public void unapply(MutableShipStatsAPI stats, String id) {
    stats.getBallisticWeaponDamageMult().unmodify(id);
    stats.getEnergyWeaponDamageMult().unmodify(id);
    stats.getMissileWeaponDamageMult().unmodify(id);
    stats.getBallisticWeaponRangeBonus().unmodify(id);
    stats.getEnergyWeaponRangeBonus().unmodify(id);
    stats.getAutofireAimAccuracy().unmodify(id);
    stats.getWeaponTurnRateBonus().unmodify(id);
    stats.getMaxSpeed().unmodify(id);
    stats.getMaxTurnRate().unmodify(id);
    stats.getAcceleration().unmodify(id);
    stats.getDeceleration().unmodify(id);
    stats.getTurnAcceleration().unmodify(id);

    if (!(stats.getEntity() instanceof ShipAPI)) {
      return;
    }
    ShipAPI ship = (ShipAPI) stats.getEntity();
    for (ShipAPI fighter : getFighters(ship)) {
      if (fighter.isHulk()) continue;
      MutableShipStatsAPI fStats = fighter.getMutableStats();
      fStats.getBallisticWeaponDamageMult().unmodify(id);
      fStats.getEnergyWeaponDamageMult().unmodify(id);
      fStats.getMissileWeaponDamageMult().unmodify(id);
      fStats.getBallisticWeaponRangeBonus().unmodify(id);
      fStats.getEnergyWeaponRangeBonus().unmodify(id);
      fStats.getAutofireAimAccuracy().unmodify(id);
      fStats.getWeaponTurnRateBonus().unmodify(id);
      fStats.getMaxSpeed().unmodify(id);
      fStats.getMaxTurnRate().unmodify(id);
      fStats.getAcceleration().unmodify(id);
      fStats.getDeceleration().unmodify(id);
      fStats.getTurnAcceleration().unmodify(id);
    }
  }

  @Override
  public StatusData getStatusData(int index, State state, float effectLevel) {
    if (index == 0) {
      return new StatusData("Devoting processing power to fighters", false);
    }
    return null;
  }

  private static List<ShipAPI> getFighters(ShipAPI carrier) {
    //  We need to do this the roundabout way to catch fighters returning for refit.
    List<ShipAPI> fighters = new ArrayList<ShipAPI>();
    for (ShipAPI ship : Global.getCombatEngine().getShips()) {
      if (!ship.isFighter()) continue;
      if (ship.getWing() == null) continue;
      if (ship.getWing().getSourceShip() == carrier) {
        fighters.add(ship);
      }
    }
    return fighters;
  }

  private static void modifyStats(MutableShipStatsAPI stats, String id, boolean buff) {
    stats.getBallisticWeaponDamageMult().modifyMult(id, buff ? DAMAGE_MULT : 1.0f / DAMAGE_MULT);
    stats.getEnergyWeaponDamageMult().modifyMult(id, buff ? DAMAGE_MULT : 1.0f / DAMAGE_MULT);
    stats.getMissileWeaponDamageMult().modifyMult(id, buff ? DAMAGE_MULT : 1.0f / DAMAGE_MULT);
    stats.getBallisticWeaponRangeBonus().modifyMult(id, buff ? RANGE_MULT : 1.0f / RANGE_MULT);
    stats.getEnergyWeaponRangeBonus().modifyMult(id, buff ? RANGE_MULT : 1.0f / RANGE_MULT);
    stats.getAutofireAimAccuracy().modifyFlat(id, buff ? AIM_ACCURACY : -AIM_ACCURACY);
    stats
        .getWeaponTurnRateBonus()
        .modifyMult(id, buff ? WEAPON_TURN_RATE_MULT : 1 / WEAPON_TURN_RATE_MULT);
    stats.getMaxSpeed().modifyMult(id, buff ? SPEED_MULT : 1.0f / SPEED_MULT);
    stats.getMaxTurnRate().modifyMult(id, buff ? SPEED_MULT : 1.0f / SPEED_MULT);
    stats.getAcceleration().modifyMult(id, buff ? AGILITY_MULT : 1.0f / AGILITY_MULT);
    stats.getDeceleration().modifyMult(id, buff ? AGILITY_MULT : 1.0f / AGILITY_MULT);
    stats.getTurnAcceleration().modifyMult(id, buff ? AGILITY_MULT : 1.0f / AGILITY_MULT);
  }
}
