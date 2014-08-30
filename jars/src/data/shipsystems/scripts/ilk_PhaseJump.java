package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import java.awt.Color;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class ilk_PhaseJump implements ShipSystemStatsScript {

    //balance constants
    private static final float JUMP_FORCE = 1000f;
    private static final float CHECK_DISTANCE = 200f;
    private static final float IMPACT_DAMAGE = 100f;
    private static final DamageType IMPACT_DAMAGE_TYPE = DamageType.ENERGY;

    //visual constants
    private static final String IMPACT_SOUND = "hit_solid";
    private static final Color EXPLOSION_COLOR = new Color(166, 50, 91);
    private static final float EXPLOSION_VISUAL_RADIUS = 200f;

    //don't touch these
    private boolean isActive = false;
    private boolean within = false;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        if (!(stats.getEntity() instanceof ShipAPI)) {
            return;
        }

        ShipAPI ship = (ShipAPI) stats.getEntity();
        CollisionClass shipClass = (CollisionClass) ship.getCollisionClass();
        float jumpAngle;

        if (state == State.IN) {
            if (!isActive) {
                isActive = true;
                ship.setCollisionClass(CollisionClass.GAS_CLOUD);
                jumpAngle = ship.getFacing();
                CombatUtils.applyForce(ship, jumpAngle, JUMP_FORCE);
            }
        }

        if (state == State.ACTIVE) {
            for (CombatEntityAPI inRangeObject : CombatUtils.getEntitiesWithinRange(ship.getLocation(), CHECK_DISTANCE)) {
                if (inRangeObject == ship) {
                    //don't do anything if its the ship activating the system
                    continue;
                }
                if (inRangeObject.getOwner() == ship.getOwner()) {
                    //don't do anything to friendlies
                    continue;
                }

                within = CollisionUtils.isPointWithinBounds(ship.getLocation(), inRangeObject);
                //is our ship within the bounds of another?

                if (within) {
                    Vector2f pointOfImpact = ship.getLocation();
                    
                    Global.getCombatEngine().spawnExplosion(pointOfImpact, inRangeObject.getVelocity(), EXPLOSION_COLOR, EXPLOSION_VISUAL_RADIUS * 2f, 0.1f);
                    Global.getCombatEngine().spawnExplosion(pointOfImpact, inRangeObject.getVelocity(), EXPLOSION_COLOR, EXPLOSION_VISUAL_RADIUS, 0.5f);
                    Global.getSoundPlayer().playSound(IMPACT_SOUND, 1f, 1f, pointOfImpact, inRangeObject.getVelocity());
                    Global.getCombatEngine().applyDamage(inRangeObject, pointOfImpact, IMPACT_DAMAGE, IMPACT_DAMAGE_TYPE, 0, false, true, ship);

                }
                within = false;
            }
        }

        if (state == State.OUT) {
            if (isActive) {
                //reset collision class and slow ship down
                ship.setCollisionClass(shipClass);
                jumpAngle = ship.getFacing();
                CombatUtils.applyForce(ship, -jumpAngle, JUMP_FORCE);
            }
            isActive = false;
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id
    ) {
        //DOES NOTHING!!!
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData("ripping through space", false);
        }
        return null;
    }
}