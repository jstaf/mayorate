package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.CampaignPlugin;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
//import data.scripts.plugins.ilk_CombatVoice;
import data.scripts.weapons.ai.ilk_NukeAI;
import data.scripts.world.rasht.ilk_RashtGen;
import java.io.IOException;
import org.apache.log4j.Level;
import org.dark.shaders.light.LightData;
import org.dark.shaders.util.ShaderLib;
import org.dark.shaders.util.TextureData;
import org.json.JSONException;

public class MayorateModPlugin extends BaseModPlugin {

    private static void initMayorate() {
        try {
            Global.getSettings().getScriptClassLoader().loadClass("data.scripts.world.ExerelinGen");
            //Got Exerelin, so just Exerelin
        } catch (ClassNotFoundException ex) {
            // Exerelin not found so continue and run normal generation code
            new ilk_RashtGen().generate(Global.getSector());
        }
    }

    @Override
    public void onApplicationLoad() {
        Global.getLogger(MayorateModPlugin.class).setLevel(Level.ERROR);

        ShaderLib.init();
        LightData.readLightDataCSV("data/lights/ilk_light_data.csv");
        TextureData.readTextureDataCSVNoOverwrite("data/lights/ilk_texture_data.csv");

        /*try {
            ilk_CombatVoice.reloadSettings();
        } catch (IOException | JSONException e) {
            Global.getLogger(MayorateModPlugin.class).log(Level.ERROR, "Combat Voice Loading Failed! " + e.getMessage());
        }*/
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
}
