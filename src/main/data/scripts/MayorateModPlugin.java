package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.CampaignPlugin;
import com.fs.starfarer.api.combat.*;
import data.scripts.weapons.ai.ilk_NukeAI;
import data.scripts.world.mayorateGen;
import exerelin.campaign.SectorManager;
import org.dark.shaders.light.LightData;
import org.dark.shaders.util.ShaderLib;
import org.dark.shaders.util.TextureData;

public class MayorateModPlugin extends BaseModPlugin {

  private static boolean isExerelin;

  public static boolean getIsExerelin() {
    return isExerelin;
  }

  private static void initMayorate() {
    isExerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
    if (!isExerelin || SectorManager.getCorvusMode()) {
      new mayorateGen().generate(Global.getSector());
    }
  }

  /** Initialize ShaderLib, crash game if player is missing dependencies. */
  @Override
  public void onApplicationLoad() {
    if (!Global.getSettings().getModManager().isModEnabled("lw_lazylib")) {
      throw new RuntimeException("The Mayorate requires LazyLib.");
    }
    if (!Global.getSettings().getModManager().isModEnabled("shaderLib")) {
      throw new RuntimeException("The Mayorate requires GraphicsLib.");
    }

    ShaderLib.init();
    LightData.readLightDataCSV("data/lights/ilk_light_data.csv");
    TextureData.readTextureDataCSVNoOverwrite("data/lights/ilk_texture_data.csv");
  }

  @Override
  public void onNewGame() {
    initMayorate();
  }

  public static final String NUKE_ID = "ilk_aoe_mirv";

  @Override
  public PluginPick<MissileAIPlugin> pickMissileAI(MissileAPI missile, ShipAPI launchingShip) {
    switch (missile.getProjectileSpecId()) {
      case NUKE_ID:
        return new PluginPick<MissileAIPlugin>(
            new ilk_NukeAI(missile, launchingShip), CampaignPlugin.PickPriority.MOD_SET);
      default:
        return null;
    }
  }

  @Override
  public PluginPick<AutofireAIPlugin> pickWeaponAutofireAI(WeaponAPI weapon) {
    switch (weapon.getId()) {
      default:
        return null;
    }
  }
}
