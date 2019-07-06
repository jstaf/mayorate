package ilk.world.economy;

import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;

public class Indoctrination extends BaseMarketConditionPlugin {

  public static final float INDOCTRINATION_PRODUCTION_MULT = 1.5f;
  public static final float STABILITY_INDOCTRINATION = 1f;
  public static final float INDOCTRINATION_ORGANS_MULT = 5f;

  @Override
  public void apply(String id) {
    market.getStability().modifyFlat(id, STABILITY_INDOCTRINATION, "Indoctrination");
    // TODO: Re-add supply changes without messing up prices.
  }

  @Override
  public void unapply(String id) {
    market.getStability().unmodify(id);
  }
}
