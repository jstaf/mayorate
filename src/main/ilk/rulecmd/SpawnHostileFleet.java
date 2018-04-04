package ilk.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class SpawnHostileFleet extends BaseCommandPlugin {

  /*
  Usage:
  param1 - a string faction name WITHOUT quotes
  param2 - a string matching one of the "fleet_compositions" in the .faction file WITHOUT quotes
  param3 - spawn location matching a sector entity id
  param4 - global memory key to add fleet to. MUST NOT BEGIN WITH $ (or rules.csv thinks its a variable)
  */
  @Override
  public boolean execute(
      String ruleId,
      InteractionDialogAPI dialog,
      List<Misc.Token> params,
      Map<String, MemoryAPI> memoryMap) {
    // parse input
    String faction = params.get(0).getString(memoryMap);
    String fleetCompType = params.get(1).getString(memoryMap);
    String location = params.get(2).getString(memoryMap);
    String memKey = "$" + params.get(3).getString(memoryMap);

    // create a new fleet of our faction of interest and spawn it RIGHT ON TOP OF THE PLAYER!!!
    CampaignFleetAPI fleet = Global.getSector().createFleet(faction, fleetCompType);
    CampaignFleetAPI player = Global.getSector().getPlayerFleet();
    SectorEntityToken spawnLoc = Global.getSector().getEntityById(location);
    spawnLoc.getContainingLocation().spawnFleet(spawnLoc, 0, 0, fleet);

    // add new fleet's id to memory, so we can check to see if it's still alive later and do stuff
    String fleetID = fleet.getId();
    Global.getSector().getMemory().set(memKey, fleetID);

    // start the fireworks
    dialog.dismiss();
    Global.getSector().setPaused(false);

    // allow immediate attack & make the other fleet want your blood
    MemoryAPI mem = fleet.getMemoryWithoutUpdate();
    mem.set(MemFlags.MEMORY_KEY_MAKE_HOSTILE, true);
    mem.set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
    fleet.addAssignment(FleetAssignment.INTERCEPT, player, 10f);
    player.setNoEngaging(0f);
    fleet.setNoEngaging(0f);
    fleet.setInteractionTarget(player);

    return true;
  }
}
