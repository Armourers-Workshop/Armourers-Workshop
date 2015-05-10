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
    public static String CATEGORY_SERVER = "server";
    
    public static Configuration config;

    public static boolean disableRecipes;
    public static int clientModelCacheTime = 12000;
    public static int serverModelCacheTime = 12000;
    public static int maxRenderDistance = 40;
    public static boolean downloadSkins;
    public static boolean compatibilityRender = false;
    public static boolean allowEquipmentWardrobe = true;
    public static String[] disabledSkins = {};
    public static boolean allowClientsToSaveSkins = false;
    public static boolean allowModsToRegisterWithAPI = true;
    public static int maxModelBakingThreads = 1;
    
    //Register
    /** Should skins be dropped on player death.<br/>
     * <br/>
     * 0 = use keep inventory rule<br/>
     * 1 = never drop<br/>
     * 2 = always drop
     */
    public static int dropSkinsOnDeath = 0;

    public static void init(File file) {
        if (config == null) {
            config = new Configuration(file);
            loadConfigFile();
        }
    }

    public static void loadConfigFile() {
        // recipe
        allowClientsToSaveSkins = config
                .get(CATEGORY_GENERAL, "Allow Clients To Save Skins", false,
                "Allows clients to save skins from a server to their local computer using the library.")
                .getBoolean(false);
        
        disableRecipes = config
                .get(CATEGORY_GENERAL, "Disable Recipes", false,
                "Disable all mod recipes. Use if you want to manually add recipes for a mod pack.")
                .getBoolean(false);
        
        disabledSkins = config
                .getStringList("Disabled Skins", CATEGORY_GENERAL, new String[] {},
                "List of skins that will be disabled.\n"
                + "\n"
                + "Here is a list of all the skins that come with the mod.\n" 
                + "armourers:head\n"
                + "armourers:chest\n"
                + "armourers:legs\n"
                + "armourers:skirt\n"
                + "armourers:feet\n"
                + "armourers:sword\n"
                + "armourers:bow\n"
                + "\n");
        
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
        
        dropSkinsOnDeath = config.get(CATEGORY_GENERAL, "Drop Skins On Death", 0,
                "Should skins be dropped on player death.\n"
                + "0 = use keep inventory rule\n"
                + "1 = never drop\n"
                + "2 = always drop").getInt(0);
        
        allowEquipmentWardrobe = config
                .get(CATEGORY_GENERAL, "Allow equipment wardrobe", true,
                "Allow the player to open the equipment wardrobe GUI.")
                .getBoolean(true);
        
        allowModsToRegisterWithAPI = config
                .get(CATEGORY_COMPATIBILITY, "Allow mods to register with API", true,
                "Allow other mods to register with the Armourer's Workshop API.")
                .getBoolean(true);
        
        //Addons
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
        
        Addons.moreSwordsModCompatibility = config
                .get(CATEGORY_COMPATIBILITY, "More Swords Mod Compatibility", true,
                "Allow weapon render override on More Swords Mod items.")
                .getBoolean(true);
        
        Addons.mekanismToolsCompatibility = config
                .get(CATEGORY_COMPATIBILITY, "Mekanism Tools Compatibility", true,
                "Allow weapon render override on Mekanism Tools Mod items.")
                .getBoolean(true);
        
        //Client
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            maxRenderDistance = config
                    .get(CATEGORY_CLIENT, "Skin Render Distance", 40,
                    "The max distance away that skins will render.")
                    .getInt(40);
            
            maxModelBakingThreads = config
                    .get(CATEGORY_CLIENT, "Max Model Baking Threads", 1,
                    "The maximum number of threads that will be used to bake models. Less that 1 equals unlimited.")
                    .getInt(1);
            
            serverModelCacheTime = config
                    .get(CATEGORY_CLIENT, "Cliet Model Cache Time", 12000,
                    "How long in ticks the client will keep skins in it's cache.\n" + 
                    "Default 12000 ticks is 10 minutes.")
                    .getInt(12000);
        }
        
        //Server
        serverModelCacheTime = config
                .get(CATEGORY_SERVER, "Server Model Cache Time", 12000,
                "How long in ticks the server will keep skins in it's cache.\n" + 
                "Default 12000 ticks is 10 minutes.")
                .getInt(12000);
        
        
        if (config.hasChanged()) {
            config.save();
        }
    }
}
