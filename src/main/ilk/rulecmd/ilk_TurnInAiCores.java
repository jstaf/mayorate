package ilk.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CharacterDataAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.CustomRepImpact;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.RepActionEnvelope;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.RepActions;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.AICores;
import com.fs.starfarer.api.util.Misc.Token;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import org.lazywizard.lazylib.MathUtils;

public class ilk_TurnInAiCores extends BaseCommandPlugin {

  private static final float PERSONAL_REP_MULT = 0.25f;
  private static final float BASE_REP_FOR_AI_CREW_CHANCE = 10.0f;
  private static final String AI_CREW_ID = "ilk_AICrew";
  private static final RepLevel MIN_AI_CREW_REP = RepLevel.WELCOMING;

  private InteractionDialogAPI dialog;
  private TextPanelAPI text;
  private Map<String, MemoryAPI> memoryMap;
  private PersonAPI person;
  private FactionAPI faction;
  private CargoAPI playerCargo;
  private CharacterDataAPI playerData;

  @Override
  public boolean execute(
      String ruleId,
      InteractionDialogAPI dialog,
      List<Token> params,
      Map<String, MemoryAPI> memoryMap) {
    String command = params.get(0).getString(memoryMap);
    if (command == null) return false;

    this.dialog = dialog;
    text = dialog.getTextPanel();
    this.memoryMap = memoryMap;
    person = dialog.getInteractionTarget().getActivePerson();
    faction = person.getFaction();
    playerCargo = Global.getSector().getPlayerFleet().getCargo();
    playerData = Global.getSector().getCharacterData();

    switch (command) {
      case "personCanAcceptCores":
        return personCanAcceptCores();
      case "selectCores":
        selectCores();
        return true;
      case "giveAiCrew":
        giveAiCrew();
        return true;
      default:
        return true;
    }
  }

  protected void selectCores() {
    float bounty = 0.0f;
    float reputation = 0.0f;
    float aiCrewChanceNotGiven = 1.0f;
    for (CargoStackAPI stack : playerCargo.getStacksCopy()) {
      CommoditySpecAPI spec = stack.getResourceIfResource();
      if (spec != null && spec.getDemandClass().equals(Commodities.AI_CORES)) {
        float baseRepValue = AICores.getBaseRepValue(spec.getId());
        reputation += baseRepValue * stack.getSize();
        bounty += spec.getBasePrice() * stack.getSize();

        aiCrewChanceNotGiven *=
            Math.pow(1 - baseRepValue / BASE_REP_FOR_AI_CREW_CHANCE, stack.getSize());

        AddRemoveCommodity.addCommodityLossText(
            stack.getCommodityId(), (int) stack.getSize(), text);
        playerCargo.removeStack(stack);
      }
    }

    bounty *= faction.getCustomFloat("AICoreValueMult");
    reputation *= faction.getCustomFloat("AICoreRepMult");

    playerCargo.getCredits().add(bounty);
    AddRemoveCommodity.addCreditsGainText((int) bounty, text);

    CustomRepImpact impact = new CustomRepImpact();
    impact.delta = reputation * 0.01f;
    Global.getSector()
        .adjustPlayerReputation(
            new RepActionEnvelope(RepActions.CUSTOM, impact, null, text, true), faction.getId());

    impact.delta *= 0.25f;
    Global.getSector()
        .adjustPlayerReputation(
            new RepActionEnvelope(RepActions.CUSTOM, impact, null, text, true), person);

    if (!playerData.knowsHullMod(AI_CREW_ID)
        && faction.getRelToPlayer().isAtWorst(MIN_AI_CREW_REP)
        && MathUtils.getRandomNumberInRange(0.0f, 1.0f) > aiCrewChanceNotGiven) {
      FireBest.fire(null, dialog, memoryMap, "ilkAiCoresTurnedInAiCrew");
    } else {
      FireBest.fire(null, dialog, memoryMap, "ilkAiCoresTurnedInDefault");
    }
  }

  protected void giveAiCrew() {
    playerData.addHullMod(AI_CREW_ID);

    String text = "AI Crew hullmod learned.";
    Color color = Global.getSettings().getColor("textFriendColor");
    dialog.getTextPanel().addParagraph(text, color);
  }

  protected boolean personCanAcceptCores() {
    return Ranks.POST_BASE_COMMANDER.equals(person.getPostId())
        || Ranks.POST_STATION_COMMANDER.equals(person.getPostId())
        || Ranks.POST_ADMINISTRATOR.equals(person.getPostId())
        || Ranks.POST_OUTPOST_COMMANDER.equals(person.getPostId());
  }
}
