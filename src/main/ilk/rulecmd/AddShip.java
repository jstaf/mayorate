package ilk.rulecmd;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

public class AddShip extends BaseCommandPlugin {

  @Override
  public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params,
      Map<String, MemoryAPI> memoryMap) {

    String ship = params.get(0).getString(memoryMap);

    FleetMemberType type;
    if (ship.endsWith("_wing")) {
      type = FleetMemberType.FIGHTER_WING;
    } else {
      type = FleetMemberType.SHIP;
    }
    FleetMemberAPI newMember = Global.getFactory().createFleetMember(type, ship);
    Global.getSector().getPlayerFleet().getFleetData().addFleetMember(newMember);

    // it needs crew too!
    float minCrew = newMember.getMinCrew();
    Global.getSector().getPlayerFleet().getCargo().addCrew((int) minCrew);

    String text = "Added " + newMember.getVariant().getFullDesignationWithHullName();
    Color color = Global.getSettings().getColor("textFriendColor");

    dialog.getTextPanel().addParagraph(text, color);

    return true;
  }
}
