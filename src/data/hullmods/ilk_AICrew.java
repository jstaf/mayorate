package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class ilk_AICrew extends BaseHullMod {

    public static final float PERCENT_AI_CREW = 50f;
    public static final float AI_EFFICIENCY = 15f;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        //crew mods
        stats.getCrewLossMult().modifyPercent(id, PERCENT_AI_CREW);
        stats.getMinCrewMod().modifyPercent(id, PERCENT_AI_CREW);
        stats.getMaxCrewMod().modifyPercent(id, PERCENT_AI_CREW);

        //weapon mods
        stats.getAutofireAimAccuracy().modifyFlat(id, AI_EFFICIENCY / 10);

        //engine mods
        stats.getAcceleration().modifyPercent(id, 100 + AI_EFFICIENCY);
        stats.getDeceleration().modifyPercent(id, 100 + AI_EFFICIENCY);
        stats.getTurnAcceleration().modifyPercent(id, 100 + AI_EFFICIENCY);
        stats.getMaxSpeed().modifyPercent(id, 100 + AI_EFFICIENCY);

        //logistical penalties
        stats.getRepairRatePercentPerDay().modifyPercent(id, PERCENT_AI_CREW);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        switch (index) {
            case 0:
                return "" + (int) PERCENT_AI_CREW;
            case 1:
                return "" + (int) AI_EFFICIENCY;
            case 2:
                return "" + (int) PERCENT_AI_CREW;
            default:
                return null;
        }
    }
}
