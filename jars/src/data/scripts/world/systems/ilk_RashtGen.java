package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.OrbitAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.terrain.BaseRingTerrain;
import com.fs.starfarer.api.util.Misc;
import data.scripts.world.ilk_PathSpawnPoint;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class ilk_RashtGen implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.createStarSystem("Rasht");
        system.getLocation().set(13000,-4500);

        system.setBackgroundTextureFilename("graphics/ilk/backgrounds/ilk_background2.jpg");

        // create the star and generate the hyperspace anchor for this system
        PlanetAPI star = system.initStar("rasht", "star_red_dwarf", 400f, 300f);

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
        PlanetAPI ilk1 = system.addPlanet("ilkhanna", star, "Ilkhanna", "ilk_ilkhanna", 185, 175, 3800, 200);
        ilk1.setCustomDescriptionId("planet_Ilkhanna");
        ilk1.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "sindria"));
        ilk1.getSpec().setGlowColor(new Color(255, 255, 255, 255));
	    ilk1.getSpec().setUseReverseLightForGlow(true);
	    ilk1.applySpecChanges();

        PlanetAPI ilk1_1 = system.addPlanet("mun", ilk1, "Mun", "barren", 150, 80, 1200, 42);
        ilk1_1.setCustomDescriptionId("planet_Mun");

        //add station
        SectorEntityToken ilk_station = system.addCustomEntity("ilk_port", "Kushehr Orbital Yards", "ilk_station_kushehr", "mayorate");
        ilk_station.setCircularOrbitPointingDown(ilk1, 315f, 300f, 40f);
        ilk_station.setInteractionImage("illustrations", "interdiction");
        ilk_station.setCustomDescriptionId("ilk_station_kushehr");
                
        PlanetAPI ilk2 = system.addPlanet("inir", star, "Inir", "rocky_metallic", 330, 120, 1000, 30);
        ilk2.setCustomDescriptionId("planet_Inir");
        ilk2.setInteractionImage("illustrations", "inir_surface");
        
        PlanetAPI ilk3 = system.addPlanet("sindral", star, "Sindral", "rocky_ice", 20, 75, 10500, 250);
        ilk3.setCustomDescriptionId("planet_Sindral");
        ilk3.setInteractionImage("illustrations", "geothermal");
        
        PlanetAPI ilk4 = system.addPlanet("iolanthe", star, "Iolanthe", "gas_giant", 330, 200, 8500, 150);
        ilk4.setCustomDescriptionId("planet_Iolanthe");
        ilk4.setInteractionImage("illustrations", "cloud_city");

        // create relay
        SectorEntityToken relay = system.addCustomEntity("mayorate_relay", // unique id
				 "Rasht Comm Relay", // name - if null, defaultName from custom_entities.json will be used
				 "comm_relay", // type of object, defined in custom_entities.json
				 "mayorate"); // faction
        //orbits rasht @ ilkhanna L5 point
        relay.setCircularOrbit(system.getEntityById("rasht"), 245, 3800, 200);

        // create jump point
        JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("ilk_jump_alpha", "Ilkhanna L5 Jump Point");
        jumpPoint.setCircularOrbit(system.getEntityById("rasht"), 125, 3800, 200);
        jumpPoint.setRelatedPlanet(ilk1);
        jumpPoint.setStandardWormholeToHyperspaceVisual();
        system.addEntity(jumpPoint);
        system.autogenerateHyperspaceJumpPoints(true, true);

        // inner system band
        system.addAsteroidBelt(star, 200, 1850, 600, 100, 130);
        system.addRingBand(star, "misc", "rings1", 256f, 2, Color.white, 256f, 2050, 120f);
        system.addRingBand(star, "misc", "rings1", 256f, 3, Color.white, 256f, 1800, 150f);
        system.addRingBand(star, "misc", "rings1", 256f, 3, Color.white, 256f, 2100, 110f);
        system.addRingBand(star, "misc", "rings1", 20f, 2, Color.white, 20f, 2300, 110f);
        SectorEntityToken ring = system.addTerrain(Terrain.RING, new BaseRingTerrain.RingParams(500f, 2000, null, "Cinder Field"));
        ring.setCircularOrbit(star, 0, 0, 120f);

        // outer system band
        system.addRingBand(star, "misc", "rings1", 256f, 1, Color.white, 256f, 6000, 200f);
        system.addRingBand(star, "misc", "rings1", 256f, 0, Color.white, 280f, 6300, 160f);
        system.addRingBand(star, "misc", "rings1", 256f, 2, Color.white, 256f, 6900, 110f);
        system.addAsteroidBelt(star, 400, 6200, 300, 100, 200);
        system.addAsteroidBelt(star, 100, 7100, 100, 60, 110);
        SectorEntityToken ringInner = system.addTerrain(Terrain.RING, new BaseRingTerrain.RingParams(600f, 6000, null, "Lanula's Arch"));
        ringInner.setCircularOrbit(star, 0, 0, 165f);

        // full system nebula
        /*SectorEntityToken rashtNebula = Misc.addNebulaFromPNG("data/campaign/terrain/rashtNebula.png",
                0f, 0f, //center
                system,
                "terrain", "nebula_amber", 4, 4);*/

        //adding markets
        String MAYORATE_FACTION = "mayorate";
        SystemUtils.addMarketplace(MAYORATE_FACTION,
                ilk1,
                new ArrayList<>(Arrays.asList(ilk_station)),
                "Ilkhanna",
                6,
                new ArrayList<>(Arrays.asList("ai_core",
                        Conditions.HEADQUARTERS, Conditions.MILITARY_BASE, Conditions.ORBITAL_STATION,
                        Conditions.AUTOFAC_HEAVY_INDUSTRY, Conditions.ORE_REFINING_COMPLEX, Conditions.ORE_REFINING_COMPLEX,
                        Conditions.VICE_DEMAND, Conditions.ARID, Conditions.POPULATION_6)),
                new ArrayList<>(Arrays.asList(Submarkets.GENERIC_MILITARY, Submarkets.SUBMARKET_BLACK, Submarkets.SUBMARKET_OPEN, Submarkets.SUBMARKET_STORAGE)),
                0.3f
        );
 
        SystemUtils.addMarketplace(MAYORATE_FACTION,
                ilk2,
                null,
                "Inir",
                3,
                new ArrayList<>(Arrays.asList("ai_core", "indoctrination",
                        Conditions.ANTIMATTER_FUEL_PRODUCTION, Conditions.ORE_COMPLEX, Conditions.UNINHABITABLE, Conditions.POPULATION_4)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_BLACK, Submarkets.SUBMARKET_OPEN, Submarkets.SUBMARKET_STORAGE)),
                0.3f
        );
 
        SystemUtils.addMarketplace(Factions.PIRATES,
                ilk3,
                null,
                "Sindral",
                4,
                new ArrayList<>(Arrays.asList("indoctrination", Conditions.FRONTIER, Conditions.COTTAGE_INDUSTRY,
                        Conditions.ORGANIZED_CRIME, Conditions.FREE_PORT, Conditions.ICE, Conditions.POPULATION_3)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_BLACK, Submarkets.SUBMARKET_OPEN, Submarkets.SUBMARKET_STORAGE)),
                0.3f
        );
 
        SystemUtils.addMarketplace(Factions.INDEPENDENT,
                ilk4,
                null,
                "Iolanthe",
                2,
                new ArrayList<>(Arrays.asList(Conditions.LARGE_REFUGEE_POPULATION, Conditions.OUTPOST,
                        Conditions.VOLATILES_COMPLEX, Conditions.UNINHABITABLE, Conditions.POPULATION_3)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_BLACK, Submarkets.SUBMARKET_OPEN, Submarkets.SUBMARKET_STORAGE)),
                0.3f
        );

        // add consuls to mayorate markets
        SystemUtils.addConsul(ilk1.getMarket());
        SystemUtils.addConsul(ilk2.getMarket());

        // make some luddites so players can level up their mayorate rep if they want
        sector.addScript(new ilk_PathSpawnPoint(sector, system, 3, 7, ilk3));
    }
}
