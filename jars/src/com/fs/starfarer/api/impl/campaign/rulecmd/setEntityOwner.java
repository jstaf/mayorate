package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class setEntityOwner extends BaseCommandPlugin {

    // param 1 is the faction youd like to designate as the current owner of what you're interacting with
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        SectorEntityToken interactor = dialog.getInteractionTarget();
        
        String faction = params.get(0).getString(memoryMap);
        
        interactor.setFaction(faction);
        if (interactor.getMarket() != null) {
            interactor.getMarket().setFactionId(faction);
        }
        
        return true;
    }
    
}
