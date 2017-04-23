package ilk.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class AddMarketCondition extends BaseCommandPlugin {

    //first param is just a string with the market condition you want
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        MarketAPI market = dialog.getInteractionTarget().getMarket();
        
        if (market == null) {
            return false;
        }
        
        for (Misc.Token param : params) {
            market.addCondition(param.getString(memoryMap));
        }
        
        return true;
    }
    
}
