package ilk.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

public class ilk_SetInteractionImage extends BaseCommandPlugin {

  // param 1 = category
  // param 2 = key
  @Override
  public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params,
      Map<String, MemoryAPI> memoryMap) {
    SectorEntityToken interactor = dialog.getInteractionTarget();
    String category = params.get(0).getString(memoryMap);
    String key = params.get(1).getString(memoryMap);

    interactor.setInteractionImage(category, key);

    dialog.getVisualPanel().showImageVisual(interactor.getCustomInteractionDialogImageVisual());

    return true;
  }
}
