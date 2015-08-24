package data.scripts.plugins.pulseRenderer;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 22/08/15.
 */
public class ilk_GravPulseInjector extends BaseEveryFrameCombatPlugin {

    private CombatEngineAPI engine;
    private float interval = 0f;
    private static final float THRESHOLD = 0.1f;

    static final float PULSE_SIZE = 700f;
    static final float RING_SIZE = 200f;
    static final float PER_FRAME_DAMAGE = 100f;
    static final float PULSE_SPEED = 350f;

    static SpriteAPI indicator = Global.getSettings().getSprite("util", "indicator");
    static {
        indicator.setAdditiveBlend();
    }

    private static ArrayList<ilk_GravPulseSpec> pulses = new ArrayList<>();
    private static ArrayList<ilk_GravPulseSpec> toRemove = new ArrayList<>();

    @Override
    public void init(CombatEngineAPI combatEngineAPI) {
        engine = Global.getCombatEngine();
        pulses.clear();
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if (engine == null || engine.isPaused()) {
            return;
        }

        interval += amount;

        if (interval > THRESHOLD) {

            for (ilk_GravPulseSpec pulse : pulses) {
                if (pulse.done) continue;

                if (pulse.systemState == ShipSystemStatsScript.State.IN) {
                    Vector2f shipLoc = pulse.myShip.getLocation();
                }

                if (pulse.systemState == ShipSystemStatsScript.State.OUT) {
                    List<CombatEntityAPI> inRing = CombatUtils.getEntitiesWithinRange(pulse.pulseLoc, pulse.currentRing);
                    inRing.removeAll(CombatUtils.getEntitiesWithinRange(pulse.pulseLoc, pulse.currentSize));

                    int owner = pulse.myShip.getOwner();

                    // normalize damage to time since last frame
                    float damage = PER_FRAME_DAMAGE * (pulse.level - interval);
                    for (CombatEntityAPI entity : inRing) {
                        if (entity.getOwner() == owner) continue;

                        Vector2f direction = new Vector2f();
                        Vector2f.sub(entity.getLocation(), pulse.pulseLoc, direction);
                        CombatUtils.applyForce(entity, direction, 1000f * (pulse.level - interval));

                        engine.applyDamage(entity, entity.getLocation(), damage, DamageType.ENERGY, 0f, false, true, pulse.myShip);
                    }

                    interval = 0f;
                }
            }
        }
    }

    @Override
    public void renderInWorldCoords(ViewportAPI view) {
        if (engine == null) {
            return;
        }

        for (ilk_GravPulseSpec pulse : pulses) {
            if (pulse.done) {
                toRemove.add(pulse);
                continue;
            }

            if (pulse.systemState == ShipSystemStatsScript.State.ACTIVE) pulse.setLoc();

            if (pulse.systemState == ShipSystemStatsScript.State.OUT) {
                pulse.setLoc();
                indicator.setAlphaMult(1f);
                // test render of indicator sprites
                indicator.setSize(700f, 700f);
                indicator.renderAtCenter(pulse.pulseLoc.x, pulse.pulseLoc.y);
                indicator.setSize(pulse.currentSize, pulse.currentSize);
                indicator.renderAtCenter(pulse.pulseLoc.x, pulse.pulseLoc.y);
            }
        }
        pulses.removeAll(toRemove);
        toRemove.clear();
    }

    public static void add(ilk_GravPulseSpec newPulse) {
        pulses.add(newPulse);
    }
}

