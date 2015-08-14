package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.CampaignPlugin;
import com.fs.starfarer.api.combat.*;
import data.scripts.weapons.ai.DisruptorAI;
import data.scripts.weapons.ai.ilk_NukeAI;
import data.scripts.world.mayorateGen;
import org.apache.log4j.Level;
import org.dark.shaders.light.LightData;
import org.dark.shaders.util.ShaderLib;
import org.dark.shaders.util.TextureData;

public class MayorateModPlugin extends BaseModPlugin {

    private static void initMayorate() {
        try {
            Global.getSettings().getScriptClassLoader().loadClass("data.scripts.world.ExerelinGen");
            //Got Exerelin, so just Exerelin
        } catch (ClassNotFoundException ex) {
            // Exerelin not found so continue and run normal generation code
            new mayorateGen().generate(Global.getSector());
        }
    }

    @Override
    public void onApplicationLoad() {
        Global.getLogger(MayorateModPlugin.class).setLevel(Level.ERROR);

        try {
            ShaderLib.init();
            LightData.readLightDataCSV("data/lights/ilk_light_data.csv");
            TextureData.readTextureDataCSVNoOverwrite("data/lights/ilk_texture_data.csv");
        } catch (Exception e) {
            //shaderlib not installed
        }
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
                return new PluginPick<MissileAIPlugin>(new ilk_NukeAI(missile, launchingShip), CampaignPlugin.PickPriority.MOD_SET);
            default:
                return null;
        }
    }

    @Override
    public PluginPick<AutofireAIPlugin> pickWeaponAutofireAI(WeaponAPI weapon)
    {
        switch (weapon.getId())
        {
            case "ilk_disruptor":
                return new PluginPick<AutofireAIPlugin>(new DisruptorAI(weapon), CampaignPlugin.PickPriority.MOD_SET);
            case "ilk_disruptor_heavy":
                return new PluginPick<AutofireAIPlugin>(new DisruptorAI(weapon), CampaignPlugin.PickPriority.MOD_SET);
            default:
                return null;
        }
    }
}
