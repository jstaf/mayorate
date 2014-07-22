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
public class MayorateSupportFleetSpawnPoint extends BaseSpawnPoint {

	private final SectorEntityToken convoyDestination;

	public MayorateSupportFleetSpawnPoint(SectorAPI sector, LocationAPI location, 
							float daysInterval, int maxFleets, SectorEntityToken anchor,
							SectorEntityToken convoyDestination) {
		super(sector, location, daysInterval, maxFleets, anchor);
		this.convoyDestination = convoyDestination;
	}

	private int convoyNumber = 0;
	
	@Override
	protected CampaignFleetAPI spawnFleet() {
		CampaignFleetAPI fleet = getSector().createFleet("mayorate", "supply_convoy");
		getLocation().spawnFleet(getAnchor(), 0, 0, fleet);
		
		CargoAPI cargo = fleet.getCargo();
		if (convoyNumber == 1) {
			cargo.addWeapons("ilk_graser", 1);
			cargo.addWeapons("ilk_graser_light", 5);
			cargo.addWeapons("ilk_nuke", 1);
			cargo.addWeapons("ilk_nuke_large", 3); 
			cargo.addWeapons("heavyneedler", 5);
			cargo.addWeapons("heavyac", 10);
			cargo.addWeapons("hephag", 10);
			cargo.addWeapons("ilk_shotgun", 5);
			cargo.addWeapons("ilk_windstalker", 5);

			
		} else {
			cargo.addWeapons("ilk_fluxtorp", 1);
			cargo.addWeapons("ilk_laserhead", 3);
			cargo.addWeapons("ilk_ppc", 5);
			cargo.addWeapons("ilk_graser_pd", 5);
			addRandomWeapons(cargo, (int) (Math.random() * 7) + 4);
			
			if ((float) Math.random() > 0.75f) cargo.addWeapons("reaper", 5);
			if ((float) Math.random() > 0.75f) cargo.addWeapons("lightdualac", 5);
			if ((float) Math.random() > 0.75f) cargo.addWeapons("lightac", 5);
			if ((float) Math.random() > 0.75f) cargo.addWeapons("lightmg", 5);
			if ((float) Math.random() > 0.75f) cargo.addWeapons("lrpdlaser", 5);
			if ((float) Math.random() > 0.75f) cargo.addWeapons("pdlaser", 5);
			if ((float) Math.random() > 0.75f) cargo.addWeapons("lightdualmg", 5);
			if ((float) Math.random() > 0.75f) cargo.addWeapons("ilk_phoenix", 5);
		}
	
		addRandomShips(fleet, (int) (Math.random() * 7f) + 4);
		
		Script script = null;
		script = createArrivedScript(fleet);
/*		if (fleet.isInCurrentLocation()) { 
			Global.getSector().addMessage("A Tri-Tachyon convoy has arrived insystem to provide logistical support for the Mayoral Navy.");
		}
*/
		
		fleet.addAssignment(FleetAssignment.DELIVER_RESOURCES, convoyDestination, 1000);
		fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, convoyDestination, 1000, script);
		
		convoyNumber++;
		return fleet;
	}
	
	private Script createArrivedScript(final CampaignFleetAPI fleet) {
		return new Script() {
			public void run() {
				if (fleet.isInCurrentLocation()) {
					Global.getSector().addMessage("A logistical support fleet has reached Ilkhanna.");
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
									"ilk_ravana_Hull",
									"ilk_del_azarchel_Hull",
									"ilk_cimeterre_Hull",
									"ilk_jamaran_Hull",
									"ilk_lilith_Hull",
									"ilk_foraker_Hull",
									"ilk_safir_Hull",
									"ilk_safir_converted_Hull",
									};

	private static String [] wings = { 
									"ilk_inanna_wing",
									"ilk_raad_wing",
									"ilk_angha_wing",
									};
									
}



