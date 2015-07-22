package riskyken.armourersWorkshop.common.config;

import java.io.File;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.config.Configuration;
import riskyken.armourersWorkshop.common.addons.Addons;
import riskyken.armourersWorkshop.common.undo.UndoManager;
import riskyken.armourersWorkshop.common.update.UpdateCheck;

public class ConfigHandler {

    public static String CATEGORY_RECIPE = "recipe";
    public static String CATEGORY_GENERAL = "general";
    public static String CATEGORY_COMPATIBILITY = "compatibility";
    public static String CATEGORY_CLIENT = "client";
    public static String CATEGORY_SERVER = "server";
    public static String CATEGORY_DEBUG = "debug";
    
    public static Configuration config;

    //recipes
    public static boolean disableRecipes;
    public static boolean disableDollRecipe;
    public static boolean disableSkinningRecipes;
    public static boolean hideDollFromCreativeTabs;
    
    //client
    public static int clientModelCacheTime = 12000;
    public static int maxRenderDistance = 40;
    public static int maxModelBakingThreads = 1;
    
    //server
    public static int serverModelCacheTime = 12000;
    
    //general
    public static boolean downloadSkins;
    public static boolean allowEquipmentWardrobe = true;
    public static String[] disabledSkins = {};
    public static boolean allowClientsToSaveSkins = false;
    
    //compatibility
    public static boolean allowModsToRegisterWithAPI = true;
    
    //debug
    public static boolean skinTextureRenderOverride;
    public static boolean skinSafeModelRenderOverride;
    public static boolean showF3DebugInfo;
    public static boolean showSkinTooltipDebugInfo;
    
    
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
        
        allowClientsToSaveSkins = config
                .get(CATEGORY_GENERAL, "Allow Clients To Save Skins", false,
                "Allows clients to save skins from a server to their local computer using the library.")
                .getBoolean(false);
        
        //recipe
        disableRecipes = config
                .get(CATEGORY_RECIPE, "Disable Recipes", false,
                "Disable vanilla recipes. Use if you want to manually add recipes for a mod pack.")
                .getBoolean(false);
        
        disableDollRecipe = config
                .get(CATEGORY_RECIPE, "Disable Doll Recipe", false,
                "Disable hidden in world doll recipe.")
                .getBoolean(false);
        
        disableSkinningRecipes = config
                .get(CATEGORY_RECIPE, "Disable Skinning Recipes", false,
                "Disable skinning table recipes.")
                .getBoolean(false);
        
        hideDollFromCreativeTabs = config
                .get(CATEGORY_RECIPE, "Hide Doll Block", true,
                "Hides the doll block from the creative tab and NEI..")
                .getBoolean(true);
        
        //debug
        skinSafeModelRenderOverride = config
                .get(CATEGORY_DEBUG, "Safe Model Render Override", false,
                "Only enable this is you are having rendering issues with skins on players.\n"
                + "Enable this option will break Smart Moving compatibility")
                .getBoolean(false);
        
        skinTextureRenderOverride = config
                .get(CATEGORY_DEBUG, "Safe Textue Render Override", false,
                "Only enable this is you are having rendering issues with skins.\n"
                + "This option is force on is shaders mod is installed.")
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
        
        Addons.overrideSwordsActive = config
                .getStringList("Sword Overrides", CATEGORY_COMPATIBILITY, Addons.overrideSwordsDefault,
                "List of swords that can have skins applied.\n"
                + "Format [mod id:item name]"
                + "\n"
                + "\n");
        
        Addons.overrideBowsActive = config
                .getStringList("Bow Overrides", CATEGORY_COMPATIBILITY, Addons.overrideBowsDefault,
                "List of bows that can have skins applied.\n"
                + "Format [mod id:item name]"
                + "\n"
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
                    .get(CATEGORY_CLIENT, "Client Model Cache Time", 12000,
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
