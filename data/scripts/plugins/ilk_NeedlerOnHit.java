package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;
import java.awt.Toolkit;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;

public class ilk_NeedlerOnHit implements OnHitEffectPlugin {

    public static int needleCount = 0; //number of needles currently tracked
    public int waitTime = 2000; //time to detonation in milliseconds after first hit
    public CombatEntityAPI target;
    public Vector2f point;
    
    ActionListener action = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                TestSound();
            }
        };
    Timer countDown = new Timer(waitTime, action);
    
    //Timer countdown = new Timer();
    //TimerTask endTimer = new Detonate();
    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
            Vector2f point, boolean shieldHit, CombatEngineAPI engine) {
        //reset timer if present, add a needle, start countdown to detonation
        countDown.stop();
                
        needleCount++;
                
        countDown.setRepeats(false);
        countDown.start();
        //countdown.schedule(endTimer, waitTime);
        //countdown.schedule(new TestSound(), waitTime);
    }

    public void TestSound() {
        Toolkit.getDefaultToolkit().beep();
        countDown.stop();
    }


    /*    class Detonate extends TimerTask {

     private float damageMult;
     private final int damage = 50;

     @Override
     public void run() {
     if (needleCount < 5) {
     damageMult = 1f;
     } else if (needleCount >= 5 && needleCount < 10) {
     damageMult = 2f;
     } else if (needleCount >= 10 && needleCount < 15) {
     damageMult = 3f;
     } else {
     damageMult = 4f;
     }

     //deals damage and resets needle count
     Global.getCombatEngine().applyDamage(target, point, damage * damageMult,
     DamageType.HIGH_EXPLOSIVE, 0, true, true, target);
     Global.getCombatEngine().spawnExplosion(
     point, // Location
     target.getVelocity(), // Velocity
     Color.pink,
     50 + (float) Math.random() * 100, // Size
     2 + (float) Math.random() * 2); // Duration
     Global.getSoundPlayer().playSound("hit_heavy", 1, 1, point, target.getVelocity());

     needleCount = 0;
     }
     }
     */
}
