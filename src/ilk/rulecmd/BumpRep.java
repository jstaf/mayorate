package ilk.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jeff
 */
public class BumpRep extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        String faction = params.get(0).getString(memoryMap);
        float val = Float.parseFloat(params.get(1).getString(memoryMap));

        FactionAPI player = Global.getSector().getFaction(Factions.PLAYER);

        player.adjustRelationship(faction, val);

        String text;
        Color color;
        if (val > 0) {
            text = "Relationship with " + Global.getSector().getFaction(faction).getDisplayNameLongWithArticle() +
                    " increased by " + String.valueOf((int) (val * 100));
            color = Global.getSettings().getColor("textFriendColor");
        } else {
            text = "Relationship with " + Global.getSector().getFaction(faction).getDisplayNameLongWithArticle() +
                    " decreased by " + String.valueOf((int) (val * 100));
            color = Global.getSettings().getColor("textEnemyColor");
        }

        dialog.getTextPanel().addParagraph(text, color);

        return true;
    }

}
