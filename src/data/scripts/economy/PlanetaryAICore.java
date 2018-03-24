package data.scripts.economy;

import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class PlanetaryAICore extends BaseMarketConditionPlugin {
    
    private static final float FOOD_USAGE_MULT = 0.75f;
    private static final float FOOD_PRODUCTION_MULT = 0.35f;
    private static final float FUEL_USAGE_MULT = 0.0025f;
//    public static final float METALS_USAGE_MULT = 0.0025f;
//    public static final float RARE_METALS_USAGE = 0.001f;
    
    @Override
    public void apply(String id) {
        // TODO: Re-add supply changes without messing up prices.
    }
    
    @Override
    public void unapply(String id) {
    }
    
}
