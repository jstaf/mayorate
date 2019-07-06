package ilk.world.utils;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.events.CampaignEventPlugin;

/**
 * Spawns a bounty every once and awhile so people have an opportunity to level
 * up with the Mayorate if they want to
 */
public class BountySpawner implements EveryFrameScript {

  SectorAPI sector;
  LocationAPI system;
  MarketAPI market;
  float interval;
  private long lastSpawn;
  private boolean isDone;

  boolean init = false;
  CampaignEventPlugin bounty;

  public BountySpawner(SectorAPI sector, LocationAPI system, MarketAPI market, float dayInterval) {
    this.sector = sector;
    this.system = system;
    this.market = market;
    this.interval = dayInterval;
    isDone = false;

    lastSpawn = sector.getClock().getTimestamp();
  }

  // run indefinitely
  @Override
  public boolean isDone() {
    return isDone;
  }

  @Override
  public boolean runWhilePaused() {
    return false;
  }

  /**
   * Create bounty every "interval" days
   *
   * @param amount
   */
  @Override
  public void advance(float amount) {
    /*
     * TODO 0.9 if (!init && (sector.getClock().getElapsedDaysSince(lastSpawn) >=
     * 95)) { // spawn an initial event sector .getEventManager() .startEvent(new
     * CampaignEventTarget(market), Events.SYSTEM_BOUNTY, null); lastSpawn =
     * sector.getClock().getTimestamp(); init = true; } else if
     * (sector.getClock().getElapsedDaysSince(lastSpawn) >= interval) { sector
     * .getEventManager() .startEvent(new CampaignEventTarget(market),
     * Events.SYSTEM_BOUNTY, null); lastSpawn = sector.getClock().getTimestamp(); }
     */
  }
}
