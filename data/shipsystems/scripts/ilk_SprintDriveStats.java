package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

public class ilk_SprintDriveStats implements ShipSystemStatsScript {
               
        @Override
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		if (state == ShipSystemStatsScript.State.OUT) {
			//stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
			stats.getMaxSpeed().modifyPercent(id, 100f * effectLevel);
			//stats.getDeceleration().modifyPercent(id, 2000f * effectLevel);
			
		} else {
			stats.getMaxSpeed().modifyFlat(id, 2400f * effectLevel);
			stats.getAcceleration().modifyFlat(id, 20000f * effectLevel);
			stats.getDeceleration().modifyFlat(id, 20000f * effectLevel);

			//stats.getAcceleration().modifyPercent(id, 200f * effectLevel);
		}
	}
        
        @Override
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getMaxSpeed().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
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
