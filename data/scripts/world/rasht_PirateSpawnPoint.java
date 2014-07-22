package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;

import data.scripts.world.BaseSpawnPoint;

public class rasht_PirateSpawnPoint extends BaseSpawnPoint {

	public rasht_PirateSpawnPoint(SectorAPI sector, LocationAPI location, 
							float daysInterval, int maxFleets, SectorEntityToken anchor) {
		super(sector, location, daysInterval, maxFleets, anchor);
	}

	@Override
	public CampaignFleetAPI spawnFleet() {
		//if ((float) Math.random() < 0.5f) return null;
		
		String type = null;
		float r = (float) Math.random();
		if (r > .9f) {
			type = "scout";
		} else if (r > 0.7) {
			type = "raiders1";
		} else if (r > 0.5f) {
			type = "raiders2";
		} else if (r > 0.35f) {
			type = "raiders3";
		} else if (r > 0.2f) {
			type = "attackFleet";
		} else if (r > 0.1f) {
			type = "carrierGroup";
		} else {
			type = "armada";
		}
		
		
		CampaignFleetAPI fleet = getSector().createFleet("pirates", type);
		getLocation().spawnFleet(getAnchor(), 0, 0, fleet);
		
		fleet.setPreferredResupplyLocation(getAnchor());
		
		
		StarSystemAPI corvus = Global.getSector().getStarSystem("Corvus");
		StarSystemAPI askonia = Global.getSector().getStarSystem("Askonia");
		if (type.equals("scout") || type.equals("raiders1") || type.equals("raiders2") ||
						type.equals("raiders3") || type.equals("attackFleet") || (float) Math.random() < 0.5f) {
			if ((float) Math.random() > 0.4f) {
				if ((float) Math.random() > 0.5f) {
					fleet.addAssignment(FleetAssignment.RAID_SYSTEM, askonia.getHyperspaceAnchor(), 30);
					fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, getAnchor(), 1000);
				} else {
					fleet.addAssignment(FleetAssignment.RAID_SYSTEM, askonia.getStar(), 30);
					fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, getAnchor(), 1000);
				}
			} else {
				if ((float) Math.random() > 0.5f) {
					fleet.addAssignment(FleetAssignment.RAID_SYSTEM, corvus.getHyperspaceAnchor(), 30);
					fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, getAnchor(), 1000);
				} else {
					fleet.addAssignment(FleetAssignment.RAID_SYSTEM, corvus.getStar(), 30);
					fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, getAnchor(), 1000);
				}
			}
		} else {
			fleet.addAssignment(FleetAssignment.DEFEND_LOCATION, getAnchor(), 20);
			fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, getAnchor(), 1000);
		}
		
		return fleet;
	}

}
