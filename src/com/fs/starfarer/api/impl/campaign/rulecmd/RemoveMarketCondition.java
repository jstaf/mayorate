package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoveMarketCondition extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {        
        MarketAPI market = dialog.getInteractionTarget().getMarket();

        if (market == null) {
            return false;
        }
        
        List<String> toCheckFor = new ArrayList<String>();
        for (Misc.Token param : params) {
            toCheckFor.add(param.getString(memoryMap));
        }
        List<String> toRemove = new ArrayList<String>();
        for (MarketConditionAPI condition : market.getConditions()) {
            String id = condition.getId();
            if (toCheckFor.contains(id)) {
                toRemove.add(id);
            }
        }
        for (String removeMe : toRemove) {
            market.removeCondition(removeMe);
        }

        return true;
    }

}
