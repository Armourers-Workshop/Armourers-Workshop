package moe.plushie.armourers_workshop.common.config;

import java.io.File;
import java.util.Arrays;

import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigHandlerOverrides {

    public static String CATEGORY_OVERRIDES = "overrides";
    
    public static Configuration config;

    public static void init(File file) {
        if (config == null) {
            config = new Configuration(file, "1");
            loadConfigFile();
        }
    }

    public static void loadConfigFile() {
        loadCategoryCompatibility();
        if (config.hasChanged()) {
            config.save();
        }
    }
    
    private static void loadCategoryCompatibility() {
        Property prop = config.get(CATEGORY_OVERRIDES, "itemOverrides", ModAddonManager.getDefaultOverrides());
        prop.setLanguageKey("itemOverrides");
        prop.setComment("List of items that can have skins applied.\n"
                + "Format [override type:mod id:item name]\n"
                + "Valid override types are:\n"
                + "sword\n"
                + "item\n"
                + "pickaxe\n"
                + "axe\n"
                + "shovel\n"
                + "hoe\n"
                + "bow");
        ModAddonManager.itemOverrides.clear();
        ModAddonManager.itemOverrides.addAll(Arrays.asList(prop.getStringList()));
    }
}
