package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.input.InputEventAPI;
import java.awt.Color;
import java.util.*;

import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class ilk_ShotgunSpecialBehavior extends BaseEveryFrameCombatPlugin {

    private static final Set<String> SHOTGUNPROJ_IDS = new HashSet<>();
    
    static {
        //add Projectile IDs here.
        SHOTGUNPROJ_IDS.add("ilk_shotgun_shot");
        SHOTGUNPROJ_IDS.add("ilk_laserhead_shot");
    }

    private float interval = 0f;
    private static final float THRESHOLD = 1f;
    CombatEngineAPI engine;

    @Override
    public void init(CombatEngineAPI engine) {
        this.engine = engine;
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {    

        if (engine == null || engine.isPaused()) {
            return;
        }

        interval += amount;

        for (DamagingProjectileAPI proj : engine.getProjectiles()) {
            String spec = proj.getProjectileSpecId();

            if (SHOTGUNPROJ_IDS.contains(spec)) {
                Vector2f loc = proj.getLocation();
                Vector2f vel = proj.getVelocity();
                
                switch (spec) {                    
                    case "ilk_shotgun_shot":
                        for (int i = 0; i < 18; i++) {
                            Vector2f randomVel = MathUtils.getRandomPointOnCircumference(null, MathUtils.getRandomNumberInRange(0f, 120f));
                            randomVel.x += vel.x;
                            randomVel.y += vel.y;
                            //spec + "_clone" means is, if its got the same name in its name (except the "_clone" part) then it must be that weapon.
                            engine.spawnProjectile(proj.getSource(), proj.getWeapon(), spec + "_clone", loc, proj.getFacing(), randomVel);
                        }
                        engine.removeEntity(proj);
                        break;
                        
                    case "ilk_laserhead_shot":
                        // create a drone and spawn it
                        engine.getFleetManager(proj.getOwner()).spawnShipOrWing("ilk_laserhead_drone_wing", proj.getLocation(), proj.getFacing());
                        engine.removeEntity(proj);
                        break;
                }
            }
        }

        // check for drones that have fired every second or so and remove them from the game engine
        if (interval > THRESHOLD) {
            interval = 0f;

            for (ShipAPI drone : engine.getShips()) {
                //ignore ships that aren't our drone types
                if (drone.getHullSpec().getBaseHullId().equals("ilk_laserhead_drone")) {

                    // get drone weapon
                    WeaponAPI weapon = drone.getAllWeapons().get(0);

                    // remove drones that have fired from the engine
                    if (weapon.getCooldownRemaining() > 0.5f && !weapon.isFiring()) removeFromEngine(drone);

                    // remove drones that got messed up or otherwise missed their target
                    if (drone.getFullTimeDeployed() > 5f) removeFromEngine(drone);
                }
            }
        }
    }

    public void removeFromEngine(ShipAPI drone) {
        CombatFleetManagerAPI manager = engine.getFleetManager(drone.getOwner());
        manager.removeFromReserves(manager.getDeployedFleetMember(drone).getMember());
        engine.removeEntity(drone);
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
