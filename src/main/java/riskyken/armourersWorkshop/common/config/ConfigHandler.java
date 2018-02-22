package riskyken.armourersWorkshop.common.config;

import java.io.File;
import java.util.UUID;

import net.minecraftforge.common.config.Configuration;
import riskyken.armourersWorkshop.common.undo.UndoManager;
import riskyken.armourersWorkshop.common.update.UpdateCheck;

public class ConfigHandler {

    public static String CATEGORY_RECIPE = "recipe";
    public static String CATEGORY_GENERAL = "general";
    public static String CATEGORY_COMPATIBILITY = "compatibility";
    public static String CATEGORY_SERVER = "server";
    public static String CATEGORY_ENTITY_SKINS = "entity_skins";
    
    public static Configuration config;
    
    //recipes
    public static boolean disableRecipes;
    public static boolean disableDollRecipe;
    public static boolean disableSkinningRecipes;
    public static boolean hideDollFromCreativeTabs;
    
    //server
    public static int serverModelCacheTime = 600000;
    public static int serverSkinSendRate = 4000;
    public static boolean serverCompressesSkins = true;
    
    //general
    public static boolean extractOfficialSkins;
    public static boolean allowEquipmentWardrobe = true;
    public static boolean allowClientsToDownloadSkins = false;
    public static boolean allowClientsToUploadSkins = true;
    public static boolean enableHolidayEvents = true;
    public static int startingWardrobeSlots = 3;
    public static boolean libraryShowsModelPreviews = true;
    public static boolean lockDyesOnSkins = false;
    public static boolean instancedDyeTable = false;
    
    //entity skins
    public static int enitiySpawnWithSkinsChance = 75;
    public static int entityDropSkinChance = 10;
    public static String enitiySpawnSkinTargetPath = "/";
    
    //compatibility
    public static boolean allowModsToRegisterWithAPI = true;
    
    public static UUID remotePlayerId;
    
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
            config = new Configuration(file, "1");
            loadConfigFile();
        }
    }

    public static void loadConfigFile() {
        loadCategoryGeneral();
        loadCategoryRecipe();
        loadCategoryCompatibility();
        loadCategoryServer();
        loadCategoryEntitySkins();
        if (config.hasChanged()) {
            config.save();
        }
    }
    
    private static void loadCategoryEntitySkins() {
        enitiySpawnWithSkinsChance = config
                .getInt("enitiySpawnWithSkinsChance", CATEGORY_ENTITY_SKINS, 0, 0, 100,
                        "Percentage change that entities will spawn with skins equipped.");
        
        entityDropSkinChance = config
                .getInt("entityDropSkinChance", CATEGORY_ENTITY_SKINS, 0, 0, 100,
                        "Percentage change that entities will drop equipped skins when killed.");
        
        enitiySpawnSkinTargetPath = "/" + config
                .getString("enitiySpawnSkinTargetPath", CATEGORY_ENTITY_SKINS, "",
                        "Target library path for skin spawned on entities.\n"
                        + "Examples: 'official/' for only skins in the official folder or 'downloads/' for skins in the downloads folder.\n"
                        + "Leave black for all skins.");
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
                .getInt("startingWardrobeSlots", CATEGORY_GENERAL, 3, 1, 8,
                "Number of slot columns the player starts with for skins.");
        
        libraryShowsModelPreviews = config
                .getBoolean("libraryShowsModelPreviews", CATEGORY_GENERAL, true,
                        "Shows model previews in the library.\n"
                        + "Causes a lot of extra load on servers.\n"
                        + "Best to turn off on high population servers");
        
        lockDyesOnSkins = config
                .getBoolean("lockDyesOnSkins", CATEGORY_GENERAL, false,
                        "When enabled players will not be able to remove dyes from skins in the dye table.");
        
        instancedDyeTable = config
                .getBoolean("instancedDyeTable", CATEGORY_GENERAL, false,
                        "If true the dye table will be instanced for each player. Items will be dropped when the table is closed.");
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
    
    private static void loadCategoryCompatibility() {
        allowModsToRegisterWithAPI = config
                .get(CATEGORY_COMPATIBILITY, "allowModsToRegisterWithAPI", true,
                "Allow other mods to register with the Armourer's Workshop API.")
                .getBoolean(true);
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
        
        serverCompressesSkins = config.getBoolean("serverCompressesSkins", CATEGORY_SERVER, true,
                "If enabled the server will compress skins before sending them to clients.\n" +
                "Highly recommended unless the server has a very slow CPU.");
    }
}
