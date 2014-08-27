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

public class ilk_RashtPirateSpawnPoint extends BaseSpawnPoint {

    public ilk_RashtPirateSpawnPoint(SectorAPI sector, LocationAPI location, float daysInterval, int maxFleets, SectorEntityToken anchor) {
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

            if (station.getFaction().isNeutralFaction() || station.getFaction().getRelationship("pirates") >= 0f) {
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
        if (r > 0.9f) {
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
        try {
            FleetManager.createFleet(fleet, "PIR_" + type);
        } catch (NoClassDefFoundError ex) {
        }
        getLocation().spawnFleet(getAnchor(), 0, 0, fleet);

        fleet.setPreferredResupplyLocation(getAnchor());

        if (type.equals("scout") || type.equals("raiders1") || type.equals("raiders2") || type.equals("raiders3") || type.equals("attackFleet") || (float) Math.random() < 0.5f) {
            fleet.addAssignment(FleetAssignment.DEFEND_LOCATION, getAnchor(), 1f, new AttackScript(fleet));
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
