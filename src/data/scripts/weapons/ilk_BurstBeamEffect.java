package data.scripts.weapons;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import data.scripts.util.ilk_Interpolation;

public class ilk_BurstBeamEffect implements BeamEffectPlugin {

    private boolean init = false;
    private float startingWidth;
    private static final float SPIKE_MULT_VAL = 2.5f;
    private static final float SPIKE_MAX_DURATION = 0.05f;
    private float duration = 0f;

    @Override
    public void advance(float v, CombatEngineAPI combatEngineAPI, BeamAPI beamAPI) {
        if (!init) {
            init = true;
            startingWidth = beamAPI.getWidth();
        }

        beamSpike(beamAPI, v);
        afterglow(beamAPI);
    }

    /**
     * Cause the width to spike non-linearly
     * @param beamAPI
     */
    public void beamSpike(BeamAPI beamAPI, float delta) {
        if (beamAPI.getWeapon().isFiring()) {
            duration += delta;
            float amplitude;
            if (duration <= SPIKE_MAX_DURATION) {
                // spike in
                amplitude = duration / SPIKE_MAX_DURATION;
            } else {
                // spike out
                amplitude = 1f - ((duration - SPIKE_MAX_DURATION) / SPIKE_MAX_DURATION);
            }
            beamAPI.setWidth(ilk_Interpolation.linear(ilk_Interpolation.clamp(amplitude)) * (SPIKE_MULT_VAL - 1f) * startingWidth + startingWidth);
        } else {
            duration = 0f;
        }
    }

    /**
     * Draw a "searing afterglow"
     *
     * @param beamAPI
     */
    public void afterglow(BeamAPI beamAPI) {

    }
}
