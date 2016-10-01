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
    public static int clientModelCacheTime = 600000;
    public static int clientTextureCacheTime = 600000;
    public static int maxSkinRenderDistance = 40;
    public static int maxModelBakingThreads = 1;
    public static boolean multipassSkinRendering = true;
    public static int mannequinMaxEquipmentRenderDistance = 1024;
    public static int blockSkinMaxRenderDistance = 2500;
    public static double lodDistance = 32F;
    public static int skinLoadAnimationTime = 500;
    public static int maxLodLevels = 4;
    
    //server
    public static int serverModelCacheTime = 600000;
    public static int serverSkinSendRate = 4000;
    
    //general
    public static boolean extractOfficialSkins;
    public static boolean allowEquipmentWardrobe = true;
    public static String[] disabledSkins = {};
    public static boolean allowClientsToDownloadSkins = false;
    public static boolean allowClientsToUploadSkins = true;
    public static boolean enableHolidayEvents = true;
    public static int startingWardrobeSlots = 3;
    public static boolean libraryShowsModelPreviews = true;
    
    //compatibility
    public static boolean allowModsToRegisterWithAPI = true;
    
    //debug
    public static boolean skinTextureRenderOverride;
    public static int skinRenderType;
    public static boolean showF3DebugInfo;
    public static boolean showSkinTooltipDebugInfo;
    public static boolean showArmourerDebugRender;
    public static boolean wireframeRender;
    public static boolean disableTexturePainting;
    
    
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
        loadCategoryGeneral();
        loadCategoryRecipe();
        loadCategoryDebug();
        loadCategoryCompatibility();
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            loadCategoryClient();
        }
        loadCategoryServer();
        
        if (config.hasChanged()) {
            config.save();
        }
    }
    
    private static void loadCategoryGeneral() {
        allowClientsToDownloadSkins = config
                .get(CATEGORY_GENERAL, "allowClientsToDownloadSkins", false,
                "Allows clients to save skins from a server to their local computer using the library.")
                .getBoolean(false);
        
        allowClientsToUploadSkins = config
                .get(CATEGORY_GENERAL, "allowClientsToUploadSkins", true,
                "Allows clients to load skins from their local computer onto the server using the library.")
                .getBoolean(true);
        
        disabledSkins = config
                .getStringList("disabledSkins", CATEGORY_GENERAL, new String[] {},
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
                + "armourers:arrow\n"
                + "\n");
        
        extractOfficialSkins = config
                .get(CATEGORY_GENERAL, "extractOfficialSkins", true,
                "Allow the mod to extract the official skins that come with the mod into the library folder.")
                .getBoolean(true);
        
        UndoManager.maxUndos = config
                .get(CATEGORY_GENERAL, "maxUndos", 100,
                "Max number of undos a player has for block painting.")
                .getInt(100);
        
        UpdateCheck.checkForUpdates = config.get(CATEGORY_GENERAL, "checkForUpdates", true,
                "Should the mod check for new versions?").getBoolean(true);
        
        dropSkinsOnDeath = config.get(CATEGORY_GENERAL, "dropSkinsOnDeath", 0,
                "Should skins be dropped on player death.\n"
                + "0 = use keep inventory rule\n"
                + "1 = never drop\n"
                + "2 = always drop").getInt(0);
        
        allowEquipmentWardrobe = config
                .get(CATEGORY_GENERAL, "allowEquipmentWardrobe", true,
                "Allow the player to open the equipment wardrobe GUI.")
                .getBoolean(true);
        
        enableHolidayEvents = config
                .get(CATEGORY_GENERAL, "enableHolidayEvents", true,
                "Setting to false will disable holiday events. What's wrong with you!")
                .getBoolean(true);
        
        startingWardrobeSlots = config
                .getInt("startingWardrobeSlots", CATEGORY_GENERAL, 3, 1, 5,
                "Number of slot columns the player starts with for skins.");
        
        libraryShowsModelPreviews = config
                .getBoolean("libraryShowsModelPreviews", CATEGORY_GENERAL, true,
                        "Shows model previews in the library.\n"
                        + "Causes a lot of extra load on servers.\n"
                        + "Best to turn off on high population servers");
    }
    
    private static void loadCategoryRecipe() {
        disableRecipes = config
                .get(CATEGORY_RECIPE, "disableRecipes", false,
                "Disable vanilla recipes. Use if you want to manually add recipes for a mod pack.")
                .getBoolean(false);
        
        disableDollRecipe = config
                .get(CATEGORY_RECIPE, "disableDollRecipe", false,
                "Disable hidden in world doll recipe.")
                .getBoolean(false);
        
        disableSkinningRecipes = config
                .get(CATEGORY_RECIPE, "disableSkinningRecipes", false,
                "Disable skinning table recipes.")
                .getBoolean(false);
        
        hideDollFromCreativeTabs = config
                .get(CATEGORY_RECIPE, "hideDollFromCreativeTabs", true,
                "Hides the doll block from the creative tab and NEI.")
                .getBoolean(true);
    }
    
    private static void loadCategoryDebug() {
        skinRenderType = config
                .getInt("skinRenderType", CATEGORY_DEBUG, 0, 0, 2,
                "Only change this if you are having rendering issues with skins on players." +
                "(normally fixes skins not rotating on players)\n" +
                "This option is force on if Smart Moving is installed.\n" +
                "\n" +
                "0 = auto\n" +
                "1 = render event\n" +
                "2 = model attachment\n");
        
        skinTextureRenderOverride = config
                .get(CATEGORY_DEBUG, "skinTextureRenderOverride", false,
                "Only enable this if you are having rendering issues with skins. (normally fixes lighting issues)\n"
                + "This option is force on if Shaders Mod or Colored Lights mod is installed.")
                .getBoolean(false);
        
        showF3DebugInfo = config
                .get(CATEGORY_DEBUG, "showF3DebugInfo", true,
                "Shows extra info on the F3 debug screen.")
                .getBoolean(true);
        
        showSkinTooltipDebugInfo = config
                .get(CATEGORY_DEBUG, "showSkinTooltipDebugInfo", true,
                "Shows extra debug info on skin tooltips.")
                .getBoolean(true);
        
        showArmourerDebugRender = config
                .get(CATEGORY_DEBUG, "showArmourerDebugRender", false,
                "Shows extra debug renders on the armourer.")
                .getBoolean(false);
        
        wireframeRender = config
                .get(CATEGORY_DEBUG, "wireframeRender", false,
                "Render models in a wireframe.")
                .getBoolean(false);
        
        disableTexturePainting = config.getBoolean("disableTexturePainting", CATEGORY_DEBUG, false,
                "Disables replacing the players texture with a painted version.\n"
                + "Disabling this may fix issues with the players texture rendering\n"
                + "incorrectly or showing the steve skin.");
    }
    
    private static void loadCategoryCompatibility() {
        allowModsToRegisterWithAPI = config
                .get(CATEGORY_COMPATIBILITY, "allowModsToRegisterWithAPI", true,
                "Allow other mods to register with the Armourer's Workshop API.")
                .getBoolean(true);
        
        Addons.overrideSwordsActive = config
                .getStringList("swordOverrides", CATEGORY_COMPATIBILITY, Addons.overrideSwordsDefault,
                "List of swords that can have skins applied.\n"
                + "Format [mod id:item name]"
                + "\n"
                + "\n");
        
        Addons.overrideBowsActive = config
                .getStringList("bowOverrides", CATEGORY_COMPATIBILITY, Addons.overrideBowsDefault,
                "List of bows that can have skins applied.\n"
                + "Format [mod id:item name]"
                + "\n"
                + "\n");
    }
    
    private static void loadCategoryClient() {
        maxSkinRenderDistance = config
                .get(CATEGORY_CLIENT, "maxSkinRenderDistance", 40,
                "The max distance away that skins will render.")
                .getInt(40);
        
        maxModelBakingThreads = config
                .get(CATEGORY_CLIENT, "maxModelBakingThreads", 1,
                "The maximum number of threads that will be used to bake models. Less that 1 equals unlimited.")
                .getInt(1);
        
        clientModelCacheTime = config
                .get(CATEGORY_CLIENT, "clientModelCacheTime", 600000,
                "How long in ms the client will keep skins in it's cache.\n" + 
                "Default 600000 ms is 10 minutes.")
                .getInt(600000);
        
        clientTextureCacheTime = config
                .getInt("clientTextureCacheTime", CATEGORY_CLIENT, 600, 1, 3600,
                "How long in seconds the client will keep textures in it's cache.\n" + 
                "Default 600 seconds is 10 minutes.");
        
        multipassSkinRendering = config.getBoolean("multipassSkinRendering", CATEGORY_CLIENT, true,
                "When enabled skin will render in multiple passes to reduce visual artifacts.\n"
                + "Disabling this will improve skin rendering performance at the cost of visual quality.");
        
        mannequinMaxEquipmentRenderDistance = config.getInt("mannequinMaxEquipmentRenderDistance", CATEGORY_CLIENT, 1024, 1, 4096,
                "The max distance squared that equipment will be rendered on mannequins.");
        
        blockSkinMaxRenderDistance = config.getInt("blockSkinMaxRenderDistance", CATEGORY_CLIENT, 2500, 1, 65536,
                "The max distance squared that block skins will be rendered.");
        
        lodDistance = config.getFloat("lodDistance", CATEGORY_CLIENT, 32F, 8, 128,
                "Distance away that skins will have lod applied to them.");
        
        skinLoadAnimationTime = config.getInt("skinLoadAnimationTime", CATEGORY_CLIENT, 500, 0, 10000,
                "How long skins will display their loading animation for in milliseconds\n"
                + "Settings this to 0 will disable loading animations.");
        
        maxLodLevels = config.getInt("maxLodLevels", CATEGORY_CLIENT, 4, 0, 4,
                "Number of LOD models to create. Higher number should give a boost to framerate at a small cost to VRAM.");
    }
    
    private static void loadCategoryServer() {
        serverModelCacheTime = config
                .get(CATEGORY_SERVER, "serverModelCacheTime", 600000,
                "How long in ms the server will keep skins in it's cache.\n" + 
                "Default 600000 ms is 10 minutes.")
                .getInt(600000);
        
        serverSkinSendRate = config.getInt("serverModelSendRate", CATEGORY_SERVER, 4000, 0, 8000,
                "The maximum number of skins the server is allow to send every minute.\n"
                + "Less that 1 equals unlimited. (not recommended may cause bandwidth and cpu spikes on the server)");
    }
}
