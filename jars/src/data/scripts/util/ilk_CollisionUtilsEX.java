// by Deathfly and Tartiflette
package data.scripts.util;

import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import java.awt.geom.Line2D;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class ilk_CollisionUtilsEX {

    /////////////////////////////////////////
    //                                     //
    //             SHIP HIT                //
    //                                     //
    /////////////////////////////////////////
    // return the collision point of segment segStart to segEnd and a ship (will consider shield).
    // if segment can not hit the ship, will return null.
    // if segStart hit the ship, will return segStart.
    // if segStart hit the shield, will return segStart.
    public static Vector2f getShipCollisionPoint(Vector2f segStart, Vector2f segEnd, ShipAPI ship) {

        // if target can not be hit, return null
        if (ship.getCollisionClass() == CollisionClass.NONE) {
            return null;
        }
        ShieldAPI shield = ship.getShield();

        // Check hit point when shield is off.
        if (shield == null || shield.isOff()) {
            return CollisionUtils.getCollisionPoint(segStart, segEnd, ship);
        } // If ship's shield is on, thing goes complicated...
        else {
            Vector2f circleCenter = shield.getLocation();
            float circleRadius = shield.getRadius();
            // calculate the shield collision point
            Vector2f tmp1 = getCollisionPointOnCircumference(segStart, segEnd, circleCenter, circleRadius);
            if (tmp1 != null) {
                // OK! hit the shield in face
                if (shield.isWithinArc(tmp1)) {
                    return tmp1;
                } else {
                    // if the hit come outside the shield's arc but it hit the shield's "edge", find that point.
                    boolean hit = false;
                    Vector2f tmp = new Vector2f(segEnd);

                    //the beam cannot go farther than it's max range or the hull
                    Vector2f hullHit = CollisionUtils.getCollisionPoint(segStart, segEnd, ship);
                    if (hullHit != null) {
                        tmp = hullHit;
                        hit = true;
                    }
                    Vector2f shieldEdge1 = MathUtils.getPointOnCircumference(circleCenter, circleRadius, MathUtils.clampAngle(shield.getFacing() + shield.getActiveArc() / 2));
                    Vector2f tmp2 = CollisionUtils.getCollisionPoint(segStart, tmp, circleCenter, shieldEdge1);
                    if (tmp2 != null) {
                        tmp = tmp2;
                        hit = true;
                    }
                    Vector2f shieldEdge2 = MathUtils.getPointOnCircumference(circleCenter, circleRadius, MathUtils.clampAngle(shield.getFacing() - shield.getActiveArc() / 2));
                    Vector2f tmp3 = CollisionUtils.getCollisionPoint(segStart, tmp, circleCenter, shieldEdge2);
                    if (tmp3 != null) {
                        tmp = tmp3;
                        hit = true;
                    }

                    // return null if do not hit anything.
                    return hit ? tmp : null;
                }
            }
        }
        return null;
    }

    /////////////////////////////////////////
    //                                     //
    //       CIRCLE COLLISION POINT        //
    //                                     //
    /////////////////////////////////////////
    // return the first intersection point of segment segStart to segEnd and circumference.
    // if segStart is outside the circle and segment can not intersection with the circumference, will return null.
    // if segStart is inside the circle, will return segStart.
    public static Vector2f getCollisionPointOnCircumference(Vector2f segStart, Vector2f segEnd, Vector2f circleCenter, float circleRadius) {

        Vector2f startToEnd = Vector2f.sub(segEnd, segStart, null);
        Vector2f startToCenter = Vector2f.sub(circleCenter, segStart, null);
        double ptLineDistSq = (float) Line2D.ptLineDistSq(segStart.x, segStart.y, segEnd.x, segEnd.y, circleCenter.x, circleCenter.y);
        float circleRadiusSq = circleRadius * circleRadius;
        
        // if lineStart is within the circle, return it directly
        if (startToCenter.lengthSquared() < circleRadiusSq) {
            return segStart;
        }

        // if lineStart is outside the circle and segment can not reach the circumference, return null
        if (ptLineDistSq > circleRadiusSq || startToCenter.length() - circleRadius > startToEnd.length()) {
            return null;
        }

        // calculate the intersection point.
        startToEnd.normalise(startToEnd);
        double dist = Vector2f.dot(startToCenter, startToEnd) - Math.sqrt(circleRadiusSq - ptLineDistSq);
        startToEnd.scale((float) dist);
        return Vector2f.add(segStart, startToEnd, null);
    }

    /////////////////////////////////////////
    //                                     //
    //             SHIELD HIT              //
    //                                     //
    /////////////////////////////////////////
    // SHOULD ONLY BE USED WHEN YOU ONLY NEED SHIELD COLLISION POINT!
    // if you need the check for a ship hit (considering it's shield), use getShipCollisionPoint instead.
    // return the collision point of segment segStart to segEnd and ship's shield.
    // if the segment can not hit the shield or if the ship has no shield, return null.
    // if ignoreHull = flase and the segment hit the ship's hull first, return null.
    // if segStart is inside the shield, will return segStart.
    public static Vector2f getShieldCollisionPoint(Vector2f segStart, Vector2f segEnd, ShipAPI ship, boolean ignoreHull) {
        // if target not shielded, return null
        ShieldAPI shield = ship.getShield();
        if (ship.getCollisionClass() == CollisionClass.NONE || shield == null || shield.isOff()) {
            return null;
        }
        Vector2f circleCenter = shield.getLocation();
        float circleRadius = shield.getRadius();
        // calculate the shield collision point
        Vector2f tmp1 = getCollisionPointOnCircumference(segStart, segEnd, circleCenter, circleRadius);
        if (tmp1 != null) {
            // OK! hit the shield in face
            if (shield.isWithinArc(tmp1)) {
                return tmp1;
            } else {
                // if the hit come outside the shield's arc but it hit the shield's "edge", find that point.                

                Vector2f tmp = new Vector2f(segEnd);
                boolean hit = false;

                Vector2f shieldEdge1 = MathUtils.getPointOnCircumference(circleCenter, circleRadius, MathUtils.clampAngle(shield.getFacing() + shield.getActiveArc() / 2));
                Vector2f tmp2 = CollisionUtils.getCollisionPoint(segStart, tmp, circleCenter, shieldEdge1);
                if (tmp2 != null) {
                    tmp = tmp2;
                    hit = true;
                }

                Vector2f shieldEdge2 = MathUtils.getPointOnCircumference(circleCenter, circleRadius, MathUtils.clampAngle(shield.getFacing() - shield.getActiveArc() / 2));
                Vector2f tmp3 = CollisionUtils.getCollisionPoint(segStart, tmp, circleCenter, shieldEdge2);
                if (tmp3 != null) {
                    tmp = tmp3;
                    hit = true;
                }

                // If we don't ignore hull hit, check if there is one...
                if (!ignoreHull && CollisionUtils.getCollisionPoint(segStart, tmp, ship) != null) {
                    return null;
                }
                // return null if do not hit shield.
                return hit ? tmp : null;
            }
        }
        return null;
    }
}
