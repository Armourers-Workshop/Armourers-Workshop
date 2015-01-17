package riskyken.armourersWorkshop.common.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import riskyken.armourersWorkshop.common.addons.Addons;
import riskyken.armourersWorkshop.common.undo.UndoManager;
import riskyken.armourersWorkshop.common.update.UpdateCheck;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class ConfigHandler {

    public static String CATEGORY_GENERAL = "general";
    public static String CATEGORY_COMPATIBILITY = "compatibility";
    public static String CATEGORY_CLIENT = "client";
    
    public static Configuration config;

    public static boolean disableRecipes;
    public static int modelCacheTime = 12000;
    public static int maxRenderDistance = 40;
    public static boolean downloadSkins;
    public static boolean compatibilityRender = false;


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
        
        downloadSkins = config
                .get(CATEGORY_GENERAL, "Allow Auto Skin Downloads", true,
                "Allow the mod to auto download new skins.")
                .getBoolean(true);
        
        UndoManager.maxUndos = config
                .get(CATEGORY_GENERAL, "Max Undos", 100,
                "Max number of undos a player has for block painting.")
                .getInt(100);
        
        UpdateCheck.checkForUpdates = config.get(CATEGORY_GENERAL, "Check for updates", true,
                "Should the mod check for new versions?").getBoolean(true);
        
        Addons.weaponmodCompatibility = config
                .get(CATEGORY_COMPATIBILITY, "Balkon's Weapon Mod Compatibility", true,
                "Allow weapon render override on Balkon's Weapon Mod items.")
                .getBoolean(true);
        
        Addons.betterStorageCompatibility = config
                .get(CATEGORY_COMPATIBILITY, "Better Storage Compatibility", true,
                "Allow weapon render override on Better Storage items.")
                .getBoolean(true);
        
        Addons.botaniaCompatibility = config
                .get(CATEGORY_COMPATIBILITY, "Botania Compatibility", true,
                "Allow weapon render override on Botania items.")
                .getBoolean(true);
        
        Addons.minecraftCompatibility = config
                .get(CATEGORY_COMPATIBILITY, "Minecraft Compatibility", true,
                "Allow weapon render override on Minecraft items.")
                .getBoolean(true);
        
        Addons.tConstructCompatibility = config
                .get(CATEGORY_COMPATIBILITY, "Tinkers' Construct Compatibility", true,
                "Allow weapon render override on Tinkers' Construct items.")
                .getBoolean(true);
        
        Addons.thaumcraftCompatibility = config
                .get(CATEGORY_COMPATIBILITY, "Thaumcraft Compatibility", true,
                "Allow weapon render override on Thaumcraft items.")
                .getBoolean(true);
        
        Addons.zeldaswordskillsCompatibility = config
                .get(CATEGORY_COMPATIBILITY, "Zelda Sword Skills Compatibility", true,
                "Allow weapon render override on Zelda Sword Skills items.")
                .getBoolean(true);
        
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            maxRenderDistance = config
                    .get(CATEGORY_CLIENT, "Skin Render Distance", 40,
                    "The max distance away that skins will render.")
                    .getInt(40);
        }
        
        if (config.hasChanged()) {
            config.save();
        }
    }
}
