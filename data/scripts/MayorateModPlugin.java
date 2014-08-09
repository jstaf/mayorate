package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import data.scripts.world.rasht;
import org.dark.shaders.util.ShaderLib;
import org.dark.shaders.light.LightData;
import org.dark.shaders.util.TextureData;


public class MayorateModPlugin extends BaseModPlugin
{
    private static void initMayorate()
    {
        try {
            Global.getSettings().getScriptClassLoader().loadClass("data.scripts.world.ExerelinGen");
            //Got Exerelin, so just Exerelin
        }
        catch (ClassNotFoundException ex) {
        	// Exerelin not found so continue and run normal generation code
            new rasht().generate(Global.getSector());
        }
    }
    @Override  
    public void onApplicationLoad() {  
        ShaderLib.init();  
        LightData.readLightDataCSV("data/lights/ilk_light_data.csv");
        TextureData.readTextureDataCSVNoOverwrite("data/lights/ilk_texture_data.csv");
    }  
	
    @Override
    public void onNewGame()
    {
        initMayorate();
    }
}

