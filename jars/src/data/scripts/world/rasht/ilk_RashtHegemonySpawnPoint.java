package data.scripts.world.rasht;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import data.scripts.world.BaseSpawnPoint;
import data.scripts.world.FleetManager;

public class ilk_RashtHegemonySpawnPoint extends BaseSpawnPoint {

    public ilk_RashtHegemonySpawnPoint(SectorAPI sector, LocationAPI location, float daysInterval, int maxFleets, SectorEntityToken anchor) {
        super(sector, location, daysInterval, maxFleets, anchor);
    }

    @Override
    public CampaignFleetAPI spawnFleet() {
        String type;
        float r = (float) Math.random();
        if (r > 0.85f) {
            type = "scout";
        } else if (r > 0.6) {
            type = "skirmish";
        } else if (r > 0.5f) {
            type = "patrol";
        } else if (r > 0.05f) {
            type = "attack";
        } else {
            type = "war";
        }

        CampaignFleetAPI fleet = getSector().createFleet("hegemony", type);
        try {
            FleetManager.createFleet(fleet, "HEG_" + type);
        } catch (NoClassDefFoundError ex) {
        }
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
