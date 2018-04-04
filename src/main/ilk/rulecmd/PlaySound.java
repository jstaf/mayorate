package ilk.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;
import org.lwjgl.util.vector.Vector2f;

public class PlaySound extends BaseCommandPlugin {

  @Override
  public boolean execute(
      String ruleId,
      InteractionDialogAPI dialog,
      List<Misc.Token> params,
      Map<String, MemoryAPI> memoryMap) {
    String soundKey = params.get(0).getString(memoryMap);

    Global.getSoundPlayer()
        .playSound(
            soundKey, 1f, 1f, Global.getSector().getPlayerFleet().getLocation(), new Vector2f());
    return true;
  }
}
