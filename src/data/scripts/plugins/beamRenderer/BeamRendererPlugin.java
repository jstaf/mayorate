package data.scripts.plugins.beamRenderer;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.input.InputEventAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

/** Draw arbitrary beam sprites wherever you need them and fade them out. Has none of the drawbacks of the old code (static beams, one-time damage).
 *  @author Tartiflette and Deathfly (complete and TOTAL rewrite by kazi)
 */
public class BeamRendererPlugin extends BaseEveryFrameCombatPlugin {

    private CombatEngineAPI engine;

    private static ArrayList<BeamSpec> beamsToRender = new ArrayList<>();
    private ArrayList<BeamSpec> toRemove = new ArrayList<>();

    // add beams to the rendering/damage thread this way (by creating a NEW beamSpec object using the constructor)
    public static void addBeam(BeamSpec newBeam) {
        beamsToRender.add(newBeam);
        newBeam.calcImpactPoint();

        // only draw flashes once
        newBeam.engine.addHitParticle(newBeam.startLoc, new Vector2f(),
                (float) Math.random() * newBeam.size / 2 + newBeam.size,
                1,
                (float) Math.random() * newBeam.duration / 2 + newBeam.duration,
                newBeam.beamColor);
        if (newBeam.target != null) {
            newBeam.engine.addHitParticle(newBeam.hitLoc, newBeam.target.getVelocity(),
                    (float) Math.random() * newBeam.size / 4 + newBeam.size / 2,
                    1, 0.1f, Color.WHITE);
            newBeam.engine.addHitParticle(newBeam.hitLoc, newBeam.target.getVelocity(),
                    (float) Math.random() * newBeam.size / 2 + newBeam.size,
                    1,
                    (float) Math.random() * newBeam.duration / 2 + newBeam.duration,
                    newBeam.beamColor);
        }
    }

    @Override
    public void init(CombatEngineAPI combatEngineAPI) {
        engine = Global.getCombatEngine();
        //reinitialize the map
        beamsToRender.clear();
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if (engine == null || engine.isPaused()) {
            return;
        }

        // recalculate render coords and apply damage
        for (BeamSpec beam : beamsToRender) {
            beam.update(amount);
        }
    }

    @Override
    public void renderInWorldCoords(ViewportAPI view) {
        if (engine == null) {
            return;
        }

        if (!beamsToRender.isEmpty()) {
            // iterate through the beams, rendering each in turn
            for (BeamSpec beam : beamsToRender) {
                if (beam.isDone) {
                    toRemove.add(beam);
                    continue;
                }

                //draw the beam
                SpriteAPI sprite = beam.sprite;
                sprite.setAlphaMult(beam.opacity);
                sprite.setSize(MathUtils.getDistance(beam.startLoc, beam.hitLoc) + 50f, beam.width);
                sprite.setAngle(beam.aim);
                Vector2f center = MathUtils.getMidpoint(beam.startLoc, beam.hitLoc);
                sprite.renderAtCenter(center.x, center.y);
            }

            //remove the beams that are done
            if (!toRemove.isEmpty()) {
                beamsToRender.removeAll(toRemove);
                toRemove.clear();
            }
        }
    }
}
