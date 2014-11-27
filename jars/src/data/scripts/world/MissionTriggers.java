package data.scripts.world;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignClockAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;

public class MissionTriggers implements EveryFrameScript {

    public static boolean Mission1Start = false;
    public static boolean Mission1Complete = false;
    private float playerLevel;
    private float MayorateRep;
    private float IndependentRep;
    
    private final SectorAPI sector = Global.getSector();
    private final FactionAPI player = sector.getFaction(Factions.PLAYER);
    private final FactionAPI mayorate = sector.getFaction("mayorate");
    private final StarSystemAPI system = sector.getStarSystem("Rasht");
    private SectorEntityToken mission_ilk;
    
    private long lastCheckTime = Long.MIN_VALUE;
    private final int dayThreshold = 1;
    
    @Override
    public void advance(float amount) {
        CampaignClockAPI clock = sector.getClock();
        
        if (clock.getElapsedDaysSince(lastCheckTime) >= dayThreshold) {
            lastCheckTime = clock.getTimestamp(); 
            
            if (player.isAtWorst(mayorate, RepLevel.WELCOMING)) {
                SectorEntityToken mission_ilk = system.addCustomEntity("interactIlkhanna", "Mission Nav Point", "ilk_mission_obj", "neutral");
                mission_ilk.setCircularOrbit( system.getEntityById("ilkhanna"), 30, 250, 100);
                mission_ilk.setInteractionImage("illustrations", "karimova");
            }
            
            if (Mission1Complete && (mission_ilk != null)) {
                //figure out how to destroy all of the relevant mission objects
            }
        }
    }

    @Override
    public boolean isDone() {
        return Mission1Complete;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }
    
}
