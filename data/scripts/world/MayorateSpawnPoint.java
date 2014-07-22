package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;

import data.scripts.world.BaseSpawnPoint;

public class MayorateSpawnPoint extends BaseSpawnPoint {

	public MayorateSpawnPoint(SectorAPI sector, LocationAPI location, 
							float daysInterval, int maxFleets, SectorEntityToken anchor) {
		super(sector, location, daysInterval, maxFleets, anchor);
	}

	@Override
	public CampaignFleetAPI spawnFleet() {
		//if ((float) Math.random() < 0.5f) return null;
		
		String type = null;
		float r = (float) Math.random();
		if (r > .85f) {
			type = "detachment1";
		} else if (r > 0.7f) {
			type = "detachment2";
		} else if (r > 0.4f) {
			type = "squadron";
		} else if (r > 0.2f) {
			type = "task_force";
		} else if (r > 0.1f) {
			type = "battlegroup";
		} else {
			type = "fleet";
		}
		
		
		CampaignFleetAPI fleet = getSector().createFleet("mayorate", type);
		getLocation().spawnFleet(getAnchor(), 0, 0, fleet);
		
		fleet.setPreferredResupplyLocation(getAnchor());
		
		StarSystemAPI corvus = Global.getSector().getStarSystem("Corvus");
		if (type.equals("squadron") || type.equals("task force") || type.equals("battlegroup") ||
						type.equals("detachment") || (float) Math.random() < 0.5f) {
			if ((float) Math.random() > 0.5f) {
				if ((float) Math.random() > 0.5f) {
					fleet.addAssignment(FleetAssignment.RAID_SYSTEM, corvus.getHyperspaceAnchor(), 30);
					fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, getAnchor(), 1000);
				} else {
					fleet.addAssignment(FleetAssignment.RAID_SYSTEM, corvus.getStar(), 30);
					fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, getAnchor(), 1000);
				}
			} else {
				if ((float) Math.random() > 0.5f) {
					fleet.addAssignment(FleetAssignment.PATROL_SYSTEM, ((StarSystemAPI)getLocation()).getHyperspaceAnchor(), 30);
					fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, getAnchor(), 1000);
				} else {
					fleet.addAssignment(FleetAssignment.RAID_SYSTEM, null, 10);
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
