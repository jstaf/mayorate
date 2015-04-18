/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

/**
 *
 * @author Jeff
 */
public class BubbleShieldStats implements ShipSystemStatsScript {

    private boolean start = false;
    private float orig;
    ShieldAPI shield;
    
    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        if (!(stats.getEntity() instanceof ShipAPI)) {
            return;
        }
        ShipAPI ship = (ShipAPI) stats.getEntity();
        
        shield = ship.getShield();
        
        if (!start) {
            orig = shield.getArc();
            start = true;
        }
        
        //Expand shield to 360, fade in shield
        if (state == State.IN) {
            shield.setArc(effectLevel * (360 - orig) + orig);
            stats.getShieldDamageTakenMult().modifyMult(id, 1-effectLevel);
            stats.getShieldUnfoldRateMult().modifyMult(id, 3f);
        }
        
        if (state == State.ACTIVE) {
            shield.setArc(360f);
            stats.getShieldDamageTakenMult().modifyMult(id, 0f);
        }
        
        //Close shield
        if (state == State.OUT) {
            shield.setArc(effectLevel * (360 - orig) + orig);
            stats.getShieldDamageTakenMult().modifyMult(id, 1-effectLevel);
        }
                
        if ((effectLevel == 0) && (start)) {
            start = false;
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getShieldDamageTakenMult().unmodify(id);
        stats.getShieldUnfoldRateMult().unmodify(id);
        if (shield != null) {
            shield.setArc(orig);
        }
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData("invulnerable", false);
        }
        return null;
    }
    
}
