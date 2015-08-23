package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import data.scripts.plugins.pulseRenderer.ilk_GravPulseSpec;
import data.scripts.plugins.pulseRenderer.ilk_GravPulseInjector;


public class ilk_GravPulseStats implements ShipSystemStatsScript {

    private boolean triggered = false;
    private ilk_GravPulseSpec pulse;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        if (!(stats.getEntity() instanceof ShipAPI)) {
            return;
        }

        ShipAPI ship = (ShipAPI) stats.getEntity();
        if (pulse == null) pulse = new ilk_GravPulseSpec(ship);

        pulse.setLevel(effectLevel);

        if (state == State.IN) {
            if (!triggered) {
                triggered = true;
                ilk_GravPulseInjector.add(pulse);
            }
        }
        
        if (state == State.ACTIVE) {
            pulse.setSystemState(State.ACTIVE);
        }
        
        if (state == State.OUT) {
            pulse.setSystemState(State.OUT);
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        triggered = false;
        if (pulse != null) {
            pulse.setDone(true);
        }
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData("grav pulse active", false);
        }
        return null;
    }

}
