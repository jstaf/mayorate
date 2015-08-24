package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

/** Use a random number of marines by removing them from player inventory
 * Created by jeff on 24/08/15.
 */
public class UseMarines extends BaseCommandPlugin {
    @Override
    public boolean execute(String s, InteractionDialogAPI interactionDialogAPI, List<Misc.Token> list, Map<String, MemoryAPI> map) {
        int numberUsed = Integer.parseInt(list.get(0).getString(map));
        numberUsed -= (int) (Math.random() * numberUsed);

        // set a memory key just so we know how many marines got used
        interactionDialogAPI.getInteractionTarget().getMemory().set("$MARINES_LOST", numberUsed);

        int starting = Global.getSector().getPlayerFleet().getCargo().getMarines();
        if (numberUsed < starting) {
            Global.getSector().getPlayerFleet().getCargo().removeMarines(numberUsed);
            return true;
        } else {
            Global.getSector().getPlayerFleet().getCargo().removeMarines(starting);
            return false;
        }
    }
}
