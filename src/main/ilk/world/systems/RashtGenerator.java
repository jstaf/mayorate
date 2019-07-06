package ilk.world.systems;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.terrain.BaseRingTerrain;

import org.apache.log4j.Logger;

import ilk.MayorateModPlugin;
import ilk.world.utils.BountySpawner;

public class RashtGenerator implements SectorGeneratorPlugin {

  @Override
  public void generate(SectorAPI sector) {

    Logger logger = Global.getLogger(this.getClass());

    StarSystemAPI system = sector.createStarSystem("Rasht");
    system.setBackgroundTextureFilename("graphics/ilk/backgrounds/ilk_background2.jpg");

    // create the star and generate the hyperspace anchor for this system
    PlanetAPI star = system.initStar("rasht", "ilk_star_rasht", 400f, 300f);
    system.setLightColor(new Color(183, 98, 84)); // light color in entire system, affects all entities

    // SectorEntityToken creation goes from star -> fringe

    // closer than 1000f results in fleets rubberbanding away from the star corona
    // and back to the planet. yikes.
    PlanetAPI inir = system.addPlanet("inir", star, "Inir", "rocky_metallic", 330f, 120f, 1200f, 30f);
    inir.setCustomDescriptionId("planet_Inir");
    inir.setInteractionImage("illustrations", "ilk_inir_surface");

    // inner system band
    system.addAsteroidBelt(star, 100, 2250, 300, 100, 180);
    system.addRingBand(star, "terrain", "ilk_rings4", 512f, 2, new Color(65, 47, 42), 512f, 1850f, 280f);
    system.addRingBand(star, "terrain", "ilk_rings1", 256f, 2, Color.white, 256f, 2150, 170f);
    system.addRingBand(star, "terrain", "ilk_rings1", 256f, 3, Color.white, 256f, 1975, 220f);
    system.addRingBand(star, "terrain", "ilk_rings1", 256f, 3, Color.white, 256f, 2200, 140f);
    SectorEntityToken ring = system.addTerrain(Terrain.RING,
        new BaseRingTerrain.RingParams(680f, 1950f, null, "Cinder Field"));
    ring.setCircularOrbit(star, 0, 0, 120f);

    PlanetAPI ilkhanna = system.addPlanet("ilkhanna", star, "Ilkhanna", "ilk_ilkhanna", 185, 175, 4200, 200);
    ilkhanna.setCustomDescriptionId("planet_ilkhanna");
    ilkhanna.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "sindria"));
    ilkhanna.getSpec().setGlowColor(new Color(255, 255, 255, 255));
    ilkhanna.getSpec().setUseReverseLightForGlow(true);
    ilkhanna.applySpecChanges();

    PlanetAPI mun = system.addPlanet("mun", ilkhanna, "Mun", "barren", 150, 80, 900, 42);
    mun.setCustomDescriptionId("planet_Mun");

    // add station
    SectorEntityToken ilk_station = system.addCustomEntity("ilk_port", "Kushehr Orbital Yards", "ilk_station_kushehr",
        "mayorate");
    ilk_station.setCircularOrbitPointingDown(ilkhanna, 315f, 300f, 40f);
    ilk_station.setInteractionImage("illustrations", "ilk_interdiction");
    ilk_station.setCustomDescriptionId("ilk_station_kushehr");

    // create relay
    SectorEntityToken relay = system.addCustomEntity("mayorate_relay", // unique id
        "Rasht Relay", // name - if null, defaultName from custom_entities.json will be used
        "comm_relay", // type of object, defined in custom_entities.json
        "mayorate"); // faction
    // orbits rasht @ ilkhanna L5 point
    relay.setCircularOrbit(system.getEntityById("rasht"), 245, 4200, 200);

    // create jump point
    JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("ilk_jump_alpha", "Ilkhanna Jump Point");
    jumpPoint.setCircularOrbit(system.getEntityById("rasht"), 125, 4200, 200);
    jumpPoint.setRelatedPlanet(ilkhanna);
    jumpPoint.setStandardWormholeToHyperspaceVisual();
    system.addEntity(jumpPoint);

    PlanetAPI sindral = system.addPlanet("sindral", star, "Sindral", "rocky_ice", 20f, 90f, 6200f, 275f);
    sindral.setCustomDescriptionId("planet_Sindral");

    // outer system band
    system.addRingBand(star, "terrain", "ilk_rings1", 256f, 1, Color.white, 256f, 7000, 300f);
    system.addRingBand(star, "terrain", "ilk_rings1", 256f, 0, Color.white, 280f, 7200, 260f);
    system.addRingBand(star, "terrain", "ilk_rings1", 256f, 2, Color.white, 256f, 7250, 210f);
    system.addAsteroidBelt(star, 400, 7100, 300, 100, 280);
    system.addAsteroidBelt(star, 100, 7600, 100, 60, 150);
    SectorEntityToken ringInner = system.addTerrain(Terrain.RING,
        new BaseRingTerrain.RingParams(600f, 6900, null, "Lanula's Arch"));
    ringInner.setCircularOrbit(star, 0, 0, 165f);

    PlanetAPI iolanthe = system.addPlanet("iolanthe", star, "Iolanthe", "gas_giant", 277f, 200f, 9650f, 350f);
    iolanthe.setCustomDescriptionId("planet_Iolanthe");
    iolanthe.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "asharu"));
    iolanthe.getSpec().setGlowColor(new Color(118, 248, 255, 255));
    iolanthe.getSpec().setUseReverseLightForGlow(true);
    iolanthe.applySpecChanges();

    system.addRingBand(iolanthe, "terrain", "ilk_rings3", 256f, 1, new Color(255, 255, 255, 200), 256f, 475f, 40f);
    system.addPlanet("amaru", iolanthe, "Amaru", "cryovolcanic", 330f, 50f, 800f, 50f);

    // only make jump points once everythings been added
    system.autogenerateHyperspaceJumpPoints(true, true);

    // only do the following if not in exerelin corvus mode
    if (!MayorateModPlugin.getIsExerelin()) {
      // start occasional bounties against mayorate enemies so players can more easily
      // level up their mayorate rep
      logger.info("Starting Rasht bounty generator");
      sector.addScript(new BountySpawner(sector, ilkhanna.getMarket(), 90f));
    }
  }
}
