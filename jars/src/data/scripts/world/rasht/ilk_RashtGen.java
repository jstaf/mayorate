package data.scripts.world.rasht;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.OrbitAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import java.awt.Color;

public class ilk_RashtGen implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.createStarSystem("Rasht");
        LocationAPI hyper = Global.getSector().getHyperspace();

        system.setBackgroundTextureFilename("graphics/ilk/backgrounds/ilk_background2.jpg");

        // create the star and generate the hyperspace anchor for this system
        PlanetAPI star = system.initStar("rasht", "star_red_dwarf", 400f);

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
        PlanetAPI ilk1 = system.addPlanet("ilkhanna", star, "Ilkhanna", "ilk_ilkhanna", 180, 175, 3800, 200);
        ilk1.setCustomDescriptionId("planet_Ilkhanna");
        ilk1.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "sindria"));
        ilk1.getSpec().setGlowColor(new Color(255,255,255,255));
	ilk1.getSpec().setUseReverseLightForGlow(true);
	ilk1.applySpecChanges();
                
        PlanetAPI ilk1_1 = system.addPlanet("mun", ilk1, "Mun", "barren", 150, 80, 1200, 42);
        ilk1_1.setCustomDescriptionId("planet_Mun");
        
        PlanetAPI ilk2 = system.addPlanet("inir", star, "Inir", "rocky_metallic", 330, 120, 1000, 30);
        ilk2.setCustomDescriptionId("planet_Inir");
        
        PlanetAPI ilk3 = system.addPlanet("sindral", star, "Sindral", "rocky_ice", 20, 75, 9500, 450);
        ilk3.setCustomDescriptionId("planet_Sindral");
        
        SectorEntityToken relay = system.addCustomEntity("mayorate_relay", // unique id
				 "Rasht Relay", // name - if null, defaultName from custom_entities.json will be used
				 "comm_relay", // type of object, defined in custom_entities.json
				 "mayorate"); // faction
                //orbits ilkhanna
                relay.setCircularOrbit( system.getEntityById("ilkhanna"), 150, 1600, 100);

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

        JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("ilk_jump_alpha", "Ilkhanna Jump Point");
        OrbitAPI orbit = Global.getFactory().createCircularOrbit(ilk1, 135, 600, 30);
        jumpPoint.setOrbit(orbit);
        jumpPoint.setRelatedPlanet(ilk1);
        jumpPoint.setStandardWormholeToHyperspaceVisual();
        system.addEntity(jumpPoint);

        system.autogenerateHyperspaceJumpPoints(true, true);

        //add stations and cargo
        SectorEntityToken ilk_station = system.addOrbitalStation("ilk_port", ilk1, 45, 300, 50, "Port Authority", "mayorate");
        //initIlk_StationCargo(ilk_station);

        //SectorEntityToken ras_PirateStation = system.addOrbitalStation(ilk3, 180, 300, 30, "Freeport", "pirates");
        //ras_PirateStationCargo(ras_PirateStation);

        //spawns fleets
        /*ilk_MayorateSpawnPoint mayorateSpawn = new ilk_MayorateSpawnPoint(sector, system, 2, 20, ilk1);
        system.addScript(mayorateSpawn);
        for (int i = 0; i < 3; i++) {
            mayorateSpawn.spawnFleet();
        }

        ilk_RashtPirateSpawnPoint ras_PirateSpawn = new ilk_RashtPirateSpawnPoint(sector, system, 5, 20, ilk3);
        system.addScript(ras_PirateSpawn);
        for (int i = 0; i < 3; i++) {
            ras_PirateSpawn.spawnFleet();
        }

        SectorEntityToken hegemony_raider_token = system.createToken(8000, 9000);
        ilk_RashtHegemonySpawnPoint ras_HegemonySpawn = new ilk_RashtHegemonySpawnPoint(sector, system, 4, 10, hegemony_raider_token);
        system.addScript(ras_HegemonySpawn);
        for (int i = 0; i < 4; i++) {
            ras_HegemonySpawn.spawnFleet();
        }

        ilk_MayorateSupportFleetSpawnPoint mayorate_supply_fleet = new ilk_MayorateSupportFleetSpawnPoint(sector, hyper, 17, 1, hyper.createToken(-4000, -6500), ilk_station);
        system.addScript(mayorate_supply_fleet);

        //ilk_PiratePlunderFleetSpawnPoint ras_plunder_fleet = new ilk_PiratePlunderFleetSpawnPoint(sector, hyper, 20, 1, hyper.createToken(3000, -2000), ras_PirateStation);
        //system.addScript(ras_plunder_fleet);
*/
        //gives relationship advice
        /*mayorate.setRelationship("player", 0);
        mayorate.setRelationship("tritachyon", 1);
        mayorate.setRelationship("hegemony", -1);
        mayorate.setRelationship("pirates", 0);
        mayorate.setRelationship("independent", 0);

        FactionAPI player = sector.getFaction("player");
        player.setRelationship("pirates", 0);*/
        
    }
}
