package riskyken.armourersWorkshop.common.config;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import riskyken.armourersWorkshop.common.addons.ModAddonManager;
import riskyken.armourersWorkshop.common.undo.UndoManager;
import riskyken.armourersWorkshop.common.update.UpdateCheck;

public class ConfigHandler {

    public static String CATEGORY_RECIPE = "recipe";
    public static String CATEGORY_GENERAL = "general";
    public static String CATEGORY_COMPATIBILITY = "compatibility";
    public static String CATEGORY_SERVER = "server";
    
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
        
        
        Property prop = config.get(CATEGORY_COMPATIBILITY, "itemOverrides", ModAddonManager.getDefaultOverrides());
        prop.setLanguageKey("itemOverrides");
        prop.comment = "List of items that can have skins applied.\n"
                + "Format [override type:mod id:item name]\n"
                + "Valid override types are:\n"
                + "sword\n"
                + "item\n"
                + "pickaxe\n"
                + "axe\n"
                + "shovel\n"
                + "hoe\n"
                + "bow";
        ModAddonManager.itemOverrides.clear();
        ModAddonManager.itemOverrides.addAll(Arrays.asList(prop.getStringList()));
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
