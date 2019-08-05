package ilk.plugins;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.input.InputEventAPI;

import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class ShotgunSpecialBehavior extends BaseEveryFrameCombatPlugin {

  private static final Set<String> SHOTGUNPROJ_IDS = new HashSet<>();
  static {
    // add Projectile IDs here.
    SHOTGUNPROJ_IDS.add("ilk_shotgun_shot");
    SHOTGUNPROJ_IDS.add("ilk_laserhead_shot");
  }

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

    for (DamagingProjectileAPI proj : engine.getProjectiles()) {
      String spec = proj.getProjectileSpecId();

      if (SHOTGUNPROJ_IDS.contains(spec)) {
        Vector2f loc = proj.getLocation();
        Vector2f vel = proj.getVelocity();

        switch (spec) {
        case "ilk_shotgun_shot":
          for (int i = 0; i < 12; i++) {
            Vector2f randomVel = MathUtils.getRandomPointOnCircumference(null,
                MathUtils.getRandomNumberInRange(0f, 70f));
            Vector2f projVel = new Vector2f();
            Vector2f.add(randomVel, vel, projVel);

            // spec + "_clone" means is, if its got the same name in its name (except the
            // "_clone"
            // part) then it must be that weapon.
            engine.spawnProjectile(proj.getSource(), proj.getWeapon(), spec + "_clone", loc, proj.getFacing(), projVel);
          }
          engine.removeEntity(proj);
          break;

        case "ilk_laserhead_shot":
          Vector2f fireLoc = proj.getLocation();
          BeamRendererPlugin.addBeam(new BeamSpec(engine, proj.getSource(), fireLoc, 700f, proj.getFacing(),
              proj.getDamageAmount(), DamageType.ENERGY, proj.getEmpAmount(), 0.5f, 0.1f, 0.2f, // duration, in, out
              "beams", "ilk_fakeBeamFX", 27, new Color(224, 184, 225, 175)));
          Global.getSoundPlayer().playSound("ilk_graser_fire", 1f, 1f, fireLoc, proj.getVelocity());

          engine.removeEntity(proj);
          break;
        }
      }
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
