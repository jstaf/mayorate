package data.scripts.plugins;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.input.InputEventAPI;
import org.lazywizard.lazylib.CollisionUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages OnHitEffect projectiles capable of penetration
 */
public class ilk_PenetratorManager extends BaseEveryFrameCombatPlugin {

    private static CombatEngineAPI engine;
    private static final float UPDATE_INTERVAL = 0.1f;
    private float lastDelta = 0f;
    private static ArrayList<Penetrator> penetrators = new ArrayList<>();
    private static ArrayList<Penetrator> toRemove = new ArrayList<>();

    @Override
    public void init(CombatEngineAPI cengine) {
        engine = cengine;
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if (engine == null || engine.isPaused()) {
            return;
        }

        lastDelta += amount;
        if (lastDelta > UPDATE_INTERVAL) {
            lastDelta = 0f;
            for (Penetrator pen : penetrators) {
                pen.update();
                if (pen.killme) toRemove.add(pen);
            }
            penetrators.removeAll(toRemove);
        }
    }

    public static void addPenetrator(Vector2f pos, Vector2f vel, CombatEntityAPI victim, ShipAPI source, float damage,
                                     float emp, DamageType dmgtype, float falloff) {
        penetrators.add(new Penetrator(pos, vel, victim, source, damage, emp,dmgtype, falloff));
    }

    private static class Penetrator {
        Vector2f position = new Vector2f();
        Vector2f velocity = new Vector2f();

        float lastDamage;
        float lastEmp;
        DamageType type;
        CombatEntityAPI victim;
        ShipAPI source;

        final float falloff;
        boolean killme = false;

        Penetrator(Vector2f pos, Vector2f vel, CombatEntityAPI victim, ShipAPI source, float damage, float emp,
                   DamageType dmgtype, float falloff) {
            position = pos;
            velocity = vel;
            lastDamage = damage;
            lastEmp = emp;
            type = dmgtype;
            this.falloff = falloff;
            this.victim = victim;
            this.source = source;
        }

        /**
         * Update penetrators, deal damage and flag for removal
         */
        void update() {
            if (!CollisionUtils.isPointWithinBounds(position, victim) || lastDamage <= 10) {
                killme = true;
            } else {
                Vector2f.add(position, velocity, position);
                engine.applyDamage(victim, position, lastDamage, type, lastEmp, false, true, source);
                velocity.scale(falloff);
                lastDamage *= falloff;
            }
        }
    }
}
