package ilk.shipsystems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class BubbleShieldStats extends BaseShipSystemScript {
  // Instance data
  private boolean start = false;
  private float orig = -1.0f; // Initialize to an impossible number so we can detect initialization.

  @Override
  public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
    if (!(stats.getEntity() instanceof ShipAPI)) {
      return;
    }
    ShipAPI ship = (ShipAPI) stats.getEntity();
    ShieldAPI shield = ship.getShield();

    if (!start) {
      orig = shield.getArc();
      start = true;
    }

    switch (state) {
    case IN:
      // Expand shield to 360, fade in shield
      shield.setArc(effectLevel * (360 - orig) + orig);
      stats.getShieldDamageTakenMult().modifyMult(id, 1 - effectLevel);
      stats.getShieldUnfoldRateMult().modifyMult(id, 3f);
      break;
    case ACTIVE:
      shield.setArc(360f);
      stats.getShieldDamageTakenMult().modifyMult(id, 0f);
      break;
    case OUT:
      // Close shield
      shield.setArc(effectLevel * (360 - orig) + orig);
      stats.getShieldDamageTakenMult().modifyMult(id, 1 - effectLevel);
      break;
    case COOLDOWN:
    case IDLE:
      // Nothing to do.
    }

    if ((effectLevel == 0) && (start)) {
      start = false;
    }
  }

  @Override
  public void unapply(MutableShipStatsAPI stats, String id) {
    stats.getShieldDamageTakenMult().unmodify(id);
    stats.getShieldUnfoldRateMult().unmodify(id);
    if (orig < 0 || !(stats.getEntity() instanceof ShipAPI)) {
      return;
    }
    ShipAPI ship = (ShipAPI) stats.getEntity();
    ShieldAPI shield = ship.getShield();
    if (shield != null) {
      shield.setArc(orig);
    }
  }

  @Override
  public StatusData getStatusData(int index, State state, float effectLevel) {
    if (index == 0) {
      return new StatusData("invulnerable", false);
    }
    return null;
  }
}
