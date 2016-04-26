package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.world.utils.SystemUtils;

import java.util.List;
import java.util.Map;

public class ManageConsul extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        MarketAPI market = dialog.getInteractionTarget().getMarket();

        PersonAPI consul = null;
        for (PersonAPI peep : market.getPeopleCopy()) {
            if (peep.getPostId().equals("consul")) {
                consul = peep;
                break;
            }
        }

        if (consul == null) {
            SystemUtils.addConsul(market);
            return true;
        } else {
            market.removePerson(consul);
            return false;
        }
    }
}
