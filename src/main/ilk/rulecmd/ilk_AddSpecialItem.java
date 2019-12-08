package ilk.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

public class ilk_AddSpecialItem extends BaseCommandPlugin {

    /**
     * Add a special item like blueprints to the player's inventory.
     */
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params,
            Map<String, MemoryAPI> memoryMap) {
        String itemClass = params.get(0).getString(memoryMap);
        String id = params.get(1).getString(memoryMap);
        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        cargo.addItems(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(itemClass, id), 1);
        return true;
    }

}
