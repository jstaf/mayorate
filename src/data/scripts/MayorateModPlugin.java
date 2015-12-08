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
import org.lazywizard.lazylib.LazyLib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MayorateModPlugin extends BaseModPlugin {

    private static void initMayorate() {
        try {
            // EXERELIN on
            Class<?> def = Global.getSettings().getScriptClassLoader().loadClass("exerelin.campaign.SectorManager");
            Method method;
            try {
                method = def.getMethod("getCorvusMode");
                Object result = method.invoke(def);
                if ((boolean) result) {
                    // Exerelin running in Corvus mode, go ahead and generate our sector
                    new mayorateGen().generate(Global.getSector());
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                    InvocationTargetException ex) {
                // check failed, do nothing
            }
            Global.getSector().getMemory().set("$IS_EXERELIN", true);
        } catch (ClassNotFoundException ex) {
            // Exerelin not found so continue and run normal generation code
            new mayorateGen().generate(Global.getSector());
            Global.getSector().getMemory().set("$IS_EXERELIN", false);
        }
    }

    /**
     * Initialize ShaderLib, crash game if player is missing dependencies.
     */
    @Override
    public void onApplicationLoad() {
        Global.getLogger(MayorateModPlugin.class).setLevel(Level.ERROR);

        try {
            LazyLib.getVersion();
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
            Global.getLogger(MayorateModPlugin.class).log(Level.ERROR, "LazyLib not found.");
            throw e;
        }

        try {
            ShaderLib.init();
            LightData.readLightDataCSV("data/lights/ilk_light_data.csv");
            TextureData.readTextureDataCSVNoOverwrite("data/lights/ilk_texture_data.csv");
        } catch (NoClassDefFoundError e) {
            //shaderlib not installed
            e.printStackTrace();
            Global.getLogger(MayorateModPlugin.class).log(Level.ERROR, "ShaderLib not found.");
            throw e;
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
