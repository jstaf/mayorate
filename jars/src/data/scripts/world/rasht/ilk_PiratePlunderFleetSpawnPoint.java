package data.scripts.world.rasht;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import data.scripts.world.BaseSpawnPoint;
import data.scripts.world.FleetManager;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;

public class ilk_PiratePlunderFleetSpawnPoint extends BaseSpawnPoint {

    private final SectorEntityToken convoyDestination;

    public ilk_PiratePlunderFleetSpawnPoint(SectorAPI sector, LocationAPI location, float daysInterval, int maxFleets, SectorEntityToken anchor, SectorEntityToken convoyDestination) {
        super(sector, location, daysInterval, maxFleets, anchor);
        this.convoyDestination = convoyDestination;
    }

    private int convoyNumber = 0;

    @Override
    protected CampaignFleetAPI spawnFleet() {
        CampaignFleetAPI fleet = getSector().createFleet("pirates", "plunderFleet");
        try {
            FleetManager.createFleet(fleet, "PIR_plunderFleet");
        } catch (NoClassDefFoundError ex) {
        }
        getLocation().spawnFleet(getAnchor(), 0, 0, fleet);

        CargoAPI cargo = fleet.getCargo();
        if (convoyNumber == 1) {
            cargo.addWeapons("salamanderpod", 5);
            cargo.addWeapons("sabotpod", 5);
            cargo.addWeapons("annihilatorpod", 5);
            cargo.addWeapons("heavyneedler", 5);
            cargo.addWeapons("heavyac", 10);
            cargo.addWeapons("hephag", 10);
            cargo.addWeapons("mark9", 10);
        } else {
            addRandomWeapons(cargo, MathUtils.getRandom().nextInt(7) + 4);

            if ((float) Math.random() > 0.75f) {
                cargo.addWeapons("harpoonpod", 5);
            }
            if ((float) Math.random() > 0.75f) {
                cargo.addWeapons("salamanderpod", 5);
            }
            if ((float) Math.random() > 0.75f) {
                cargo.addWeapons("annihilatorpod", 5);
            }
            if ((float) Math.random() > 0.75f) {
                cargo.addWeapons("sabotpod", 5);
            }
            if ((float) Math.random() > 0.75f) {
                cargo.addWeapons("pilum", 5);
            }
            if ((float) Math.random() > 0.75f) {
                cargo.addWeapons("mark9", 1);
            }
            if ((float) Math.random() > 0.75f) {
                cargo.addWeapons("typhoon", 1);
            }
        }

        addRandomShips(fleet, MathUtils.getRandom().nextInt(7) + 4);

        fleet.addAssignment(FleetAssignment.DELIVER_RESOURCES, convoyDestination, 1000);
        fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, convoyDestination, 1000);

        convoyNumber++;
        return fleet;
    }

    private void addRandomWeapons(CargoAPI cargo, int count) {
        List<String> weaponIds = getSector().getAllWeaponIds();
        for (int i = 0; i < count; i++) {
            String weaponId = weaponIds.get(MathUtils.getRandom().nextInt(weaponIds.size()));
            int quantity = (int) (Math.random() * 4f + 2f);
            cargo.addWeapons(weaponId, quantity);
        }
    }

    private void addRandomShips(CampaignFleetAPI fleet, int count) {
        for (int i = 0; i < count; i++) {
            if ((float) Math.random() > 0.7f) {
                String wing = wings[MathUtils.getRandom().nextInt(wings.length)];
                FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING, wing);
                fleet.getFleetData().addFleetMember(member);
                member.getRepairTracker().setMothballed(true);
            } else {
                String ship = ships[MathUtils.getRandom().nextInt(ships.length)];
                FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, ship);
                fleet.getFleetData().addFleetMember(member);
                member.getRepairTracker().setMothballed(true);
            }
        }
    }

    private static final String[] ships = {
        "wolf_Hull",
        "hound_Hull",
        "lasher_Hull",
        "vigilance_Hull",
        "dram_Hull",
        "tarsus_Hull",
        "hammerhead_Hull",
        "condor_Hull",
        "enforcer_Hull",
        "buffalo_Hull",
        "buffalo2_Hull",
        "venture_Hull",
        "dominator_Hull",
        "conquest_Hull",};

    private static final String[] wings = {
        "broadsword_wing",
        "gladius_wing",
        "warthog_wing",
        "thunder_wing",
        "talon_wing",
        "piranha_wing",
        "mining_drone_wing"
    };
}
