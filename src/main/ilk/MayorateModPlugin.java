package ilk;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.CampaignPlugin.PickPriority;
import com.fs.starfarer.api.combat.AutofireAIPlugin;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;

import org.apache.log4j.Level;
import org.dark.shaders.light.LightData;
import org.dark.shaders.util.ShaderLib;
import org.dark.shaders.util.TextureData;
import org.json.JSONObject;

import exerelin.campaign.SectorManager;
import ilk.shipsystems.BubbleShieldStats;
import ilk.shipsystems.FighterCoprocessingStats;
import ilk.shipsystems.ai.PhaseLeapAI;
import ilk.util.DamageUtils;
import ilk.weapons.ai.NukeAI;
import ilk.weapons.ai.ThermalLanceAI;
import ilk.weapons.ai.DisruptorAI;
import ilk.world.MayorateGenerator;
import ilk.world.utils.CommissionEffects;
import ilk.world.utils.PathSpawnPoint;

public class MayorateModPlugin extends BaseModPlugin {

  public static final String ID = "mayorate";
  private static final String SETTINGS_FILE = "mayorate_settings.json";
  private static boolean isExerelin;

  public static boolean getIsExerelin() {
    return isExerelin;
  }

  private static void initMayorate() {
    isExerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
    if (!isExerelin || SectorManager.getCorvusMode()) {
      new MayorateGenerator().generate(Global.getSector());
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
    case "ilk_aoe_mirv":
      return new PluginPick<MissileAIPlugin>(new NukeAI(missile, launchingShip), PickPriority.HIGHEST);
    default:
      return null;
    }
  }

  @Override
  public PluginPick<AutofireAIPlugin> pickWeaponAutofireAI(WeaponAPI weapon) {
    switch (weapon.getId()) {
    case "ilk_thermal_lance":
      return new PluginPick<AutofireAIPlugin>(new ThermalLanceAI(weapon), PickPriority.MOD_SET);
    case "ilk_disruptor":
      return new PluginPick<AutofireAIPlugin>(new DisruptorAI(weapon), PickPriority.MOD_SET);
    case "ilk_disruptor_heavy":
      return new PluginPick<AutofireAIPlugin>(new DisruptorAI(weapon), PickPriority.MOD_SET);
    default:
      return null;
    }
  }

  private static void setLogLevel(Level level) {
    Global.getLogger(BubbleShieldStats.class).setLevel(level);
    Global.getLogger(CommissionEffects.class).setLevel(level);
    Global.getLogger(DamageUtils.class).setLevel(level);
    Global.getLogger(FighterCoprocessingStats.class).setLevel(level);
    Global.getLogger(PhaseLeapAI.class).setLevel(level);
    Global.getLogger(PathSpawnPoint.class).setLevel(level);
  }
}
