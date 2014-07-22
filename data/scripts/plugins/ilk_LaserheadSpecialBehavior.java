package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.util.IntervalUtil;
import java.util.HashSet;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class ilk_LaserheadSpecialBehavior implements EveryFrameCombatPlugin
{
    private static final String SOUND_ID = "ilk_laserhead_detonate";
    
    private static final Set LASERHEADPROJ_IDS = new HashSet();
    static
    {
        //add Projectile IDs here.
        LASERHEADPROJ_IDS.add("ilk_laserhead_shot");
    }

    private final IntervalUtil veryFastTracker = new IntervalUtil(0.05f, 0.1f);
    private CombatEngineAPI engine;

	
    private static Vector2f randomCircularVelocity(float velocity)
    {
        Vector2f newVel = MathUtils.getRandomPointOnCircumference(null, velocity);
        return newVel;
    }

    @Override
    public void advance(float amount, List events)
    {
        if (engine.isPaused())
        {
            return;
        }

        veryFastTracker.advance(amount);

        DamagingProjectileAPI proj;
        CombatEntityAPI entity;
        String spec;

        if(veryFastTracker.intervalElapsed())
        {

            for (Iterator iter = engine.getProjectiles().iterator(); iter.hasNext();)
            {
                proj = (DamagingProjectileAPI) iter.next();
                spec = proj.getProjectileSpecId();

                if(LASERHEADPROJ_IDS.contains(spec))
                {
                    Vector2f loc = proj.getLocation();
                    Vector2f vel = proj.getVelocity();
                    for(int i = 0; i < 20; i++)
                    {
                        Color color = new Color( // Color of explosion
                            241, // Red 191,43,14,225
                            139, // Green
                            114, // Blue
                            5 // Opacity
                            );
                        
                        Vector2f randomVel = randomCircularVelocity(MathUtils.getRandomNumberInRange(60f,200f));
                        //randomVel.x += vel.x;
                        randomVel.y += vel.y;
                        //spec + "_clone" means is, if its got the same name in its name (except the "_clone" part) then it must be that weapon.
                        engine.spawnProjectile(proj.getSource(), null, spec + "_clone", loc, MathUtils.getRandomNumberInRange(0f,360f), randomVel);
			
                        Global.getSoundPlayer().playSound(SOUND_ID, 1f, 1f, proj.getLocation(), proj.getVelocity());
                        
                        engine.spawnExplosion(
                            loc,
                            new Vector2f(
                                (float) 0f,(float) 0f),
                            color,
                            25, // Pixel radius of explosion
                            0.5f);
                         
                    }
                    engine.removeEntity(proj);
                    continue;
                }

            }

        }

    }

    @Override
    public void init(CombatEngineAPI engine)
    {
        this.engine = engine;
    }
}