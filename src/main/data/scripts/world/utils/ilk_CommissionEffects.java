package data.scripts.world.utils;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import java.util.Collection;
import org.apache.log4j.Level;

/** Reset pirate rep as a one-time thing upon accepting mayorate commission */
public class ilk_CommissionEffects implements EveryFrameScript {

  private boolean hasRun = false;
  FactionAPI player;

  public ilk_CommissionEffects() {
    Global.getLogger(ilk_CommissionEffects.class).setLevel(Level.DEBUG);
    player = Global.getSector().getPlayerFaction();
  }

  @Override
  public boolean isDone() {
    return hasRun;
  }

  @Override
  public boolean runWhilePaused() {
    return true;
  }

  @Override
  public void advance(float amount) {
    // fuck me... took absolutely forever to find the right memory keys
    /*Global.getLogger(ilk_CommissionEffects.class).log(Level.DEBUG, "====================================================");
    logKeys(Global.getSector().getPlayerFaction().getMemory().getKeys(), "playerFaction");
    logKeys(Global.getSector().getPlayerFleet().getMemory().getKeys(), "playerFleet");
    logKeys(Global.getSector().getPlayerPerson().getMemory().getKeys(), "playerPerson");
    logKeys(Global.getSector().getMemory().getKeys(), "Global");
    logKeys(Global.getSector().getCharacterData().getMemory().getKeys(), "CharData");*/

    if ((Global.getSector().getCharacterData().getMemory().get("$fcm_faction") != null)
        && (Global.getSector()
            .getCharacterData()
            .getMemory()
            .get("$fcm_faction")
            .equals("mayorate"))) {
      // player has a mayorate commission
      FactionAPI pirates = Global.getSector().getFaction(Factions.PIRATES);

      if (player.getRelationshipLevel(pirates).isAtBest(RepLevel.HOSTILE)) {
        // player is hostile to pirates, reset rep to inhospitable
        float before = player.getRelationship(pirates.getId());
        player.setRelationship(pirates.getId(), RepLevel.INHOSPITABLE);
        float after = player.getRelationship(pirates.getId());

        Global.getSector()
            .getCampaignUI()
            .addMessage("The Mayorate has interceded with the Sector's pirates on your behalf.");
        Global.getSector()
            .getCampaignUI()
            .addMessage("Your relationship with the pirates has been set to inhospitable.");
      }

      // target this script for cleanup and removal, only a one-time affair
      hasRun = true;
    }
  }

  /**
   * Debugging method to find out where memory keys are located
   *
   * @param toDump Set of memory keys to dump
   * @param label What prefix to add when dumping keys (so you can tell where the key came from)
   */
  private void logKeys(Collection<String> toDump, String label) {
    for (String key : toDump) {
      Global.getLogger(ilk_CommissionEffects.class).log(Level.DEBUG, label + "key: " + key);
    }
  }
}
