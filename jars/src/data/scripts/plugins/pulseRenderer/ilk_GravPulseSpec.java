package data.scripts.plugins.pulseRenderer;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import org.lwjgl.util.vector.Vector2f;

/**
 * Created by jeff on 22/08/15.
 */
public class ilk_GravPulseSpec {

    ShipAPI myShip;
    ShipSystemStatsScript.State systemState;
    float level;
    boolean done;
    Vector2f pulseLoc;

    float currentRing;
    float currentSize;

    public ilk_GravPulseSpec(ShipAPI ship) {
        myShip = ship;
        systemState = ShipSystemStatsScript.State.IN;
        level = 0f;
        done = false;
    }

    public void setLoc() {
        pulseLoc = myShip.getLocation();
    }

    public void setLevel(float newLevel) {
        level = newLevel;
        currentRing = level * ilk_GravPulseInjector.PULSE_SIZE;
        currentSize = currentRing - ilk_GravPulseInjector.RING_SIZE;
    }

    public void setDone(boolean bool) {
        done = bool;
    }

    public void setSystemState(ShipSystemStatsScript.State newState) {
        systemState = newState;
    }
}

