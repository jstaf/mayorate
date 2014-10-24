package data.scripts.economy;

import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class Indoctrination extends BaseMarketConditionPlugin {
    
    public static final float INDOCTRINATION_PRODUCTION_MULT = 1.5f;
    public static final float STABILITY_INDOCTRINATION = 1f;
    public static final float INDOCTRINATION_ORGANS_MULT = 5f;
    
    private static final String[] tags = new String[] {
           	Commodities.TAG_HEAVY_INDUSTRY_IN,
		Commodities.TAG_HEAVY_INDUSTRY_OUT,
		//Commodities.TAG_LIGHT_INDUSTRY_IN,
		Commodities.TAG_LIGHT_INDUSTRY_OUT,
		Commodities.TAG_REFINING_IN, 
		Commodities.TAG_REFINING_OUT, 
	};
            
    @Override
    public void apply(String id) {
        
        for (CommodityOnMarketAPI com : market.getCommoditiesWithTags(tags)) {
            com.getSupply().modifyMult(id, INDOCTRINATION_PRODUCTION_MULT);
        }
        market.getCommodityData(Commodities.ORGANS).getSupply().modifyMult(id, INDOCTRINATION_ORGANS_MULT);

        market.getStability().modifyFlat(id, STABILITY_INDOCTRINATION, "Indoctrination");
    }

    @Override
    public void unapply(String id) {

        for (CommodityOnMarketAPI com : market.getCommoditiesWithTags(tags)) {
            com.getSupply().unmodify(id);
        }
        market.getCommodityData(Commodities.ORGANS).getSupply().unmodify(id);

        market.getStability().unmodify(id);
    }

}
