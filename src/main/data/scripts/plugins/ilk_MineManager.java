package data.scripts.plugins;

import java.awt.Color;
import java.util.List;

import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.GuidedMissileAI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.input.InputEventAPI;

import org.lazywizard.lazylib.combat.AIUtils;

/** This class manages deployed mines */
public class ilk_MineManager extends BaseEveryFrameCombatPlugin {

  CombatEngineAPI engine;

  private float interval = 0f;
  private static final float THRESHOLD = 0.1f;

  private float tickInterval = 0f;
  private static final float LARGE_THRESH = 1f;
  private static final Color PING_COLOR = new Color(235, 55, 0, 255);

  private static final float DECEL_CHANCE = 0.15f;
  private static final float DECEL_AMT = 0.1f;

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
    tickInterval += amount;

    if (interval > THRESHOLD) {
      interval = 0f;

      for (DamagingProjectileAPI proj : engine.getProjectiles()) {
        String spec = proj.getProjectileSpecId();
        if (spec == null) {
          return;
        }

        if (spec.contains("ilk_mine_mirv")) {
          // randomly decelerate our mine projectiles to give a nicer spread
          if (Math.random() < DECEL_CHANCE) {
            proj.getVelocity().scale(DECEL_AMT);
          }

          // do this only occasionally
          if (tickInterval > LARGE_THRESH) {

            // add a flash of light
            engine.addSmoothParticle(proj.getLocation(), proj.getVelocity(), 7f, 1f, 0.15f, PING_COLOR);

            // repick targets
            MissileAPI missile = (MissileAPI) proj;
            ShipAPI target = AIUtils.getNearestEnemy(missile);
            if (target != null)
              ((GuidedMissileAI) missile.getMissileAI()).setTarget(target);
          }
        }
      }
      // reset the counter
      if (tickInterval > LARGE_THRESH)
        tickInterval = 0f;
    }
  }

  @Override
  public void renderInUICoords(ViewportAPI viewport) {
    // ???
  }

  @Override
  public void renderInWorldCoords(ViewportAPI viewport) {
    // ???
  }
}
