package moe.plushie.armourers_workshop.common.config;

import java.io.File;
import java.util.ArrayList;

import moe.plushie.armourers_workshop.common.addons.ModAddon;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigHandlerOverrides {

    public static String CATEGORY_COMPATIBILITY = "compatibility";
    public static String CATEGORY_OVERRIDES = "overrides";

    public static Configuration config;
    
    private static Property propOverrides;
    private static ArrayList<String> overrides = new ArrayList<String>();

    public static void init(File file) {
        if (config == null) {
            config = new Configuration(file, "1");
            loadConfigFile();
        }
    }

    public static void loadConfigFile() {
        loadCategoryCompatibility();
        loadCategoryOverrides();
        
        if (config.hasChanged()) {
            config.save();
        }
    }

    private static void loadCategoryCompatibility() {
        config.setCategoryComment(CATEGORY_COMPATIBILITY, "Allows auto item skinning for supported mod to be enable/disable.");
        for (ModAddon modAddon : ModAddonManager.getLoadedAddons()) {
            if (modAddon.hasItemOverrides()) {
                boolean itemSkinningSupport = config.getBoolean(
                        String.format("enable-%s-compat", modAddon.getModId()),
                        CATEGORY_COMPATIBILITY, true,
                        String.format("Enable auto item support for %s.", modAddon.getModName()));
                modAddon.setItemSkinningSupport(itemSkinningSupport);
            }
        }
    }

    private static void loadCategoryOverrides() {
        config.setCategoryComment(CATEGORY_OVERRIDES,
                "Custom list of items that can be skinned.\n"
                + "Format [override type:mod id:item name]\n"
                + "Valid override types are: sword, shield, bow, pickaxe, axe, shovel, hoe and item\n"
                + "example sword:minecraft:iron_sword");
        if (propOverrides == null) {
            propOverrides = config.get(CATEGORY_OVERRIDES, "itemOverrides", new String[] {});
            propOverrides.setLanguageKey("itemOverrides");
            overrides.clear();
            for (String override : propOverrides.getStringList()) {
                overrides.add(override);
            }
        }
    }
    
    private static void setOverrides() {
        propOverrides.set(overrides.toArray(new String[overrides.size()]));
        if (config.hasChanged()) {
            config.save();
        }
    }
    
    public static void addOverride(ItemOverrideType type, Item item) {
        String key = type.toString().toLowerCase() + ":" + item.getRegistryName();
        if (!overrides.contains(key)) {
            overrides.add(key);
            setOverrides();
            ModAddonManager.buildOverridesList();
        }
        ConfigSynchronizeHandler.resyncConfigs();
    }
    
    public static void removeOverride(ItemOverrideType type, Item item) {
        String key = type.toString().toLowerCase() + ":" + item.getRegistryName();
        if (overrides.contains(key)) {
            overrides.remove(key);
            setOverrides();
            ModAddonManager.buildOverridesList();
        }
        ConfigSynchronizeHandler.resyncConfigs();
    }
    
    public static ArrayList<String> getOverrides() {
        return overrides;
    }
}
