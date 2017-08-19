package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.terrain.BaseRingTerrain;
import data.scripts.MayorateModPlugin;
import data.scripts.world.utils.SystemUtils;
import data.scripts.world.utils.ilk_BountySpawner;
import data.scripts.world.utils.ilk_CommissionEffects;
import data.scripts.world.utils.ilk_PathSpawnPoint;

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
        PlanetAPI star = system.initStar("rasht", "ilk_star_rasht", 400f, 300f);
        system.setLightColor(new Color(183, 98, 84)); // light color in entire system, affects all entities

        // SectorEntityToken creation goes from star -> fringe
        PlanetAPI ilk2 = system.addPlanet("inir", star, "Inir", "rocky_metallic", 330f, 120f, 1000f, 30f);
        ilk2.setCustomDescriptionId("planet_Inir");
        ilk2.setInteractionImage("illustrations", "inir_surface");

        // inner system band
        system.addAsteroidBelt(star, 100, 2250, 300, 100, 180);
        system.addRingBand(star, "terrain", "rings4", 512f, 2, new Color(65, 47, 42), 512f, 1850f, 280f);
        system.addRingBand(star, "terrain", "rings1", 256f, 2, Color.white, 256f, 2150, 170f);
        system.addRingBand(star, "terrain", "rings1", 256f, 3, Color.white, 256f, 1975, 220f);
        system.addRingBand(star, "terrain", "rings1", 256f, 3, Color.white, 256f, 2200, 140f);
        //system.addRingBand(star, "misc", "rings1", 20f, 2, Color.white, 20f, 2300, 110f);
        SectorEntityToken ring = system.addTerrain(Terrain.RING, new BaseRingTerrain.RingParams(680f, 1950f, null, "Cinder Field"));
        ring.setCircularOrbit(star, 0, 0, 120f);

        PlanetAPI ilk1 = system.addPlanet("ilkhanna", star, "Ilkhanna", "ilk_ilkhanna", 185, 175, 4200, 200);
        ilk1.setCustomDescriptionId("planet_ilkhanna");
        ilk1.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "sindria"));
        ilk1.getSpec().setGlowColor(new Color(255, 255, 255, 255));
	    ilk1.getSpec().setUseReverseLightForGlow(true);
	    ilk1.applySpecChanges();

        PlanetAPI ilk1_1 = system.addPlanet("mun", ilk1, "Mun", "barren", 150, 80, 900, 42);
        ilk1_1.setCustomDescriptionId("planet_Mun");

        //add station
        SectorEntityToken ilk_station = system.addCustomEntity("ilk_port", "Kushehr Orbital Yards", "ilk_station_kushehr", "mayorate");
        ilk_station.setCircularOrbitPointingDown(ilk1, 315f, 300f, 40f);
        ilk_station.setInteractionImage("illustrations", "interdiction");
        ilk_station.setCustomDescriptionId("ilk_station_kushehr");

        // create relay
        SectorEntityToken relay = system.addCustomEntity("mayorate_relay", // unique id
                "Rasht Relay", // name - if null, defaultName from custom_entities.json will be used
                "comm_relay", // type of object, defined in custom_entities.json
                "mayorate"); // faction
        //orbits rasht @ ilkhanna L5 point
        relay.setCircularOrbit(system.getEntityById("rasht"), 245, 4200, 200);

        // create jump point
        JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("ilk_jump_alpha", "Ilkhanna Jump Point");
        jumpPoint.setCircularOrbit(system.getEntityById("rasht"), 125, 4200, 200);
        jumpPoint.setRelatedPlanet(ilk1);
        jumpPoint.setStandardWormholeToHyperspaceVisual();
        system.addEntity(jumpPoint);

        PlanetAPI ilk3 = system.addPlanet("sindral", star, "Sindral", "rocky_ice", 20f, 90f, 6200f, 275f);
        ilk3.setCustomDescriptionId("planet_Sindral");

        // outer system band
        system.addRingBand(star, "terrain", "rings1", 256f, 1, Color.white, 256f, 6900, 300f);
        system.addRingBand(star, "terrain", "rings1", 256f, 0, Color.white, 280f, 7200, 260f);
        system.addRingBand(star, "terrain", "rings1", 256f, 2, Color.white, 256f, 7800, 210f);
        system.addAsteroidBelt(star, 400, 7100, 300, 100, 280);
        system.addAsteroidBelt(star, 100, 8000, 100, 60, 150);
        SectorEntityToken ringInner = system.addTerrain(Terrain.RING, new BaseRingTerrain.RingParams(600f, 6900, null, "Lanula's Arch"));
        ringInner.setCircularOrbit(star, 0, 0, 165f);

        PlanetAPI ilk4 = system.addPlanet("iolanthe", star, "Iolanthe", "gas_giant", 277f, 200f, 9650f, 350f);
        ilk4.setCustomDescriptionId("planet_Iolanthe");
        ilk4.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "asharu"));
        ilk4.getSpec().setGlowColor(new Color(118, 248, 255, 255));
        ilk4.getSpec().setUseReverseLightForGlow(true);
        ilk4.applySpecChanges();
        system.addRingBand(ilk4, "terrain", "rings2", 256f, 1, new Color(255, 255, 255, 200), 256f, 450f, 40f);

        PlanetAPI ilk4_4 = system.addPlanet("amaru", ilk4, "Amaru", "cryovolcanic", 330f, 50f, 800f, 50f);

        // only make jump points once everythings been added
        system.autogenerateHyperspaceJumpPoints(true, true);

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
                        Conditions.ARID, Conditions.POPULATION_6)),
                new ArrayList<>(Arrays.asList(Submarkets.GENERIC_MILITARY, Submarkets.SUBMARKET_BLACK, Submarkets.SUBMARKET_OPEN, Submarkets.SUBMARKET_STORAGE)),
                0.3f
        );
 
        SystemUtils.addMarketplace(MAYORATE_FACTION,
                ilk2,
                null,
                "Inir",
                3,
                new ArrayList<>(Arrays.asList("ai_core", "indoctrination",
                        Conditions.ANTIMATTER_FUEL_PRODUCTION, Conditions.ORE_COMPLEX, Conditions.POPULATION_4)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_BLACK, Submarkets.SUBMARKET_OPEN, Submarkets.SUBMARKET_STORAGE)),
                0.3f
        );
 
        SystemUtils.addMarketplace(Factions.PIRATES,
                ilk3,
                null,
                "Sindral",
                4,
                new ArrayList<>(Arrays.asList(Conditions.MILITARY_BASE, Conditions.SHIPBREAKING_CENTER, Conditions.ORGANIZED_CRIME,
                        Conditions.ICE, Conditions.POPULATION_4)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_BLACK, Submarkets.SUBMARKET_OPEN, Submarkets.SUBMARKET_STORAGE, Submarkets.GENERIC_MILITARY)),
                0.3f
        );
 
        SystemUtils.addMarketplace(Factions.INDEPENDENT,
                ilk4,
                null,
                "Iolanthe",
                3,
                new ArrayList<>(Arrays.asList(Conditions.FRONTIER, Conditions.FREE_PORT, Conditions.VOLATILES_COMPLEX,
                        Conditions.POPULATION_3)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_BLACK, Submarkets.SUBMARKET_OPEN, Submarkets.SUBMARKET_STORAGE)),
                0.3f
        );

        // only do the following if not in exerelin corvus mode
        if (!MayorateModPlugin.getIsExerelin()) {
            // make some luddites
            sector.addScript(new ilk_PathSpawnPoint(sector, system, 3, 7, ilk4));
            // start occasional bounties against mayorate enemies so players can more easily level up their mayorate rep
            sector.addScript(new ilk_BountySpawner(sector, system, ilk1.getMarket(), 180f));

            // add forgiveness script to avoid rep decay on mayorate commission from being hostile with pirates
            sector.addScript(new ilk_CommissionEffects());
        }
    }
}
