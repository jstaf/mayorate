package data.scripts.world;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.CoreCampaignPluginImpl;
import com.fs.starfarer.api.impl.campaign.CoreScript;
import com.fs.starfarer.api.impl.campaign.events.CoreEventProbabilityManager;
import com.fs.starfarer.api.impl.campaign.fleets.EconomyFleetManager;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import data.scripts.world.rasht.ilk_RashtGen;

public class mayorateGen implements SectorGeneratorPlugin {
    
    @Override
    public void generate(SectorAPI sector) {
        initFactionRelationships(sector);
        
        new ilk_RashtGen().generate(sector);
        
        sector.registerPlugin(new CoreCampaignPluginImpl());
	sector.addScript(new CoreScript());
	sector.addScript(new CoreEventProbabilityManager());
	sector.addScript(new EconomyFleetManager());
    }
    
    public static void initFactionRelationships(SectorAPI sector) {    
        FactionAPI hegemony = sector.getFaction(Factions.HEGEMONY);
	FactionAPI tritachyon = sector.getFaction(Factions.TRITACHYON);
	FactionAPI pirates = sector.getFaction(Factions.PIRATES);
	FactionAPI independent = sector.getFaction(Factions.INDEPENDENT);
	FactionAPI kol = sector.getFaction(Factions.KOL);
	FactionAPI church = sector.getFaction(Factions.LUDDIC_CHURCH);
	FactionAPI path = sector.getFaction(Factions.LUDDIC_PATH);
	FactionAPI player = sector.getFaction(Factions.PLAYER);
	FactionAPI diktat = sector.getFaction(Factions.DIKTAT);
        
        FactionAPI mayorate = sector.getFaction("mayorate");
        
        player.setRelationship(mayorate.getId(), RepLevel.COOPERATIVE);
        
        mayorate.setRelationship(path.getId(), RepLevel.VENGEFUL);
        mayorate.setRelationship(hegemony.getId(), RepLevel.HOSTILE);
        mayorate.setRelationship(pirates.getId(), RepLevel.WELCOMING);
        mayorate.setRelationship(diktat.getId(), RepLevel.NEUTRAL);
        mayorate.setRelationship(church.getId(), RepLevel.VENGEFUL);
        mayorate.setRelationship(kol.getId(), RepLevel.NEUTRAL);
        mayorate.setRelationship(tritachyon.getId(), RepLevel.COOPERATIVE);
        mayorate.setRelationship(independent.getId(), RepLevel.SUSPICIOUS);
    }
}
