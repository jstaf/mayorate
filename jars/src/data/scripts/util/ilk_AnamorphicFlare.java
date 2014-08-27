package data.scripts.util;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI;

import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lazywizard.lazylib.FastTrig;

public class ilk_AnamorphicFlare {

    /**
     * Anamorphic Flare Generator. This creates a lot of EMP flares in a tight
     * group, exploiting a bug in the game to create an effect similar to an
     * anamorphic flare. To create the effect you want, you may need to do a lot
     * of tweaking. Keep in mind that the effect has a higher resolution and a
     * greater computational cost with smaller thicknesses; extreme values (0.01
     * and smaller) can result in odd behavior. This can cause bugs if used in
     * some particular cases, such as on a weapon that spawns free-floating EMP
     * arcs.
     *
     * @param origin Put any ship in here. Really, any ship. This CANNOT be
     * null.
     * @param point Location of the anamorphic flare. You may need to offset
     * this to the right and downward to get it to be centered.
     * @param engine The base combat engine object.
     * @param brightness The visual brightness of the anamorphic flare. This can
     * also be used to attenuate its size, due to how it is shaped. Biased such
     * that 1 equals maximum brightness, but it is possible to go above maximum
     * brightness, especially on lower thicknesses, due to how the anamorphic
     * flare works.
     * @param thickness Thickness ratio of the anamorphic flare. A bit of a
     * misnomer; it won't actually get thinner, just longer, if you make this
     * smaller. Do not use a value above 0.5 because otherwise the flare won't
     * look like anything. The resolution of the flare and the precision of the
     * flare thickness and the computational complexity of the flare all
     * increase when the thickness is decreased. Values below 0.01 can have a
     * huge performance hit.
     * @param angle The angle the flare points toward, in degrees.
     * @param spread Controls how misshapen the flare will be, in degrees. Use
     * only for low thickness values (0.2 and below).
     * @param fringeGain Multiplies the alpha of the outside color of the
     * anamorphic flare, in case it wasn't colorful enough for you. Not sure if
     * this actually works... Default is 1.
     * @param fringeColor The outside color of the anamorphic flare. Alpha is
     * not used.
     * @param coreColor The core color of the anamorphic flare. Alpha is not
     * used.
     */
    public static void createFlare(ShipAPI origin, Vector2f point, CombatEngineAPI engine, float brightness, float thickness, float angle, float spread, float fringeGain, Color fringeColor, Color coreColor) {
        int magnitude = (int) (1f / thickness);
        int alpha = Math.min((int) (255f * brightness * thickness), 255);
        int alphaf = Math.min((int) (255f * brightness * thickness * fringeGain), 255);
        //point.x += (float) magnitude / 10f;
        //point.y -= (float) magnitude / 10f;

        for (int i = 0; i < magnitude; i++) {
            float angleP = angle + (float) Math.random() * 2f * spread - spread;
            if (angleP >= 360f) {
                angleP -= 360f;
            } else if (angleP < 0f) {
                angleP += 360f;
            }

            Vector2f location = point;
            location.x += FastTrig.cos(angleP * Math.PI / 180f);
            location.y += FastTrig.sin(angleP * Math.PI / 180f);

            Color fringeColorP = new Color(fringeColor.getRed(), fringeColor.getGreen(), fringeColor.getBlue(), alphaf);
            Color coreColorP = new Color(coreColor.getRed(), coreColor.getGreen(), coreColor.getBlue(), alpha);

            engine.spawnEmpArc(origin, location, null, new SimpleEntity(point),
                    DamageType.ENERGY,
                    0.0f,
                    0.0f, // emp 
                    100000f, // max range 
                    null,
                    25f, // thickness
                    fringeColorP,
                    coreColorP
            );
        }
    }

    private ilk_AnamorphicFlare() {

    }
}
