package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class ilk_InterceptionOptimization extends BaseHullMod {

  public static final float FIGHTER_DAMAGE_BONUS = 50.0f;

  @Override
  public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
    stats.getDamageToFighters().modifyPercent(id, FIGHTER_DAMAGE_BONUS);
  }
}
