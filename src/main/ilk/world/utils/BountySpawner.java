package ilk.world.utils;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.events.CampaignEventPlugin;
import com.fs.starfarer.api.impl.campaign.intel.SystemBountyManager;

import org.apache.log4j.Logger;

/**
 * Spawns a bounty every once and awhile so people have an opportunity to level
 * up with the Mayorate if they want to
 */
public class BountySpawner implements EveryFrameScript {
  private static final Logger logger = Global.getLogger(BountySpawner.class);

  SectorAPI sector;
  LocationAPI system;
  MarketAPI market;
  float interval;
  private long lastSpawn;

  boolean init = false;
  CampaignEventPlugin bounty;

  public BountySpawner(SectorAPI sector, MarketAPI market, float dayInterval) {
    this.sector = sector;
    this.market = market;
    this.interval = dayInterval;
    lastSpawn = sector.getClock().getTimestamp();
  }

  // run indefinitely
  @Override
  public boolean isDone() {
    return false;
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
    if (!init && (sector.getClock().getElapsedDaysSince(lastSpawn) >= 95)) {
      // spawn an initial event
      spawnBounty();
      init = true;
    } else if (sector.getClock().getElapsedDaysSince(lastSpawn) >= interval) {
      spawnBounty();
    }
  }

  private void spawnBounty() {
    logger.info("Spawning rasht bounty");
    if (market != null) {
      SystemBountyManager.getInstance().addOrResetBounty(market);
    }
    lastSpawn = sector.getClock().getTimestamp();
  }
}
