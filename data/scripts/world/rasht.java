package data.scripts.world;

import java.awt.Color;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.OrbitAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.CargoAPI.CrewXPLevel;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;

public class rasht implements SectorGeneratorPlugin 
//it took me THREE HOURS to figure out that I needed to add "implements sectorgeneratorplugin" to the last line so it would actually compile... FML.
{

	public void generate(SectorAPI sector) 
	{
		
		StarSystemAPI system = sector.createStarSystem("Rasht");
		LocationAPI hyper = Global.getSector().getHyperspace();
		
		system.setBackgroundTextureFilename("graphics/ilk/backgrounds/ilk_background2.jpg");
		
		// create the star and generate the hyperspace anchor for this system
		PlanetAPI star = system.initStar("star_red_dwarf", // id in planets.json
										 400f, 		// radius (in pixels at default zoom)
										 -2200, -3500);   // location in hyperspace
		
		system.setLightColor(new Color(183, 98, 84)); // light color in entire system, affects all entities
		
		/*
		 * addPlanet() parameters:
		 * 1. What the planet orbits (orbit is always circular)
		 * 2. Name
		 * 3. Planet type id in planets.json
		 * 4. Starting angle in orbit, i.e. 0 = to the right of the star
		 * 5. Planet radius, pixels at default zoom
		 * 6. Orbit radius, pixels at default zoom
		 * 7. Days it takes to complete an orbit. 1 day = 10 seconds.
		 */
		PlanetAPI ilk1 = system.addPlanet(star, "Ilkhanna", "ilk_ilkhanna", 180, 175, 3800, 200);
		PlanetAPI ilk1_1 = system.addPlanet(ilk1, "Mun", "barren", 150, 80, 1200, 42);
		PlanetAPI ilk2 = system.addPlanet(star, "Inir", "rocky_metallic", 330, 120, 1000, 30);
		PlanetAPI ilk3 = system.addPlanet(star, "Sindral", "rocky_ice", 20, 75, 9500, 450);
		
		/*
		 * addAsteroidBelt() parameters:
		 * 1. What the belt orbits
		 * 2. Number of asteroids
		 * 3. Orbit radius
		 * 4. Belt width
		 * 6/7. Range of days to complete one orbit. Value picked randomly for each asteroid. 
		 */
		system.addAsteroidBelt(star, 400, 6200, 300, 100, 200);
		system.addAsteroidBelt(star, 100, 7100, 100, 60, 110);
		system.addAsteroidBelt(star, 200, 1850, 600, 100, 130);

		
		/*
		 * addRingBand() parameters:
		 * 1. What it orbits
		 * 2. Category under "graphics" in settings.json
		 * 3. Key in category
		 * 4. Width of band within the texture
		 * 5. Index of band
		 * 6. Color to apply to band
		 * 7. Width of band (in the game)
		 * 8. Orbit radius (of the middle of the band)
		 * 9. Orbital period, in days
		 */
		 
		system.addRingBand(star, "misc", "rings1", 256f, 2, Color.white, 256f, 2050, 120f);
		system.addRingBand(star, "misc", "rings1", 256f, 3, Color.white, 256f, 1800, 150f);
		system.addRingBand(star, "misc", "rings1", 256f, 3, Color.white, 256f, 2100, 110f);
		system.addRingBand(star, "misc", "rings1", 20f, 2, Color.white, 20f, 2300, 110f);

		
		system.addRingBand(star, "misc", "rings1", 256f, 1, Color.white, 256f, 6000, 200f);
		system.addRingBand(star, "misc", "rings1", 256f, 0, Color.white, 280f, 6300, 160f);
 		
		system.addRingBand(star, "misc", "rings1", 256f, 2, Color.white, 256f, 6900, 110f);

		
		
		ilk1.setCustomDescriptionId("planet_Ilkhanna");
		ilk1_1.setCustomDescriptionId("planet_Mun");
		ilk2.setCustomDescriptionId("planet_Inir");
		ilk3.setCustomDescriptionId("planet_Sindral");
		
		JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("L2 Jump Point");
		OrbitAPI orbit = Global.getFactory().createCircularOrbit(ilk1, 135, 600, 30);
		jumpPoint.setOrbit(orbit);
		jumpPoint.setRelatedPlanet(ilk1);
		jumpPoint.setStandardWormholeToHyperspaceVisual();
		system.addEntity(jumpPoint);
		
		system.autogenerateHyperspaceJumpPoints(true, true);
		
		//add stations and cargo
		SectorEntityToken ilk_station = system.addOrbitalStation(ilk1, 45, 300, 50, "Port Authority", "mayorate");
		initIlk_StationCargo(sector, ilk_station);
		
		SectorEntityToken ras_PirateStation = system.addOrbitalStation(ilk3, 180, 300, 30, "Freeport", "pirates");
		ras_PirateStationCargo(sector, ras_PirateStation);
		
		//spawns fleets
		MayorateSpawnPoint mayorateSpawn = new MayorateSpawnPoint(sector, system, 2, 20, ilk1);
		system.addScript(mayorateSpawn);
		for (int i = 0; i < 3; i++)
			mayorateSpawn.spawnFleet();

		rasht_PirateSpawnPoint ras_PirateSpawn = new rasht_PirateSpawnPoint(sector, system, 5, 20, ilk3);
		system.addScript(ras_PirateSpawn);
		for (int i = 0; i < 3; i++)
			ras_PirateSpawn.spawnFleet();
		
		SectorEntityToken hegemony_raider_token = system.createToken(8000, 9000);
		rasht_HegemonySpawnPoint ras_HegemonySpawn = new rasht_HegemonySpawnPoint(sector, system, 4, 10, hegemony_raider_token);
		system.addScript(ras_HegemonySpawn);
		for (int i = 0; i < 4; i++)
			ras_HegemonySpawn.spawnFleet();
			
		MayorateSupportFleetSpawnPoint mayorate_supply_fleet = new MayorateSupportFleetSpawnPoint(sector, hyper, 17, 1, hyper.createToken(-4000, -6500), ilk_station);
		system.addScript(mayorate_supply_fleet);
		
		PiratePlunderFleetSpawnPoint ras_plunder_fleet = new PiratePlunderFleetSpawnPoint(sector, hyper, 20, 1, hyper.createToken(3000, -2000), ras_PirateStation);
		system.addScript(ras_plunder_fleet);
		
		//gives relationship advice
		FactionAPI mayorate = sector.getFaction("mayorate");
		mayorate.setRelationship("player", 0);
		mayorate.setRelationship("tritachyon", 1); 
		mayorate.setRelationship("hegemony", -1); 
		mayorate.setRelationship("pirates", 0); 
		mayorate.setRelationship("independent", 0); 
		
		FactionAPI player = sector.getFaction("player");
		player.setRelationship("pirates", 0);
		
	}
		//defines cargo for mayorate station
	private void initIlk_StationCargo(SectorAPI sector, SectorEntityToken station) {
		CargoAPI cargo = station.getCargo();

		List weaponIds = sector.getAllWeaponIds();
		
		//resources
		cargo.addCrew(CrewXPLevel.REGULAR, 50);
		cargo.addCrew(CrewXPLevel.GREEN, 500);
		cargo.addMarines(100);
		cargo.addSupplies(1300);
		cargo.addFuel(1600);
		
		//strike
		cargo.addWeapons("bomb", 25);
		cargo.addWeapons("reaper", 11);
		cargo.addWeapons("typhoon", 4);
		cargo.addWeapons("ilk_graser", 3);
		cargo.addWeapons("ilk_graser_light", 10);
		cargo.addWeapons("ilk_graser_pd", 30);
		cargo.addWeapons("ilk_ppc", 7);
		
		//Support
		cargo.addWeapons("lightac", 25);
		cargo.addWeapons("lightmg", 30);
		cargo.addWeapons("lightneedler", 7);
		cargo.addWeapons("annihilator", 10);
		cargo.addWeapons("harpoon_single", 12); //medium
		cargo.addWeapons("ilk_laserhead", 4);
		cargo.addWeapons("ilk_nuke", 1);
		cargo.addWeapons("ilk_nuke_large", 3);
		
		//assault
		cargo.addWeapons("ilk_fluxtorp", 1);
		cargo.addWeapons("lightmortar", 20);
		cargo.addWeapons("miningblaster", 2);
		cargo.addWeapons("hephag", 3);
		cargo.addWeapons("ilk_shotgun", 3);
		
		//PD
		cargo.addWeapons("flak", 2);
		cargo.addWeapons("swarmer", 5);
		cargo.addWeapons("lrpdlaser", 7);
		cargo.addWeapons("pdlaser", 15);
		
		cargo.addWeapons("annihilatorpod", 2); //medium
		cargo.addWeapons("pilum", 2); //medium
		cargo.addWeapons("mark9", 2); //large
		
		cargo.addWeapons("sabot", 5);
		cargo.addWeapons("ilk_windstalker", 5);
		cargo.addWeapons("lightdualmg", 10);
		cargo.addWeapons("heavyac", 10);
		cargo.addWeapons("ilk_phoenix", 20);
		
		//ships
		cargo.addMothballedShip(FleetMemberType.SHIP, "ilk_lilith_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "ilk_lilith_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "ilk_lilith_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "ilk_lilith_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "ilk_safir_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "ilk_safir_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "ilk_safir_converted_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "ilk_foraker_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "ilk_cimeterre_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "ilk_del_azarchel_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "ilk_ravana_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "ilk_jamaran_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "ilk_jamaran_Hull", null);
		cargo.addMothballedShip(FleetMemberType.FIGHTER_WING, "ilk_raad_wing", null);
		cargo.addMothballedShip(FleetMemberType.FIGHTER_WING, "ilk_angha_wing", null);
		cargo.addMothballedShip(FleetMemberType.FIGHTER_WING, "ilk_inanna_wing", null);
		cargo.addMothballedShip(FleetMemberType.FIGHTER_WING, "ilk_inanna_wing", null);
		cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "crig_Hull"));
		cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "crig_Hull"));
		cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "ox_Hull"));
		cargo.getMothballedShips().addFleetMember(Global.getFactory().createFleetMember(FleetMemberType.SHIP, "ox_Hull"));
		
	}
	
	private void ras_PirateStationCargo(SectorAPI sector, SectorEntityToken station) {
		CargoAPI cargo = station.getCargo();

		List weaponIds = sector.getAllWeaponIds();
		// cargo.addCrew(CrewXPLevel.ELITE, 25);
		cargo.addCrew(CrewXPLevel.VETERAN, 50);
		cargo.addCrew(CrewXPLevel.REGULAR, 100);
		cargo.addCrew(CrewXPLevel.GREEN, 100);
		cargo.addMarines(100);
		cargo.addSupplies(1000);
		cargo.addFuel(100);
		
		//strike
		cargo.addWeapons("bomb", 15);
		cargo.addWeapons("typhoon", 5);
		
		//PD
		cargo.addWeapons("clusterbomb", 10);
		cargo.addWeapons("flak", 10);
		cargo.addWeapons("irpulse", 10);
		cargo.addWeapons("swarmer", 10);

		//support
		cargo.addWeapons("fragbomb", 10);
		cargo.addWeapons("heatseeker", 5);
		cargo.addWeapons("harpoon", 5);
		
		cargo.addWeapons("sabot", 5);
		cargo.addWeapons("annihilator", 5);
		cargo.addWeapons("lightdualmg", 10);
		cargo.addWeapons("lightdualac", 10);
		cargo.addWeapons("lightneedler", 10);
		cargo.addWeapons("heavymg", 10);
		cargo.addWeapons("heavymauler", 5);
		cargo.addWeapons("salamanderpod", 5);
		
		cargo.addWeapons("hveldriver", 5);

		//assault
		cargo.addWeapons("lightag", 10);
		cargo.addWeapons("chaingun", 5);
		
		cargo.addMothballedShip(FleetMemberType.SHIP, "wolf_Hull", null);
		cargo.addMothballedShip(FleetMemberType.FIGHTER_WING, "broadsword_wing", null);
		cargo.addMothballedShip(FleetMemberType.FIGHTER_WING, "broadsword_wing", null);
		cargo.addMothballedShip(FleetMemberType.FIGHTER_WING, "piranha_wing", null);
		cargo.addMothballedShip(FleetMemberType.FIGHTER_WING, "piranha_wing", null);
		cargo.addMothballedShip(FleetMemberType.FIGHTER_WING, "talon_wing", null);
		cargo.addMothballedShip(FleetMemberType.FIGHTER_WING, "talon_wing", null);
		cargo.addMothballedShip(FleetMemberType.FIGHTER_WING, "thunder_wing", null);
		cargo.addMothballedShip(FleetMemberType.FIGHTER_WING, "thunder_wing", null);
		cargo.addMothballedShip(FleetMemberType.FIGHTER_WING, "gladius_wing", null);
		cargo.addMothballedShip(FleetMemberType.FIGHTER_WING, "warthog_wing", null);
		cargo.addMothballedShip(FleetMemberType.FIGHTER_WING, "warthog_wing", null);
		
		cargo.addMothballedShip(FleetMemberType.SHIP, "buffalo2_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "buffalo2_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "condor_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "tarsus_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "tarsus_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "gemini_Hull", null);


		cargo.addMothballedShip(FleetMemberType.SHIP, "venture_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "dominator_Hull", null);
		cargo.addMothballedShip(FleetMemberType.SHIP, "conquest_Hull", null);
	}
}
