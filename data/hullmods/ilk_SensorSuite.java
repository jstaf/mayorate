package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;


public class ilk_SensorSuite extends BaseHullMod {

	public static final float SENSOR_RANGE_PERCENT = 50f;
	public static final float WEAPON_RANGE_PERCENT = 5f;
	public static final float AUTOAIM_PERCENT = 10f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getSightRadiusMod().modifyPercent(id, SENSOR_RANGE_PERCENT);
		stats.getBallisticWeaponRangeBonus().modifyPercent(id, WEAPON_RANGE_PERCENT);
		stats.getEnergyWeaponRangeBonus().modifyPercent(id, WEAPON_RANGE_PERCENT);
		stats.getAutofireAimAccuracy().modifyPercent(id, AUTOAIM_PERCENT);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) (SENSOR_RANGE_PERCENT);
		if (index == 1) return "" + (int) (WEAPON_RANGE_PERCENT);
		return null;
	}


}
