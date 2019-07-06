package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class ilk_SensorSuite extends BaseHullMod {

  public static final float SENSOR_RANGE_PERCENT = 50f;
  public static final float WEAPON_RANGE_PERCENT = 5f;
  public static final float CAMPAIGN_SENSOR_BOOST = 40f;

  @Override
  public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
    stats.getSightRadiusMod().modifyPercent(id, SENSOR_RANGE_PERCENT);
    stats.getBallisticWeaponRangeBonus().modifyPercent(id, WEAPON_RANGE_PERCENT);
    stats.getEnergyWeaponRangeBonus().modifyPercent(id, WEAPON_RANGE_PERCENT);
    stats.getSensorStrength().modifyFlat(id, CAMPAIGN_SENSOR_BOOST);
  }

  @Override
  public String getDescriptionParam(int index, HullSize hullSize) {
    if (index == 0) {
      return "" + (int) SENSOR_RANGE_PERCENT;
    }
    if (index == 1) {
      return "" + (int) WEAPON_RANGE_PERCENT;
    }
    if (index == 2) {
      return "" + (int) CAMPAIGN_SENSOR_BOOST;
    }
    return null;
  }

  public boolean isApplicableToShip(ShipAPI ship) {
    return !ship.getVariant().hasHullMod("hiressensors");
  }

  public String getUnapplicableReason(ShipAPI ship) {
    if (ship.getVariant().hasHullMod("hiressensors")) {
      return "Incompatible with High-Resolution Sensors";
    }
    return null;
  }
}
