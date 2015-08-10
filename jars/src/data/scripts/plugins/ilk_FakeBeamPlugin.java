//By Tartiflette and Deathfly
//draw arbitrary beam sprites wherever you need them and fade them out
package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

public class ilk_FakeBeamPlugin extends BaseEveryFrameCombatPlugin {

    //FAKEBEAMS is the core of the script, storing both the weapons that have a flash rendered and the index of the current sprite used    
    private static final List<FakeBeam> FAKEBEAMS = new ArrayList<>();
    private final List<FakeBeam> toRemove = new ArrayList<>();

    @Override
    public void init(CombatEngineAPI engine) {
        //reinitialize the map 
        FAKEBEAMS.clear();
    }

    @Override
    public void renderInWorldCoords(ViewportAPI view) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine == null) {
            return;
        }

        if (!FAKEBEAMS.isEmpty()) {
            float amount = (engine.isPaused() ? 0f : engine.getElapsedInLastFrame());

            //dig through the FAKEBEAMS
            for (FakeBeam fakeBeam : FAKEBEAMS) {

                //Time calculation
                float fadeIn = fakeBeam.getFadeInTimer();
                float liveTime = fakeBeam.getDurationTimer();
                float fadeOut = fakeBeam.getFadeOutTimer();
                float opacity = 0;

                // check if the beam is fading in
                if (fadeIn > 0) {
                    fadeIn -= amount;
                    fakeBeam.setFadeInTimer(fadeIn);
                    opacity = (float) FastTrig.cos((fadeIn / fakeBeam.getFadeInDuration()) * (Math.PI / 2));
                    // check if the beam is on full power
                } else if (liveTime > 0) {
                    liveTime -= amount;
                    fakeBeam.setDurationTimer(liveTime);
                    opacity = 1;
                    // check if the beam is fading out
                } else if (fadeOut > 0) {
                    fadeOut -= amount;
                    fakeBeam.setFadeOutTimer(fadeOut);
                    opacity = (float) FastTrig.sin((fadeOut / fakeBeam.getFadeOutDuration()) * (Math.PI / 2));
                } else {
                    //completely faded out, remove the beam and skip               
                    toRemove.add(fakeBeam);
                    continue;
                }

                //draw the beam
                render(
                        fakeBeam.getSprite(), //Sprite to draw
                        fakeBeam.getColor(), //Sprite color
                        fakeBeam.getWidth() * opacity, //Width entry srinking with the opacity
                        fakeBeam.getLength(), //Height entry, multiplied by two because centered
                        fakeBeam.getAngleToRender(), //Angle entry
                        opacity, //opacity duh!
                        fakeBeam.getCenter()//Center
                );
            }

            //remove the beams that faded out
            //can't be done from within the iterator or it will fail when members will be missing
            if (!toRemove.isEmpty()) {
                for (FakeBeam w : toRemove) {
                    FAKEBEAMS.remove(w);
                }
                toRemove.clear();
            }
        }
    }

    private void render(SpriteAPI sprite, Color color, float width, float height, float angle, float opacity, Vector2f center) {
        //where the magic happen
        if (color != null) {
            sprite.setColor(color);
        }
        sprite.setAlphaMult(opacity);
        sprite.setSize(height, width);
        sprite.setAdditiveBlend();
        sprite.setAngle(angle);
        sprite.renderAtCenter(center.x, center.y);
    }
    
    // a class to record all we need to render a fake beam.
    public static class FakeBeam {

        protected SpriteAPI beamSprite = Global.getSettings().getSprite("graphics/fx/beamcore.png");
        protected Color beamColor = Color.white;
        protected float width = 0f;
        protected float length = 0f;
        protected float angleForRender = 0f;
        protected float duration = 0f;
        protected float durationTimer = 0f;
        protected float fadeInDuration = 0f;
        protected float fadeInTimer = 0f;
        protected float fadeOutDuration = 0f;
        protected float fadeOutTimer = 0f;
        protected Vector2f centerLoc = null;
        protected Vector2f form = null;
        protected Vector2f to = null;

        public void setSprite(SpriteAPI beamSprite) {
            this.beamSprite = beamSprite;
        }

        public SpriteAPI getSprite() {
            return beamSprite;
        }

        public void setColor(Color beamColor) {
            this.beamColor = beamColor;
        }

        public Color getColor() {
            return beamColor;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public float getWidth() {
            return width;
        }

        public void setLength(float length) {
            this.length = length;
            if (form != null) {
                setTo(MathUtils.getPointOnCircumference(form, length, angleForRender));
            }
        }

        public float getLength() {
            return length;
        }

        public void setCurrAngle(float currAngle) {
            if (form != null) {
                setTo(MathUtils.getPointOnCircumference(form, length, currAngle));
            }
        }

        public float getCurrAngle() {
            return angleForRender;
        }

        public float getAngleToRender() {
            return angleForRender;
        }

        public void setDuration(float duration) {
            this.duration = duration;
            this.durationTimer = duration;
        }

        public float getDuration() {
            return duration;
        }

        public void setDurationTimer(float durationTimer) {
            this.durationTimer = durationTimer;
        }

        public float getDurationTimer() {
            return durationTimer;
        }

        public void setFadeInDuration(float fadeInDuration) {
            this.fadeInDuration = fadeInDuration;
            this.fadeInTimer = fadeInDuration;
        }

        public float getFadeInDuration() {
            return fadeInDuration;
        }

        public void setFadeInTimer(float fadeInTimer) {
            this.fadeInTimer = fadeInTimer;
        }

        public float getFadeInTimer() {
            return fadeInTimer;
        }

        public void setFadeOutDuration(float fadeOutDuration) {
            this.fadeOutDuration = fadeOutDuration;
            this.fadeOutTimer = fadeOutDuration;
        }

        public float getFadeOutDuration() {
            return fadeOutDuration;
        }

        public void setFadeOutTimer(float fadeOutTimer) {
            this.fadeOutTimer = fadeOutTimer;
        }

        public float getFadeOutTimer() {
            return fadeOutTimer;
        }

        public void setForm(Vector2f form) {
            this.form = form;
            if (this.form != null && this.to != null) {
                this.centerLoc = MathUtils.getMidpoint(this.form, this.to);
                this.length = MathUtils.getDistance(form, to);
                this.angleForRender = VectorUtils.getAngle(form, to);
            }
        }

        public Vector2f getForm() {
            return form;
        }

        public void setTo(Vector2f to) {
            this.to = to;
            if (this.to != null && this.form != null) {
                this.centerLoc = MathUtils.getMidpoint(this.form, this.to);
                this.length = MathUtils.getDistance(form, to);
                this.angleForRender = VectorUtils.getAngle(form, to);
            }
        }

        public Vector2f getTo() {
            return to;
        }

        public void setCenter(Vector2f center) {
            this.centerLoc = center;
        }

        public Vector2f getCenter() {
            return centerLoc;
        }
    }

    // 4 methods for render fake beam, will return FakeBeam for some after rendered modify. 
    public static FakeBeam renderFakeBeam(Vector2f form, Vector2f to, float width, float duration, float fadeInDuration, float fadeOutDuration, SpriteAPI beamSprite, Color beamColor) {
        FakeBeam fakeBeam = new FakeBeam();
        if (beamSprite != null) {
            fakeBeam.setSprite(beamSprite);
        }
        fakeBeam.setColor(beamColor);
        fakeBeam.setForm(form);
        fakeBeam.setTo(to);
        fakeBeam.setWidth(width);
        fakeBeam.setDuration(duration);
        fakeBeam.setFadeInDuration(fadeInDuration);
        fakeBeam.setFadeOutDuration(fadeOutDuration);
        FAKEBEAMS.add(fakeBeam);
        return fakeBeam;
    }

    public static FakeBeam renderFakeBeam(Vector2f form, Vector2f to, float width, float duration, SpriteAPI beamSprite, Color beamColor) {
        return renderFakeBeam(form, to, width, duration - 0.5f, 0.25f, 0.25f, beamSprite, beamColor);
    }

    public static FakeBeam renderFakeBeam(Vector2f form, float length, float aim, float width, float duration, float fadeInDuration, float fadeOutDuration, SpriteAPI beamSprite, Color beamColor) {
        Vector2f to = MathUtils.getPointOnCircumference(form, length, aim);
        return renderFakeBeam(form, to, width, duration, fadeInDuration, fadeOutDuration, beamSprite, beamColor);
    }

    public static FakeBeam renderFakeBeam(Vector2f form, float length, float aim, float width, float duration, SpriteAPI beamSprite, Color beamColor) {
        Vector2f to = MathUtils.getPointOnCircumference(form, length, aim);
        return renderFakeBeam(form, to, width, duration, beamSprite, beamColor);
    }
}
