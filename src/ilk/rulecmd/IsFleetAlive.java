package ilk.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class IsFleetAlive extends BaseCommandPlugin {

  // param1 fleet mem key
  @Override
  public boolean execute(
      String ruleId,
      InteractionDialogAPI dialog,
      List<Misc.Token> params,
      Map<String, MemoryAPI> memoryMap) {
    String fleetID = params.get(0).getString(memoryMap);
    CampaignFleetAPI fleet = (CampaignFleetAPI) Global.getSector().getEntityById(fleetID);

    return (fleet != null) && (fleet.isAlive());
  }
}
