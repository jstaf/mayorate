package ilk.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

/**
 * Created by jeff on 24/08/15.
 */
public class ilk_HasMarines extends BaseCommandPlugin {
    @Override
    public boolean execute(String s, InteractionDialogAPI interactionDialogAPI, List<Misc.Token> list, Map<String, MemoryAPI> map) {
        int numberNeeded = Integer.parseInt(list.get(0).getString(map));
        int marines = Global.getSector().getPlayerFleet().getCargo().getMarines();

        if (marines >= numberNeeded) {
            return true;
        } else {
            return false;
        }
    }
}
