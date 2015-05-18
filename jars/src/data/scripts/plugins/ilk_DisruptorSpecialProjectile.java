package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import java.awt.Color;
import java.util.List;
import org.apache.log4j.Level;
import org.lwjgl.util.vector.Vector2f;

public class ilk_DisruptorSpecialProjectile extends BaseEveryFrameCombatPlugin {
    
    CombatEngineAPI engine = Global.getCombatEngine();
    
    private static final Color EMP_CORE_COLOR = new Color(255, 255, 255, 255);
    private static final Color EMP_FRINGE_COLOR = new Color(232, 14, 86, 200);
    

    ilk_DisruptorSpecialProjectile(ShipAPI source, WeaponAPI weapon, String specID, Vector2f loc, float angle, Vector2f vel) {
        specID = specID.replace("_shot", "_helper");
        
        //ShipAPI ship, WeaponAPI weapon, java.lang.String weaponId, Vector2f point, float angle, Vector2f shipVelocity
        //Global.getLogger(ilk_DisruptorSpecialProjectile.class).log(Level.DEBUG, "velocity.x" + vel.x + " " + vel.y);
        CombatEntityAPI monitor = engine.spawnProjectile(source, weapon, specID, loc, angle, vel);
    }    
    
    @Override
    public void init(CombatEngineAPI engine) {
    }
    
    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if (engine.isPaused()) {
            return;
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
