/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import java.awt.Color;
import java.util.List;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Jeff
 */
public class ilk_Jammer implements ShipSystemStatsScript {

    private static final float SYSTEM_RANGE = 600f;
    private static final float DAM_AMOUNT = 200f;
    private static final float EMP_DAMAGE = 200f;
    private static final String HIT_SOUND_ID = "system_emp_emitter_impact";
    
    private static final float fraction = 0.5f;

    private boolean isActive;
    private CombatEngineAPI engine = Global.getCombatEngine();

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        if (!(stats.getEntity() instanceof ShipAPI)) {
            return;
        }
        ShipAPI ship = (ShipAPI) stats.getEntity();

        if (state == State.IN) {
            isActive = true;

        }

        if (state == State.ACTIVE) {

            if (isActive && (ship.getShipTarget() != null)) {
                ShipAPI target = ship.getShipTarget();
                float range = MathUtils.getDistance(ship, target);

                // dooooo stuffff
                if ((!target.isPhased()) && (range <= SYSTEM_RANGE)) {
                    Vector2f point = CollisionUtils.getCollisionPoint(ship.getLocation(), target.getLocation(), target); //placeholder
                    engine.spawnEmpArc(ship, point, ship, target, DamageType.FRAGMENTATION, DAM_AMOUNT, EMP_DAMAGE, range, HIT_SOUND_ID, 700f, Color.orange, Color.yellow);
                    engine.addHitParticle(point, target.getVelocity(), 0.5f, 20f, 0.2f, Color.yellow);
                    
                    ShieldAPI shield = target.getShield();
                    if (shield != null) {
                        //is the shield off or facing away?
                        if ((shield.isOff()) || (!shield.isWithinArc(point))) {
                            List<WeaponAPI> weapons = target.getAllWeapons();
                            for (WeaponAPI weapon: weapons) {
                                if (Math.random() > fraction) {
                                    weapon.setRemainingCooldownTo(5f);
                                }
                            }
                            
                            List<ShipEngineAPI> engines = target.getEngineController().getShipEngines();
                            for (ShipEngineAPI selectEngine: engines) {
                                if (Math.random() > fraction) {
                                    selectEngine.applyDamage(200f, ship);
                                    engine.spawnExplosion(point, target.getVelocity(), Color.yellow, 10f, 0.3f);
                                }
                            }
                        }
                    }
                }
                isActive = false;
            }
        }

        if (state == State.OUT) {

            //nothing while on cooldown
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        //nothing....
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        return null;
    }

}
