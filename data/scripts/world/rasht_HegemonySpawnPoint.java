package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;

import data.scripts.world.BaseSpawnPoint;

public class rasht_HegemonySpawnPoint extends BaseSpawnPoint {

	public rasht_HegemonySpawnPoint(SectorAPI sector, LocationAPI location, 
							float daysInterval, int maxFleets, SectorEntityToken anchor) {
		super(sector, location, daysInterval, maxFleets, anchor);
	}

	@Override
	public CampaignFleetAPI spawnFleet() {
		//if ((float) Math.random() < 0.5f) return null;
		
		String type = null;
		float r = (float) Math.random();
		if (r > .85f) {
			type = "heg_scout";
		} else if (r > 0.6) {
			type = "heg_flotilla";
		} else if (r > 0.5f) {
			type = "patrol";
		} else if (r > 0.05f) {
			type = "heg_patrol2";
		} else {
			type = "invasion";
		}
		
		
		
		
		CampaignFleetAPI fleet = getSector().createFleet("hegemony", type);
		getLocation().spawnFleet(getAnchor(), 0, 0, fleet);
		
		fleet.setPreferredResupplyLocation(getAnchor());
		
		StarSystemAPI rasht = Global.getSector().getStarSystem("Rasht");
		if ((float) Math.random() > 0.6f) {
			fleet.addAssignment(FleetAssignment.RAID_SYSTEM, rasht.getHyperspaceAnchor(), 30);
			fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, getAnchor(), 1000);
		} else {
			fleet.addAssignment(FleetAssignment.RAID_SYSTEM, null, 30);
			fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, getAnchor(), 1000);
		}
		
		return fleet;
	}

}
