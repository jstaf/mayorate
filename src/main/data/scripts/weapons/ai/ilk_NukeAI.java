package data.scripts.weapons.ai;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.GuidedMissileAI;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.dark.shaders.light.LightShader;
import org.dark.shaders.light.StandardLight;
import org.lazywizard.lazylib.CollectionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class ilk_NukeAI implements MissileAIPlugin, GuidedMissileAI {

  private final MissileAPI missile;
  private CombatEntityAPI target;

  private static final float DETONATE_DISTANCE = 150f;
  private static final float VELOCITY_DAMPING_FACTOR = 0.15f;

  private float timeLive = 0f;

  private static ShipAPI findBestTarget(MissileAPI missile) {
    ShipAPI source = missile.getSource();
    if (source != null && source.getShipTarget() != null && !source.getShipTarget().isHulk()) {
      return source.getShipTarget();
    }

    ShipAPI closest = null;
    float distance, closestDistance = Float.MAX_VALUE;

    for (ShipAPI tmp : AIUtils.getEnemiesOnMap(missile)) {
      float mod = 0f;
      if (tmp.isFighter() || tmp.isDrone() || tmp.getCollisionClass() == CollisionClass.NONE) {
        mod = 4000f;
      }
      distance = MathUtils.getDistance(tmp, missile.getLocation()) + mod;
      if (distance < closestDistance) {
        closest = tmp;
        closestDistance = distance;
      }
    }

    return closest;
  }

  public ilk_NukeAI(MissileAPI missile, ShipAPI launchingShip) {
    this.missile = missile;

    List<ShipAPI> directTargets =
        CombatUtils.getShipsWithinRange(launchingShip.getMouseTarget(), 100f);
    if (!directTargets.isEmpty()) {
      Collections.sort(
          directTargets,
          new CollectionUtils.SortEntitiesByDistance(launchingShip.getMouseTarget()));
      ListIterator<ShipAPI> iter = directTargets.listIterator();
      while (iter.hasNext()) {
        ShipAPI tmp = iter.next();
        if (!tmp.isHulk()
            && tmp.getOwner() != launchingShip.getOwner()
            && !tmp.isDrone()
            && !tmp.isFighter()) {
          setTarget(tmp);
          break;
        }
      }
    }

    if (target == null) {
      target = launchingShip.getShipTarget();
    }

    if (target == null) {
      setTarget(findBestTarget(missile));
    }
  }

  public static void detonate(MissileAPI missile) {

    Global.getCombatEngine()
        .spawnExplosion(
            missile.getLocation(), new Vector2f(), new Color(255, 121, 117, 255), 500f, 0.5f);
    Global.getCombatEngine()
        .addHitParticle(
            missile.getLocation(), new Vector2f(), 400f, 1f, 2f, new Color(255, 255, 255, 200));
    Global.getCombatEngine()
        .addHitParticle(
            missile.getLocation(), new Vector2f(), 1000f, 1f, 2f, new Color(255, 121, 117, 255));

    RippleDistortion shockwave = new RippleDistortion();
    shockwave.setLocation(missile.getLocation());
    shockwave.setIntensity(1f);
    shockwave.setLifetime(1.5f);
    shockwave.setSize(500f);
    shockwave.setFrameRate(84f);
    shockwave.setCurrentFrame(10);
    shockwave.fadeOutIntensity(1f);
    DistortionShader.addDistortion(shockwave);

    StandardLight light = new StandardLight();
    light.setLocation(missile.getLocation());
    light.setColor(new Color(255, 121, 117, 255));
    light.setSize(1500f);
    light.setIntensity(2f);
    light.fadeOut(0.5f);
    LightShader.addLight(light);

    Global.getSoundPlayer()
        .playSound("ilk_nuke_detonate", 1f, 1f, missile.getLocation(), new Vector2f());

    // spawn stuff to cause damage
    Global.getCombatEngine()
        .spawnProjectile(
            missile.getSource(),
            missile.getWeapon(),
            "ilk_nuke_primaryDet",
            missile.getLocation(),
            0f,
            null);
    Global.getCombatEngine()
        .applyDamage(
            missile,
            missile.getLocation(),
            missile.getHitpoints() * 100f,
            DamageType.FRAGMENTATION,
            0f,
            false,
            false,
            missile);
    for (int i = 0; i < 60; i++) {
      float angle = (float) (i - 1) * 6f;
      Vector2f location = MathUtils.getPointOnCircumference(missile.getLocation(), 10f, angle);
      Global.getCombatEngine()
          .spawnProjectile(
              missile.getSource(), missile.getWeapon(), "ilk_nuke_damage", location, angle, null);
    }
  }

  @Override
  public void advance(float amount) {
    if (missile.isFizzling() || missile.isFading()) {
      if (target != null) {
        float distance = MathUtils.getDistance(target.getLocation(), missile.getLocation());
        if (distance
            <= DETONATE_DISTANCE + target.getCollisionRadius() + missile.getCollisionRadius()) {
          detonate(missile);
        }
      }
      return;
    }

    timeLive += amount;

    if (target == null
        || (target instanceof ShipAPI && (((ShipAPI) target).isHulk()))
        || target.getCollisionClass() == CollisionClass.NONE
        || (missile.getOwner() == target.getOwner())
        || !Global.getCombatEngine().isEntityInPlay(target)) {
      setTarget(findBestTarget(missile));
      if (target == null) {
        missile.giveCommand(ShipCommand.ACCELERATE);
        return;
      }
    }

    float distance = MathUtils.getDistance(target.getLocation(), missile.getLocation());
    Vector2f guidedTarget = target.getLocation();

    if (distance <= DETONATE_DISTANCE + target.getCollisionRadius() + missile.getCollisionRadius()
        && timeLive >= 2f) {
      timeLive = -99999f;
      detonate(missile);
      return;
    }

    float angularDistance =
        MathUtils.getShortestRotation(
            missile.getFacing(), VectorUtils.getAngle(missile.getLocation(), guidedTarget));
    float absDAng = Math.abs(angularDistance);

    missile.giveCommand(angularDistance < 0 ? ShipCommand.TURN_RIGHT : ShipCommand.TURN_LEFT);

    missile.giveCommand(ShipCommand.ACCELERATE);

    if (absDAng < 5) {
      float MFlightAng = VectorUtils.getAngle(new Vector2f(0, 0), missile.getVelocity());
      float MFlightCC = MathUtils.getShortestRotation(missile.getFacing(), MFlightAng);
      if (Math.abs(MFlightCC) > 20) {
        missile.giveCommand(MFlightCC < 0 ? ShipCommand.STRAFE_LEFT : ShipCommand.STRAFE_RIGHT);
      }
    }

    if (absDAng < Math.abs(missile.getAngularVelocity()) * VELOCITY_DAMPING_FACTOR) {
      missile.setAngularVelocity(angularDistance / VELOCITY_DAMPING_FACTOR);
    }
  }

  @Override
  public CombatEntityAPI getTarget() {
    return target;
  }

  @Override
  public final void setTarget(CombatEntityAPI target) {
    this.target = target;
  }
}
