package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class ilk_GravWell implements ShipSystemStatsScript {

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        if (!(stats.getEntity() instanceof ShipAPI)) {
            return;
        }

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
        }
        
        if (state == State.OUT) {
            
        }
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
