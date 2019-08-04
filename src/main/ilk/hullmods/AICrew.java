package ilk.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class AICrew extends BaseHullMod {

  public static final float AI_EFFICIENCY = 0.25f;
  public static final float AI_REPAIR_EFFICIENCY = 0.2f;

  @Override
  public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
    // weapon mods
    stats.getAutofireAimAccuracy().modifyFlat(id, AI_EFFICIENCY);

    // engine mods
    stats.getAcceleration().modifyMult(id, 1f + AI_EFFICIENCY);
    stats.getDeceleration().modifyMult(id, 1f + AI_EFFICIENCY);
    stats.getTurnAcceleration().modifyMult(id, 1f + AI_EFFICIENCY);

    // logistical penalties
    stats.getCombatEngineRepairTimeMult().modifyMult(id, 1.0f - AI_REPAIR_EFFICIENCY);
    stats.getCombatWeaponRepairTimeMult().modifyMult(id, 1.0f - AI_REPAIR_EFFICIENCY);
    stats.getRepairRatePercentPerDay().modifyMult(id, 1.0f - AI_REPAIR_EFFICIENCY);
  }

  @Override
  public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
    switch (index) {
    case 0:
      return "" + (int) (AI_EFFICIENCY * 100f);
    case 1:
      return "" + (int) (AI_REPAIR_EFFICIENCY * 100f);
    default:
      return null;
    }
  }
}
