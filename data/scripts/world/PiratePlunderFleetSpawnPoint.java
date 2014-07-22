package data.scripts.world;

import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Script;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;

import data.scripts.world.BaseSpawnPoint;

@SuppressWarnings("unchecked")
public class PiratePlunderFleetSpawnPoint extends BaseSpawnPoint {

	private final SectorEntityToken convoyDestination;

	public PiratePlunderFleetSpawnPoint(SectorAPI sector, LocationAPI location, 
							float daysInterval, int maxFleets, SectorEntityToken anchor,
							SectorEntityToken convoyDestination) {
		super(sector, location, daysInterval, maxFleets, anchor);
		this.convoyDestination = convoyDestination;
	}

	private int convoyNumber = 0;
	
	@Override
	protected CampaignFleetAPI spawnFleet() {
		CampaignFleetAPI fleet = getSector().createFleet("pirates", "plunderFleet");
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
			addRandomWeapons(cargo, (int) (Math.random() * 7) + 4);
			
			if ((float) Math.random() > 0.75f) cargo.addWeapons("harpoonpod", 5);
			if ((float) Math.random() > 0.75f) cargo.addWeapons("salamanderpod", 5);
			if ((float) Math.random() > 0.75f) cargo.addWeapons("annihilatorpod", 5);
			if ((float) Math.random() > 0.75f) cargo.addWeapons("sabotpod", 5);
			if ((float) Math.random() > 0.75f) cargo.addWeapons("pilum", 5);
			if ((float) Math.random() > 0.75f) cargo.addWeapons("mark9", 1);
			if ((float) Math.random() > 0.75f) cargo.addWeapons("typhoon", 1);
		}
	
		addRandomShips(fleet, (int) (Math.random() * 7f) + 4);
		
		Script script = null;
		script = createArrivedScript(fleet);
//		if (fleet.isInCurrentLocation()) { 
//			Global.getSector().addMessage("A pirate plunder fleet has returned home from pillaging other systems");
//		}
		
		fleet.addAssignment(FleetAssignment.DELIVER_RESOURCES, convoyDestination, 1000);
		fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, convoyDestination, 1000, script);
		
		convoyNumber++;
		return fleet;
	}
	
	private Script createArrivedScript(final CampaignFleetAPI fleet) {
		return new Script() {
			public void run() {
				if (fleet.isInCurrentLocation()) {
					Global.getSector().addMessage("The pirate plunder fleet has safely delivered their goods and dispersed");
				}
			}
		};
	}
	
	private void addRandomWeapons(CargoAPI cargo, int count) {
		List weaponIds = getSector().getAllWeaponIds();
		for (int i = 0; i < count; i++) {
			String weaponId = (String) weaponIds.get((int) (weaponIds.size() * Math.random()));
			int quantity = (int)(Math.random() * 4f + 2f);
			cargo.addWeapons(weaponId, quantity);
		}
	}
	
	
	private void addRandomShips(CampaignFleetAPI fleet, int count) {
		for (int i = 0; i < count; i++) {
			if ((float) Math.random() > 0.7f) {
				String wing = (String) wings[(int) (wings.length * Math.random())];
				FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING, wing);
				fleet.getFleetData().addFleetMember(member);
				member.getRepairTracker().setMothballed(true);
			} else {
				String ship = (String) ships[(int) (ships.length * Math.random())];
				FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, ship);
				fleet.getFleetData().addFleetMember(member);
				member.getRepairTracker().setMothballed(true);
			}
		}
	}

	private static String [] ships = { 
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
									"conquest_Hull",
									};

	private static String [] wings = { 
									"broadsword_wing",
									"gladius_wing",
									"warthog_wing",
									"thunder_wing",
									"talon_wing",
									"piranha_wing",
									"mining_drone_wing"
									};
									
}



