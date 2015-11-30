package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;

import java.util.ArrayList;

/**
 * Created by Jeff on 2015-11-29.
 */
public class SystemUtils {

    static void addConsul(MarketAPI market) {
        PersonAPI consul = market.getFaction().createRandomPerson();
        consul.setContactWeight(1000f);
        consul.setPostId("consul");
        consul.setRankId("delegate");
        market.addPerson(consul);
        market.getCommDirectory().addPerson(consul);
    }

    static void addMarketplace(String factionID, SectorEntityToken primaryEntity, ArrayList<SectorEntityToken> connectedEntities, String name, int size, ArrayList<String> marketConditions, ArrayList<String> submarkets, float tarrif) {
        EconomyAPI globalEconomy = Global.getSector().getEconomy();

        String planetID = primaryEntity.getId();

        //generate the market ID
        String marketID = planetID + "_market";

        //generate the market
        MarketAPI newMarket = Global.getFactory().createMarket(marketID, name, size);

        //set the faction associated with the market
        newMarket.setFactionId(factionID);
        primaryEntity.setFaction(factionID);

        //set the primary entity related to the market
        newMarket.setPrimaryEntity(primaryEntity);

        //set the base smuggling value (starting value)
        newMarket.setBaseSmugglingStabilityValue(0);

        //set the starting tarrif, could also make this value an input
        newMarket.getTariff().modifyFlat("generator", tarrif);

        //add each sub-market types to the mark
        if (null != submarkets) {
            for (String market : submarkets) {
                newMarket.addSubmarket(market);
            }
        }

        //add each market conditions
        for (String condition : marketConditions) {
            newMarket.addCondition(condition);
        }

        //add all connected entities to this marketplace, moons/stations etc.
        if (null != connectedEntities) {
            for (SectorEntityToken entity : connectedEntities) {
                newMarket.getConnectedEntities().add(entity);
            }
        }

        //add the market to the global market place
        globalEconomy.addMarket(newMarket);

        //get the primary entity and associate it back to the market that we've created
        primaryEntity.setMarket(newMarket);

        //get all associated entities and associate it back to the market we've created
        if (null != connectedEntities) {
            for (SectorEntityToken entity : connectedEntities) {
                entity.setMarket(newMarket);
            }
        }
    }
}
