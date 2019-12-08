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

    // only do the following if not in nexerelin
    if (!MayorateModPlugin.getIsExerelin()) {
      // add forgiveness script to avoid rep decay on mayorate commission from being
      // hostile with pirates
      sector.addScript(new CommissionEffects());
    }
  }

  public static void initFactionRelationships(SectorAPI sector) {
    FactionAPI mayorate = sector.getFaction("mayorate");

    sector.getFaction(Factions.PLAYER).setRelationship(mayorate.getId(), RepLevel.NEUTRAL);

    mayorate.setRelationship(sector.getFaction(Factions.LUDDIC_PATH).getId(), RepLevel.VENGEFUL);
    mayorate.setRelationship(sector.getFaction(Factions.LUDDIC_CHURCH).getId(), RepLevel.HOSTILE);
    mayorate.setRelationship(sector.getFaction(Factions.HEGEMONY).getId(), RepLevel.HOSTILE);
    mayorate.setRelationship(sector.getFaction(Factions.PIRATES).getId(), RepLevel.FAVORABLE);
    mayorate.setRelationship(sector.getFaction(Factions.DIKTAT).getId(), RepLevel.NEUTRAL);
    mayorate.setRelationship(sector.getFaction(Factions.KOL).getId(), RepLevel.SUSPICIOUS);
    mayorate.setRelationship(sector.getFaction(Factions.TRITACHYON).getId(), RepLevel.NEUTRAL);
    mayorate.setRelationship(sector.getFaction(Factions.INDEPENDENT).getId(), RepLevel.SUSPICIOUS);
    mayorate.setRelationship(sector.getFaction(Factions.PERSEAN).getId(), RepLevel.HOSTILE);

    // mod factions
    mayorate.setRelationship("scy", RepLevel.HOSTILE);
    mayorate.setRelationship("sun_ice", RepLevel.HOSTILE);
    mayorate.setRelationship("sun_ici", RepLevel.HOSTILE);
    mayorate.setRelationship("shadow_industry", RepLevel.HOSTILE);
    mayorate.setRelationship("pirate_anar", RepLevel.NEUTRAL);
    mayorate.setRelationship("citadeldefenders", RepLevel.HOSTILE);
    mayorate.setRelationship("crystanite", RepLevel.HOSTILE);
    mayorate.setRelationship("interstellarimperium", RepLevel.HOSTILE);
    mayorate.setRelationship("neutrinocorp", RepLevel.NEUTRAL);
    mayorate.setRelationship("diableavionics", RepLevel.NEUTRAL);
    mayorate.setRelationship("blackrock_driveyards", RepLevel.HOSTILE);
    mayorate.setRelationship("syndicate_asp", RepLevel.NEUTRAL);
    mayorate.setRelationship("junk_pirates", RepLevel.NEUTRAL);
    mayorate.setRelationship("pack", RepLevel.HOSTILE);
    mayorate.setRelationship("exigency", RepLevel.HOSTILE);
    mayorate.setRelationship("templars", RepLevel.VENGEFUL);
  }
}
