package data.scripts.weapons;

import com.fs.starfarer.api.combat.WeaponAPI;

/**
 * A helper to determine what a weapon is currently doing
 */
public enum WeaponState {
    Idle, Charging, Firing, Cooldown;

    /**
     * What is the weapon currently doing?
     * @param weapon
     * @return state of the weapon
     */
    public static WeaponState getState(WeaponAPI weapon) {
        if (!weapon.isFiring()) {
            return Idle;
        } else if (weapon.getChargeLevel() == 1f) {
            return Firing;
        } else if (weapon.getCooldownRemaining() > 0f) {
            return Cooldown;
        } else {
            return Charging;
        }
    }
}
