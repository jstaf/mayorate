package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

/**
 * @author kazi
 */
public class ilk_GraserCharge extends ilk_BurstBeamEffect implements BeamEffectPlugin {

    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        super.advance(amount, engine, beam); // add beam spike and afterglow
        if (beam.didDamageThisFrame()) {
            Vector2f point = beam.getTo();
            float damage = beam.getWeapon().getDamage().getDamage() * amount * 5f;
            
            DamageType type = beam.getWeapon().getDamageType();
            float fluxMult = beam.getSource().getFluxTracker().getFluxLevel();
            
            //for debugging
            //Global.getCombatEngine().addFloatingDamageText(point, damage*fluxMult, Color.ORANGE, beam.getDamageTarget(), beam.getSource());
            
            Global.getCombatEngine().applyDamage(beam.getDamageTarget(), point, damage*fluxMult, type, 0, false, false, beam.getSource());
        }
    }
}
