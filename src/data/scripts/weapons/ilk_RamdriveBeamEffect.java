package data.scripts.weapons;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;

public class ilk_RamdriveBeamEffect extends ilk_BurstBeamEffect implements BeamEffectPlugin {

    @Override
    public void advance(float delta, CombatEngineAPI combatEngineAPI, BeamAPI beamAPI) {
        super.advance(delta, combatEngineAPI, beamAPI);
    }
}
