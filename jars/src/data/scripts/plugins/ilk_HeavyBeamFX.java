package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Level;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class ilk_HeavyBeamFX extends BaseEveryFrameCombatPlugin {

    private CombatEngineAPI engine;
    List<WeaponAPI> weaponsMonitor = new ArrayList<>();
    boolean[] wepIsCharging = new boolean[100];

    private static final Set<String> HEAVY_BEAM_IDS = new HashSet<>();

    static {
        HEAVY_BEAM_IDS.add("ilk_heavy_lance_left");
        HEAVY_BEAM_IDS.add("ilk_heavy_lance_right");
    }
    private static final String HEAVY_LANCE_CHARGE = "ilk_lanceH_start";

    static {
        // Set to Level.DEBUG if you need to track down a problem in the system
        Global.getLogger(ilk_HeavyBeamFX.class).setLevel(Level.ERROR);
    }

    @Override
    public void init(CombatEngineAPI engine) {
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if (engine != Global.getCombatEngine()) {
            this.engine = Global.getCombatEngine();
            return;
        }

        if (engine.isPaused()) {
            return;
        }

        List<ShipAPI> ships = engine.getShips();
        for (ShipAPI ship : ships) {
            if (ship.isHulk()) {
                continue;
            }

            //check ships for heavy beam weapons specified above
            List<WeaponAPI> shipWeapons = ship.getAllWeapons();
            for (WeaponAPI weapon : shipWeapons) {
                String weaponID = weapon.getId();
                //add to the weapons monitor if we haven't added it already
                if (HEAVY_BEAM_IDS.contains(weaponID)) {
                    if (!weaponsMonitor.contains(weapon)) {
                        weaponsMonitor.add(weapon);
                        //Global.getLogger(ilk_HeavyBeamFX.class).log(Level.DEBUG, "adding " + weapon.toString() + " to index @" + weaponsMonitor.indexOf(weapon));
                    }
                }
            }
        }

        //check if anything in the monitor is firing
        for (WeaponAPI weapon : weaponsMonitor) {
            float charge = weapon.getChargeLevel();
            int index = weaponsMonitor.indexOf(weapon);

            if ((charge > 0)) {
                double direction = weapon.getCurrAngle();
                Vector2f weaponLoc = weapon.getLocation();
                //spawn peripheral charge particles
                for (int i = 0; i< 2; i++) {
                    
                    float rand = (float) ((Math.random() - 0.5) * Math.PI/2 + direction); //angle at which particle will be spawned
                    Vector2f loc = MathUtils.getPointOnCircumference(weaponLoc, 30, rand);
                    engine.addSmoothParticle(
                            loc, //starting point
                            new Vector2f (-0.5f * (loc.x - weaponLoc.x), -0.5f * (loc.y - weaponLoc.y)), //moves back to origin
                            (float) Math.random() * 5, 0.5f, 1f, Color.orange);
                }
                //spawn primary charge particle
                engine.addHitParticle(weaponLoc, //fix the math on this later 
                        weapon.getShip().getVelocity(),
                        30 * charge, 1f, 0.05f, Color.orange);
                
                if (!wepIsCharging[index]) {
                    //play sounds only once                    
                    String firingID = weapon.getId();
                    switch (firingID) {
                        case "ilk_heavy_lance_left":
                            Global.getSoundPlayer().playSound(HEAVY_LANCE_CHARGE, 1f, 1f, weapon.getLocation(), weapon.getShip().getVelocity());
                            break;
                        case "ilk_heavy_lance_right":
                            Global.getSoundPlayer().playSound(HEAVY_LANCE_CHARGE, 1f, 1f, weapon.getLocation(), weapon.getShip().getVelocity());
                            break;
                    }
                    wepIsCharging[index] = true;
                }
            } else {
                wepIsCharging[index] = false;
            }
        }
    }

    @Override
    public void renderInUICoords(ViewportAPI viewport) {
        //???
    }

    @Override
    public void renderInWorldCoords(ViewportAPI viewport) {
        //???
    }
}
