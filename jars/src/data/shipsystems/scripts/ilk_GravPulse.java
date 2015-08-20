package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ilk_GravPulse implements ShipSystemStatsScript {

    public static final float PULSE_SIZE = 700f;
    public static final float RING_SIZE = 200f;
    public static final float PER_FRAME_DAMAGE = 100f;

    public static final float PULSE_SPEED = 350f;

    Vector2f pulseLoc;
    float lastEffectLevel = 0f;

    CombatEngineAPI engine;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        if (!(stats.getEntity() instanceof ShipAPI)) {
            return;
        }

        engine = Global.getCombatEngine();

        ShipAPI ship = (ShipAPI) stats.getEntity();
        
        if (state == State.IN) {
            Vector2f shipLoc = ship.getLocation();

            for (int i = 0; i < 3; i++) {
                Vector2f point = MathUtils.getRandomPointInCircle(shipLoc, ship.getCollisionRadius());
                //loc, vel, size, brightness, duration, color
                Global.getCombatEngine().addSmoothParticle(
                        point,
                        VectorUtils.getDirectionalVector(shipLoc, point),
                        10f,
                        0.5f,
                        1f,
                        new Color(1.0f, 0.1882353f, 0.0f, 0.0f));
            }
        }
        
        if (state == State.ACTIVE) {
            pulseLoc = ship.getLocation();
        }
        
        if (state == State.OUT) {
            float currentRing = effectLevel * PULSE_SIZE;
            float currentSize = currentRing - RING_SIZE;

            List<CombatEntityAPI> inRing = CombatUtils.getEntitiesWithinRange(pulseLoc, currentRing);
            inRing.removeAll(CombatUtils.getEntitiesWithinRange(pulseLoc, currentSize));

            int owner = ship.getOwner();

            // normalize damage to time since last frame
            float damage = PER_FRAME_DAMAGE * (effectLevel - lastEffectLevel);
            for (CombatEntityAPI entity : inRing) {
                if (entity.getOwner() == owner) continue;

                engine.applyDamage(entity, entity.getLocation(), damage, DamageType.ENERGY, 0f, false, true, ship);
            }
        }

        lastEffectLevel = effectLevel;
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        //does nothing
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData("grav pulse active", false);
        }
        return null;
    }

}
