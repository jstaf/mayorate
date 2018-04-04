package ilk.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class SetEntityOwner extends BaseCommandPlugin {

  // param 1 is the faction youd like to designate as the current owner of what you're interacting
  // with
  @Override
  public boolean execute(
      String ruleId,
      InteractionDialogAPI dialog,
      List<Misc.Token> params,
      Map<String, MemoryAPI> memoryMap) {
    SectorEntityToken interactor = dialog.getInteractionTarget();

    String faction = params.get(0).getString(memoryMap);

    interactor.setFaction(faction);
    MarketAPI market = interactor.getMarket();
    if (market != null) {
      market.setFactionId(faction);
      FactionAPI actualFaction = Global.getSector().getFaction(faction);
      for (SubmarketAPI sub : market.getSubmarketsCopy()) {
        sub.setFaction(actualFaction);
      }
    }

    return true;
  }
}
