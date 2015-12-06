package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

/**
 * Created by jeff on 22/08/15.
 */
public class IsFactionPresent extends BaseCommandPlugin {
    @Override
    public boolean execute(String s, InteractionDialogAPI interactionDialogAPI, List<Misc.Token> list, Map<String, MemoryAPI> map) {
        FactionAPI faction = Global.getSector().getFaction(list.get(0).getString(map));

        if (faction == null) {
            return false;
        } else {
            return true;
        }
    }
}
