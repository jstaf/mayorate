package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.util.Misc;
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
        
        // add new fleet's id to memory, so we can check to see if it's still alive later and do stuff
        String fleetID = fleet.getId();
        Global.getSector().getMemory().set("$SPAWNED_FLEET_ID", fleetID);
        
        // allow immediate attack
        player.setNoEngaging(0f);       
        fleet.setNoEngaging(0f);
        
        // make the other fleet want your blood
        MemoryAPI mem = fleet.getMemoryWithoutUpdate();
        mem.set(MemFlags.MEMORY_KEY_MAKE_HOSTILE, true);
        mem.set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
        fleet.addAssignment(FleetAssignment.INTERCEPT, player, 10f);
        
        // start the fireworks         
        dialog.dismiss();
        Global.getSector().setPaused(false);
        
        //dialog.startBattle(new BattleCreationContext(player, FleetGoal.ATTACK, fleet, FleetGoal.ATTACK));
        
        return true;
    }
}
