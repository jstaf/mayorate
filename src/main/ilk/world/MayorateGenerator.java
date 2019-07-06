package ilk.world;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;

import ilk.MayorateModPlugin;
import ilk.world.systems.RashtGenerator;
import ilk.world.utils.CommissionEffects;

public class MayorateGenerator implements SectorGeneratorPlugin {

  @Override
  public void generate(SectorAPI sector) {
    initFactionRelationships(sector);
    new RashtGenerator().generate(sector);

    // only do the following if not in exerelin corvus mode
    if (!MayorateModPlugin.getIsExerelin()) {
      // add forgiveness script to avoid rep decay on mayorate commission from being
      // hostile with pirates
      sector.addScript(new CommissionEffects());
    }
  }

  public static void initFactionRelationships(SectorAPI sector) {
    FactionAPI mayorate = sector.getFaction("mayorate");

    FactionAPI hegemony = sector.getFaction(Factions.HEGEMONY);
    FactionAPI tritachyon = sector.getFaction(Factions.TRITACHYON);
    FactionAPI pirates = sector.getFaction(Factions.PIRATES);
    FactionAPI independent = sector.getFaction(Factions.INDEPENDENT);
    FactionAPI league = sector.getFaction(Factions.PERSEAN);
    FactionAPI kol = sector.getFaction(Factions.KOL);
    FactionAPI church = sector.getFaction(Factions.LUDDIC_CHURCH);
    FactionAPI path = sector.getFaction(Factions.LUDDIC_PATH);
    FactionAPI player = sector.getFaction(Factions.PLAYER);
    FactionAPI diktat = sector.getFaction(Factions.DIKTAT);

    player.setRelationship(mayorate.getId(), RepLevel.NEUTRAL);

    mayorate.setRelationship(path.getId(), RepLevel.VENGEFUL);
    mayorate.setRelationship(hegemony.getId(), RepLevel.HOSTILE);
    mayorate.setRelationship(pirates.getId(), RepLevel.NEUTRAL);
    mayorate.setRelationship(diktat.getId(), RepLevel.SUSPICIOUS);
    mayorate.setRelationship(church.getId(), RepLevel.HOSTILE);
    mayorate.setRelationship(kol.getId(), RepLevel.SUSPICIOUS);
    mayorate.setRelationship(tritachyon.getId(), RepLevel.NEUTRAL);
    mayorate.setRelationship(independent.getId(), RepLevel.NEUTRAL);
    mayorate.setRelationship(league.getId(), RepLevel.SUSPICIOUS);

    // mod factions
    mayorate.setRelationship("scy", RepLevel.HOSTILE);
    mayorate.setRelationship("sun_ice", RepLevel.HOSTILE);
    mayorate.setRelationship("sun_ici", RepLevel.HOSTILE);
    mayorate.setRelationship("shadow_industry", RepLevel.SUSPICIOUS);
    mayorate.setRelationship("pirate_anar", RepLevel.FAVORABLE);
    mayorate.setRelationship("citadeldefenders", RepLevel.HOSTILE);
    mayorate.setRelationship("crystanite", RepLevel.HOSTILE);
    mayorate.setRelationship("interstellarimperium", RepLevel.HOSTILE);
    mayorate.setRelationship("neutrinocorp", RepLevel.SUSPICIOUS);
    mayorate.setRelationship("diableavionics", RepLevel.SUSPICIOUS);
    mayorate.setRelationship("blackrock_driveyards", RepLevel.HOSTILE);
    mayorate.setRelationship("syndicate_asp", RepLevel.SUSPICIOUS);
    mayorate.setRelationship("junk_pirates", RepLevel.NEUTRAL);
    mayorate.setRelationship("pack", RepLevel.HOSTILE);
    mayorate.setRelationship("exigency", RepLevel.HOSTILE);
    mayorate.setRelationship("templars", RepLevel.VENGEFUL);
  }
}
