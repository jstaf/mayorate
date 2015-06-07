package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class SpawnHostileFleet extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        // parse input
        String faction = params.get(0).getString(memoryMap);
        String fleetCompType = params.get(1).getString(memoryMap);
        
        // debugging
        // dialog.getTextPanel().addParagraph(faction + " " + fleetCompType);
        
        // create a new fleet of our faction of interest and spawn it RIGHT ON TOP OF THE PLAYER!!!
        CampaignFleetAPI fleet = Global.getSector().createFleet(faction, fleetCompType);
        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        player.getContainingLocation().spawnFleet(player, 0, 0, fleet);
        fleet.addAssignment(FleetAssignment.INTERCEPT, player, 10f);
        
        // allow immediate attack
        player.setNoEngaging(0f);       
        
        // make the other fleet want your blood
        MemoryAPI memory = fleet.getMemoryWithoutUpdate();
        memory.set(MemFlags.MEMORY_KEY_MAKE_HOSTILE, true);
        memory.set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
        
        // add new fleet to memory
        String fleetID = (String) memory.get("id");
        
        memoryMap.put("global", memory);
        
        // start the fireworks
        dialog.dismiss();
        Global.getSector().setPaused(false);
        
        return true;
    }

}
