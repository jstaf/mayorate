package data.shipsystems.scripts.ai;

import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;

/**
 * Created by jeff on 19/08/15.
 */
public class ilk_GravPulseAI implements ShipSystemAIScript {

    private float interval = 0f;
    private static final float THRESHOLD = 0.25f;

    private static final float CONSISDERATION_RANGE = 600f;

    //initialization variables
    private CombatEngineAPI engine;
    private ShipAPI ship;
    private ShipSystemAPI system;
    private ShipwideAIFlags aiFlags;

    @Override
    public void init(ShipAPI shipAPI, ShipSystemAPI shipSystemAPI, ShipwideAIFlags shipwideAIFlags, CombatEngineAPI combatEngineAPI) {
        engine = combatEngineAPI;
        ship = shipAPI;
        system = shipSystemAPI;
        aiFlags = shipwideAIFlags;
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        // should we even be running this AI script?
        if (engine == null) return;
        interval += amount;
        if (interval < THRESHOLD) return;
        if (!AIUtils.canUseSystemThisFrame(ship)) return;
        if (ship.isHoldFire()) return;

        List<MissileAPI> missiles = AIUtils.getNearbyEnemyMissiles(ship, CONSISDERATION_RANGE);
        float trigger = 0;
        for (MissileAPI missile : missiles) {
            if (((GuidedMissileAI) missile.getMissileAI()).getTarget().equals(ship)) trigger += 0.3;
        }

        List<ShipAPI> enemies = AIUtils.getNearbyEnemies(ship, CONSISDERATION_RANGE);
        for (ShipAPI enemy : enemies) {
            if (enemy.isFighter() || enemy.isDrone()) {
                trigger += 0.5f;
            } else {
                trigger++;
            }
        }

        if (trigger >= 4) ship.useSystem();
    }
}
