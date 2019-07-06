package ilk.plugins;

import java.awt.Color;
import java.util.List;

import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.input.InputEventAPI;

import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.entities.AnchoredEntity;
import org.lwjgl.util.vector.Vector2f;

public class DisruptorPlugin extends BaseEveryFrameCombatPlugin {

  private static final float ARC_DISTANCE = 60f;
  private static final float ARC_WIDTH = 5f;
  private static final float HEAVY_ARC_DISTANCE = 100f;
  private static final float HEAVY_ARC_WIDTH = 15f;
  private static final Color EMP_CORE_COLOR = new Color(255, 255, 255, 255);
  private static final Color EMP_FRINGE_COLOR = new Color(232, 14, 86, 150);

  private float interval = 0f;
  private static final float THRESHOLD = 0.1f;
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
    if (interval > THRESHOLD) {
      interval = 0f;

      // look for projectiles and add them to the map!
      for (DamagingProjectileAPI proj : engine.getProjectiles()) {
        String spec = proj.getProjectileSpecId();
        if (spec == null) {
          return;
        }

        if (spec.contains("ilk_disruptor_shot") || spec.contains("ilk_heavy_disruptor_shot")) {
          createFX(proj, spec);
        }
      }
    }
  }

  private void createFX(DamagingProjectileAPI projectile, String id) {
    Vector2f loc = projectile.getLocation();
    switch (id) {
    case "ilk_disruptor_shot":
      Vector2f vel = (Vector2f) new Vector2f(projectile.getVelocity()).normalise()
          .scale(ARC_DISTANCE * (float) Math.random());
      Vector2f point = Vector2f.sub(loc, vel, new Vector2f());
      CombatEntityAPI anchor1 = new AnchoredEntity(projectile, MathUtils.getRandomPointOnCircumference(point, 5));
      engine.spawnEmpArc(projectile.getSource(), loc, // source, startLocation
          anchor1, // anchor
          anchor1, // target
          DamageType.ENERGY, 0f, 0f, // damage stats
          1000f, null, ARC_WIDTH, EMP_FRINGE_COLOR, EMP_CORE_COLOR); // maxrange, sfx, width, fringe, core
      break;
    case "ilk_heavy_disruptor_shot":
      Vector2f vel2 = (Vector2f) new Vector2f(projectile.getVelocity()).normalise()
          .scale(HEAVY_ARC_DISTANCE * (float) Math.random() * (float) Math.random());
      Vector2f point2 = Vector2f.sub(loc, vel2, new Vector2f());
      CombatEntityAPI anchor2 = new AnchoredEntity(projectile, MathUtils.getRandomPointOnCircumference(point2, 5));
      engine.spawnEmpArc(projectile.getSource(), loc, // source, startLocation
          anchor2, // anchor
          anchor2, // target
          DamageType.ENERGY, 0f, 0f, // damage stats
          1000f, null, HEAVY_ARC_WIDTH, EMP_FRINGE_COLOR, EMP_CORE_COLOR); // maxrange, sfx, width, fringe, core
      break;
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
