package data.scripts.world.utils;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.events.CampaignEventPlugin;
import com.fs.starfarer.api.campaign.events.CampaignEventTarget;
import com.fs.starfarer.api.impl.campaign.ids.Events;

/**
 * Spawns a bounty every once and awhile so people have an opportunity to level up with the Mayorate if they want to
 */
public class ilk_BountySpawner implements EveryFrameScript {

    SectorAPI sector;
    LocationAPI system;
    MarketAPI market;
    float interval;
    private long lastSpawn;

    boolean init = false;
    CampaignEventPlugin bounty;

    public ilk_BountySpawner(SectorAPI sector, LocationAPI system, MarketAPI market, float dayInterval) {
        this.sector = sector;
        this.system = system;
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
        if (!init) {
            // spawn an initial event
            bounty = sector.getEventManager().primeEvent(new CampaignEventTarget(market), Events.SYSTEM_BOUNTY, null);
            bounty.startEvent();
            init = true;
        }

        if (sector.getClock().getElapsedDaysSince(lastSpawn) >= interval) {
            sector.getEventManager().endEvent(bounty);
        }
    }
}
