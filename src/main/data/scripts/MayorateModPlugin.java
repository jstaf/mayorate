package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.CampaignPlugin.PickPriority;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.AutofireAIPlugin;
import data.scripts.util.ilk_DamageUtils;
import data.scripts.weapons.ai.ilk_NukeAI;
import data.scripts.weapons.ai.ilk_ThermalLanceAutofirePlugin;
import data.scripts.weapons.ilk_RamdriveEveryFrameEffect;
import data.scripts.world.mayorateGen;
import data.scripts.world.utils.ilk_CommissionEffects;
import data.scripts.world.utils.ilk_PathSpawnPoint;
import data.shipsystems.scripts.ilk_BubbleShieldStats;
import exerelin.campaign.SectorManager;
import org.apache.log4j.Level;
import org.dark.shaders.light.LightData;
import org.dark.shaders.util.ShaderLib;
import org.dark.shaders.util.TextureData;
import org.json.JSONObject;

public class MayorateModPlugin extends BaseModPlugin {

  private static final String SETTINGS_FILE = "mayorate_settings.json";

  private static final String NUKE_ID = "ilk_aoe_mirv";
  private static final String THERMAL_LANCE_ID = "ilk_thermal_lance";

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
  public void onApplicationLoad() throws Exception {
    if (!Global.getSettings().getModManager().isModEnabled("lw_lazylib")) {
      throw new RuntimeException("The Mayorate requires LazyLib.");
    }
    if (!Global.getSettings().getModManager().isModEnabled("shaderLib")) {
      throw new RuntimeException("The Mayorate requires GraphicsLib.");
    }

    ShaderLib.init();
    LightData.readLightDataCSV("data/lights/ilk_light_data.csv");
    TextureData.readTextureDataCSVNoOverwrite("data/lights/ilk_texture_data.csv");

    JSONObject settings = Global.getSettings().loadJSON(SETTINGS_FILE);
    setLogLevel(Level.toLevel(settings.optString("logLevel", "ERROR"), Level.ERROR));
  }

  @Override
  public void onNewGame() {
    initMayorate();
  }

  @Override
  public PluginPick<MissileAIPlugin> pickMissileAI(MissileAPI missile, ShipAPI launchingShip) {
    switch (missile.getProjectileSpecId()) {
      case NUKE_ID:
        return new PluginPick<MissileAIPlugin>(
            new ilk_NukeAI(missile, launchingShip), PickPriority.HIGHEST);
      default:
        return null;
    }
  }

  @Override
  public PluginPick<AutofireAIPlugin> pickWeaponAutofireAI(WeaponAPI weapon) {
    switch (weapon.getId()) {
      case THERMAL_LANCE_ID:
        return new PluginPick<AutofireAIPlugin>(
            new ilk_ThermalLanceAutofirePlugin(weapon), PickPriority.MOD_SET);
      default:
        return null;
    }
  }

  private static void setLogLevel(Level level) {
    Global.getLogger(ilk_BubbleShieldStats.class).setLevel(level);
    Global.getLogger(ilk_CommissionEffects.class).setLevel(level);
    Global.getLogger(ilk_DamageUtils.class).setLevel(level);
    Global.getLogger(ilk_PathSpawnPoint.class).setLevel(level);
    Global.getLogger(ilk_RamdriveEveryFrameEffect.class).setLevel(level);
  }
}
