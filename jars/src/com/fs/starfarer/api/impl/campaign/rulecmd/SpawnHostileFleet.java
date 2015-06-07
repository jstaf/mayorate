package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.util.Misc.TokenType;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SpawnHostileFleet extends BaseCommandPlugin {
    
    /*
    Usage:
    param1 - a string faction name WITHOUT quotes
    param2 - a string matching one of the "fleet_compositions" in the .faction file WITHOUT quotes
    */
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        // parse input
        String faction = params.get(0).getString(memoryMap);
        String fleetCompType = params.get(1).getString(memoryMap);
        
        // create a new fleet of our faction of interest and spawn it RIGHT ON TOP OF THE PLAYER!!!
        CampaignFleetAPI fleet = Global.getSector().createFleet(faction, fleetCompType);
        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        player.getContainingLocation().spawnFleet(player, 0, 0, fleet);
        fleet.addAssignment(FleetAssignment.INTERCEPT, player, 10f);
        
        // allow immediate attack
        player.setNoEngaging(0f);       
        
        // make the other fleet want your blood
        MemoryAPI mem = fleet.getMemoryWithoutUpdate();
        mem.set(MemFlags.MEMORY_KEY_MAKE_HOSTILE, true);
        mem.set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
        
        // add new fleet's id to memory, so we can check to see if it's still alive later and do stuff
        String fleetID = fleet.getId();
        Global.getSector().getMemory().set("$SPAWNED_FLEET_ID", fleetID);
        
        // start the fireworks     
        //dialog.dismiss();
        //Global.getSector().setPaused(false);
        
        return true;
    }
}
