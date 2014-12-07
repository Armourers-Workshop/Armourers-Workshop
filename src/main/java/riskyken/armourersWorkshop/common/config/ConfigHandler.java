package riskyken.armourersWorkshop.common.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import riskyken.armourersWorkshop.common.UpdateCheck;

public class ConfigHandler {

    public static String CATEGORY_GENERAL = "general";
    public static String CATEGORY_MISC = "misc";

    public static Configuration config;

    public static boolean disableRecipes;
    public static int modelCacheTime = 12000;
    public static int maxRenderDistance = 40;

    public static void init(File file) {
        if (config == null) {
            config = new Configuration(file);
            loadConfigFile();
        }
    }

    public static void loadConfigFile() {
        // recipe
        disableRecipes = config
                .get(CATEGORY_GENERAL, "Disable Recipes", false,
                "Disable all mod recipes. Use if you want to manually add recipes for a mod pack.")
                .getBoolean(false);

        UpdateCheck.checkForUpdates = config.get(CATEGORY_GENERAL, "Check for updates", true,
                "Should the mod check for new versions?").getBoolean(true);
        
        if (config.hasChanged()) {
            config.save();
        }
    }
}
