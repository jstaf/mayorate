package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

public class ilk_SprintDriveStats implements ShipSystemStatsScript {

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        stats.getMaxSpeed().modifyFlat(id, 600f * effectLevel);
        stats.getAcceleration().modifyFlat(id, 20000f * effectLevel);
        stats.getDeceleration().modifyFlat(id, 20000f * effectLevel);
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData("increased engine power", false);
        }
        return null;
    }
}
