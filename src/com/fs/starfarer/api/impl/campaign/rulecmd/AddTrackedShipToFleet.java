package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class AddTrackedShipToFleet extends BaseCommandPlugin {

    //param1 variant id
    //param2 fleet id memory key name to add ship to
    //param3 ship id memory key name to add to global memory
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String ship = params.get(0).getString(memoryMap);
        String fleetID = params.get(1).getString(memoryMap);
        String shipMemKey = "$" + params.get(2).getString(memoryMap);
        
        // create new ship
        FleetMemberType type;
	if (ship.endsWith("_wing")) {
            type = FleetMemberType.FIGHTER_WING; 
	} else {
            type = FleetMemberType.SHIP;
        }
        FleetMemberAPI newMember = Global.getFactory().createFleetMember(type, ship);
        
        // if the memory key is indeed a fleet...
        if (Global.getSector().getEntityById(fleetID) instanceof CampaignFleetAPI){
            // add ship to fleet
            CampaignFleetAPI fleet = (CampaignFleetAPI) Global.getSector().getEntityById(fleetID);
            fleet.getFleetData().addFleetMember(newMember);
            // it needs crew too!
            float minCrew = newMember.getMinCrew();
            fleet.getCargo().addCrew(CargoAPI.CrewXPLevel.REGULAR, (int) minCrew);
            
            // add ship's id to global memory
            String memID = newMember.getId();
            Global.getSector().getMemory().set(shipMemKey, memID);
            return true;
        } else {
            return false;
        }        
    }
}
