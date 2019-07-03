package data.scripts.plugins;

import java.util.List;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.input.InputEventAPI;

public class ilk_MusicReset implements EveryFrameCombatPlugin {

  /**
   * Unfortunately, this hack was necessary to prevent mission music from bleeding
   * over into the title screen.
   *
   * @param amount
   * @param events
   */
  @Override
  public void advance(float amount, List<InputEventAPI> events) {
    try {
      if (Global.getCurrentState().equals(GameState.TITLE)
          && !Global.getSoundPlayer().getCurrentMusicId().equals("miscallenous_main_menu.ogg")) {
        // we're on the title screen, and the title music is not playing, oh no!
        Global.getSoundPlayer().playMusic(0, 0, "music_title");
      } else if (Global.getCurrentState().equals(GameState.COMBAT)) {
        // we are in combat
        if (Global.getCombatEngine().getMissionId() != null || Global.getCombatEngine().isSimulation()) {
          // we're in a mission or mission simulator, check for title music and get rid of
          // it
          if (Global.getSoundPlayer().getCurrentMusicId().equals("miscallenous_main_menu.ogg")) {
            Global.getSoundPlayer().playMusic(0, 0, "music_combat");
          }
        }
      }
    } catch (RuntimeException e) {
      // no one cares
    }
  }

  @Override
  public void renderInWorldCoords(ViewportAPI viewport) {
  }

  @Override
  public void renderInUICoords(ViewportAPI viewport) {
  }

  @Override
  public void init(CombatEngineAPI engine) {
  }

  @Override
  public void processInputPreCoreControls(float f, List<InputEventAPI> events) {
  }
}
