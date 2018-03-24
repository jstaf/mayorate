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
		Commodities.TAG_LIGHT_INDUSTRY_OUT,
		Commodities.TAG_REFINING_IN, 
		Commodities.TAG_REFINING_OUT, 
	};
            
    @Override
    public void apply(String id) {
        market.getStability().modifyFlat(id, STABILITY_INDOCTRINATION, "Indoctrination");
        // TODO: Re-add supply changes without messing up prices.
    }

    @Override
    public void unapply(String id) {
       market.getStability().unmodify(id);
    }

}
