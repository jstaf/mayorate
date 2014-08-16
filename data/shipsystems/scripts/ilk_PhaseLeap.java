package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.combat.ShipAPI;
import java.awt.Color;
import java.util.Iterator;
import static org.lazywizard.lazylib.CollisionUtils.getCollisionPoint;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class ilk_PhaseLeap implements ShipSystemStatsScript {

    //balance constants
    private static final float LEAP_DISTANCE = 600f;
    private static final float IMPACT_DAMAGE = 500f;
    private static final DamageType IMPACT_DAMAGE_TYPE = DamageType.ENERGY;

    //visual constants
    private static final String IMPACT_SOUND = "hit_solid";
    private static final Color EXPLOSION_COLOR = new Color(55, 160, 88);
    private static final float EXPLOSION_VISUAL_RADIUS = 200f;

    //don't touch these
    private boolean isActive = false;
    public Vector2f startLoc;
    public Vector2f endLoc;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        if (!(stats.getEntity() instanceof ShipAPI)) {
            return;
        }

        ShipAPI systemShip = (ShipAPI) stats.getEntity();

        if (state == State.IN) {
            if (!isActive) {
                isActive = true;
                //find ship location before teleport
                startLoc = systemShip.getLocation();
            }
        }
        if (state == State.OUT) {
            if (isActive) {
                //find ship location after teleport
                endLoc = systemShip.getLocation();
                for (Iterator iter = CombatUtils.getEntitiesWithinRange(systemShip.getLocation(), LEAP_DISTANCE).iterator(); iter.hasNext();) {
                    CombatEntityAPI inRangeObject = (CombatEntityAPI) iter.next();
                    if (inRangeObject == systemShip) {
                        //don't do anything if its the ship activating the system
                        continue;
                    }
                    if (inRangeObject.getOwner() == systemShip.getOwner()) {
                        //don't do anything to friendlies
                        continue;
                    }
                    if (inRangeObject.getOwner() != systemShip.getOwner()) {
                        //find point of impact between start and end location of jump
                        Vector2f pointOfImpact = getCollisionPoint(startLoc, endLoc, inRangeObject);

                        if (pointOfImpact != null) {
                            //only proceed if getCollisionPoint returned a point of impact
                            CombatEngineAPI engine = Global.getCombatEngine();
                            engine.spawnExplosion(pointOfImpact, inRangeObject.getVelocity(), EXPLOSION_COLOR, EXPLOSION_VISUAL_RADIUS, 0.2f);
                            Global.getSoundPlayer().playSound(IMPACT_SOUND, 1f, 1f, pointOfImpact, inRangeObject.getVelocity());
                            Global.getCombatEngine().applyDamage(inRangeObject, pointOfImpact, IMPACT_DAMAGE, IMPACT_DAMAGE_TYPE, 0, false, true, systemShip);

                        }
                    }
                }
            }
            isActive = false;
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        //nothing happens!
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData("generates hard flux", false);
        } else if (index == 1) {
            return new StatusData("damages objects in path", false);
        }
        return null;
    }
}
