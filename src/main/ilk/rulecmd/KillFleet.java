package ilk.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

public class KillFleet extends BaseCommandPlugin {

  // param1 fleet mem key
  @Override
  public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params,
      Map<String, MemoryAPI> memoryMap) {
    String fleetID = params.get(0).getString(memoryMap);

    if (Global.getSector().getEntityById(fleetID) instanceof CampaignFleetAPI) {
      CampaignFleetAPI fleet = (CampaignFleetAPI) Global.getSector().getEntityById(fleetID);

      // nothing personal.
      List<FleetMemberAPI> toDestroy = fleet.getFleetData().getMembersListCopy();
      for (FleetMemberAPI unwittingVictim : toDestroy) {
        fleet.removeFleetMemberWithDestructionFlash(unwittingVictim);
      }
      fleet.despawn();

      return true;
    } else {
      return false;
    }
  }
}
