package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import org.apache.log4j.Level;

import java.util.Random;

public class ilk_PathSpawnPoint extends BaseSpawnPoint {

    FactionAPI path;

    public ilk_PathSpawnPoint(SectorAPI sector, LocationAPI location,
                              float daysInterval, int maxFleets, SectorEntityToken anchor) {
        super(sector, location, daysInterval, maxFleets, anchor);
        path = Global.getSector().getFaction(Factions.LUDDIC_PATH);

        Global.getLogger(ilk_PathSpawnPoint.class).setLevel(Level.ALL);
    }

    @Override
    protected CampaignFleetAPI spawnFleet() {
        CampaignFleetAPI fleet = Global.getFactory().createEmptyFleet("luddic_path", "pathFleet", true);

        double rand = 0f;
        while (rand < 0.8f) {
            rand = Math.random();
            path.pickShipAndAddToFleet("combatSmall", 1f, fleet);
        }

        Global.getLogger(ilk_PathSpawnPoint.class).log(Level.DEBUG, "Spawned path fleet w/ " +
                fleet.getFleetData().getMembersListCopy().size() + " members.");
        getLocation().spawnFleet(getAnchor(), 0, 0, fleet);

        return fleet;
    }
}
