package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class SpawnHostileFleet extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        // create a new fleet of our faction of interest and spawn it RIGHT ON TOP OF THE PLAYER!!!
        String faction = params.get(0).getString(memoryMap);
        String fleetCompType = params.get(1).getString(memoryMap);
        CampaignFleetAPI fleet = Global.getSector().createFleet(faction, fleetCompType);
        Global.getSector().getPlayerFleet().getContainingLocation().spawnFleet(Global.getSector().getPlayerFleet(), 0, 0, fleet);
        fleet.addAssignment(FleetAssignment.INTERCEPT, Global.getSector().getPlayerFleet(), 10f);
                
        // add fleet id to memory
        //memoryMap.put("lastSpawnedFleet", null);
        
        return true;
    }

}
