package data.scripts.plugins;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.input.InputEventAPI;

import java.util.List;

public class MusicReset implements EveryFrameCombatPlugin {

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if (Global.getCurrentState().equals(GameState.TITLE) &&
                !Global.getSoundPlayer().getCurrentMusicId().equals("miscallenous_main_menu.ogg")) {
            Global.getSoundPlayer().playMusic(0, 0, "music_title");
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
}
