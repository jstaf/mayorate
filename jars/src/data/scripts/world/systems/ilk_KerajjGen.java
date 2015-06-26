package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jeff on 25/06/15.
 */
public class ilk_KerajjGen implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {

        StarSystemAPI system = sector.createStarSystem("Kerajj");
        system.getLocation().set(-9500, -7300);
        LocationAPI hyper = Global.getSector().getHyperspace();

        system.setBackgroundTextureFilename("graphics/ilk/backgrounds/ilk_background2.jpg");

        //initialize the star
        PlanetAPI star = system.initStar("Kerajj", "star_white", 500f);
        star.setCustomDescriptionId("planet_Kerajj");
        system.setLightColor(new Color(240, 238, 191)); // light color in entire system, affects all entities

        PlanetAPI ker1 = system.addPlanet("opel", star, "Opel", "desert", 40, 100, 3300, 100);
        ker1.setCustomDescriptionId("planet_Opel");
        ker1.setInteractionImage("illustrations", "urban03");
        ilk_RashtGen.addMarketplace(Factions.INDEPENDENT,
                ker1,
                null,
                "Opel",
                2,
                new ArrayList<>(Arrays.asList(Conditions.FRONTIER, Conditions.FREE_PORT, Conditions.LARGE_REFUGEE_POPULATION, Conditions.DESERT, Conditions.POPULATION_4)),
                new ArrayList<>(Arrays.asList(Submarkets.GENERIC_MILITARY, Submarkets.SUBMARKET_BLACK, Submarkets.SUBMARKET_OPEN, Submarkets.SUBMARKET_STORAGE)),
                0.3f);

        PlanetAPI ker2 = system.addPlanet("", star, "", "radiated", 160, 140, 1500, 150);
        ker1.setCustomDescriptionId("planet_");
    }
}
