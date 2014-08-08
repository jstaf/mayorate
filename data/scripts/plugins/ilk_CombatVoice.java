package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import java.util.List;

public class ilk_CombatVoice implements EveryFrameCombatPlugin {

    private static final String DAMAGE_WARN = "damage_warning";
    private static boolean hull_warn = false;
    private CombatEngineAPI engine;

    private final IntervalUtil VoiceFrameTracker = new IntervalUtil(1f, 1f);

    @Override
    public void advance(float amount, List events) {
        //don't do anything if the game is paused
        if (engine.isPaused()) {
            return;
        }

        ShipAPI player;

        VoiceFrameTracker.advance(amount);
        player = engine.getPlayerShip();
        if (VoiceFrameTracker.intervalElapsed()) {
            //play DAMAGE_WARN sound if player health drops to 10%
            if ((player.getHitpoints() / player.getMaxHitpoints() < 0.1) && (hull_warn == false)) {
                Global.getSoundPlayer().playSound(DAMAGE_WARN, 1f, 1f, player.getLocation(), player.getVelocity());
                hull_warn = true;
            }
        }
    }

    @Override
    public void init(CombatEngineAPI engine) {
        this.engine = engine;
    }
}
