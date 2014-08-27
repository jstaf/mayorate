package data.scripts.world.rasht;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Script;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.OrbitalStationAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import data.scripts.world.BaseSpawnPoint;
import data.scripts.world.FleetManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;

public class ilk_MayorateSpawnPoint extends BaseSpawnPoint {

    public ilk_MayorateSpawnPoint(SectorAPI sector, LocationAPI location, float daysInterval, int maxFleets, SectorEntityToken anchor) {
        super(sector, location, daysInterval, maxFleets, anchor);
    }

    private static StarSystemAPI getRandomSystem() {
        List<StarSystemAPI> systems = Global.getSector().getStarSystems();
        return systems.get(MathUtils.getRandom().nextInt(systems.size()));
    }

    private static OrbitalStationAPI getRandomStationInSystem(StarSystemAPI system) {
        List<OrbitalStationAPI> stations = new ArrayList();
        Iterator<SectorEntityToken> iter = system.getOrbitalStations().iterator();
        while (iter.hasNext()) {
            SectorEntityToken token = iter.next();
            if (!(token instanceof OrbitalStationAPI)) {
                continue;
            }

            OrbitalStationAPI station = (OrbitalStationAPI) token;

            if (station.getFaction().isNeutralFaction() || station.getFaction().getRelationship("mayorate") >= 0f) {
                continue;
            }

            stations.add(station);
        }

        if (stations.isEmpty()) {
            return null;
        }

        return stations.get(MathUtils.getRandom().nextInt(stations.size()));
    }

    @Override
    public CampaignFleetAPI spawnFleet() {
        String type;
        float r = (float) Math.random();
        if (r > 0.85f) {
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
        try {
            FleetManager.createFleet(fleet, "MAY_" + type);
        } catch (NoClassDefFoundError ex) {
        }
        getLocation().spawnFleet(getAnchor(), 0, 0, fleet);

        fleet.setPreferredResupplyLocation(getAnchor());

        if (type.equals("squadron") || type.equals("task force") || type.equals("battlegroup") || type.equals("detachment") || (float) Math.random() < 0.5f) {
            if ((float) Math.random() > 0.5f) {
                fleet.addAssignment(FleetAssignment.DEFEND_LOCATION, getAnchor(), 1f, new AttackScript(fleet));
            } else {
                if ((float) Math.random() > 0.5f) {
                    fleet.addAssignment(FleetAssignment.PATROL_SYSTEM, ((StarSystemAPI) getLocation()).getHyperspaceAnchor(), 30);
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

    private class AttackScript implements Script {

        private final CampaignFleetAPI fleet;

        private AttackScript(CampaignFleetAPI fleet) {
            this.fleet = fleet;
        }

        @Override
        public void run() {
            StarSystemAPI system = getRandomSystem();
            SectorEntityToken target = getRandomStationInSystem(system);

            while (true) {
                if (target == null) {
                    system = getRandomSystem();
                    target = getRandomStationInSystem(system);
                } else {
                    break;
                }
            }

            fleet.addAssignment(FleetAssignment.RAID_SYSTEM, target, 100);
            fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, getAnchor(), 1000);
        }
    }
}
