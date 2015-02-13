/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.scripts.economy;

import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class PlanetaryAICore extends BaseMarketConditionPlugin {
    
//    much of the population are ais, less food is produced and consumed, but more fuel and metals are used to maintain them
    
    public static final float FOOD_USAGE_MULT = 0.25f;
    public static final float FOOD_PRODUCTION_MULT = 0.35f;
    
    public static final float FUEL_USAGE_MULT = 0.0025f;
    public static final float METALS_USAGE_MULT = 0.0025f;
    public static final float RARE_METALS_USAGE = 0.001f;
    
    @Override
    public void apply(String id) {
        float population = getPopulation(market);
        
        market.getDemand(Commodities.FOOD).getDemand().modifyMult(id, FOOD_USAGE_MULT);
        market.getCommodityData(Commodities.FOOD).getSupply().modifyMult(id, FOOD_PRODUCTION_MULT);
        
        market.getDemand(Commodities.FUEL).getDemand().modifyFlat(id, FUEL_USAGE_MULT * population);
        market.getDemand(Commodities.METALS).getDemand().modifyFlat(id, METALS_USAGE_MULT * population);
        market.getDemand(Commodities.RARE_METALS).getDemand().modifyFlat(id, RARE_METALS_USAGE * population);
        
    }
    
    @Override
    public void unapply(String id) {
        market.getDemand(Commodities.FOOD).getDemand().unmodify(id);
        market.getCommodityData(Commodities.FOOD).getSupply().unmodify(id);
        
        market.getDemand(Commodities.FUEL).getDemand().unmodify(id);
        market.getDemand(Commodities.METALS).getDemand().unmodify(id);
        market.getDemand(Commodities.RARE_METALS).getDemand().unmodify(id);
    }
    
}
