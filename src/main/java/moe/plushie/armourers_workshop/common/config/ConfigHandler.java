package moe.plushie.armourers_workshop.common.config;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import moe.plushie.armourers_workshop.common.holiday.Holiday;
import moe.plushie.armourers_workshop.common.holiday.ModHolidays;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_WARDROBE = "wardrobe";
    public static final String CATEGORY_LIBRARY = "library";
    public static final String CATEGORY_RECIPE = "recipe";
    public static final String CATEGORY_HOLIDAY = "holiday-events";
    public static final String CATEGORY_ENTITY_SKINS = "entity-skins";
    public static final String CATEGORY_CACHE = "cache";
    
    public static Configuration config;
    
    // General
    public static int maxUndos;
    public static boolean lockDyesOnSkins = false;
    public static boolean instancedDyeTable = false;
    public static int serverSkinSendRate = 4000;
    public static boolean serverCompressesSkins = true;
    
    // Wardrobe
    public static boolean allowOpeningWardrobe = true;
    public static boolean enableWardrobeTabSkins = true;
    public static boolean enableWardrobeTabOutfits = true;
    public static boolean enableWardrobeTabDisplaySettings = true;
    public static boolean enableWardrobeTabColourSettings = true;
    public static boolean enableWardrobeTabDyes = true;
    public static int startingWardrobeSlots = 3;
    public static String storageId;
    public static int dropSkinsOnDeath = 0;
    
    // Library
    public static boolean extractOfficialSkins;
    public static boolean libraryShowsModelPreviews = true;
    public static boolean allowDownloadingSkins = false;
    public static boolean allowUploadingSkins = true;
    
    // Recipes
    public static boolean disableRecipes;
    public static boolean disableDollRecipe;
    public static boolean disableSkinningRecipes;
    public static boolean hideDollFromCreativeTabs;
    public static boolean enableRecoveringSkins;
    
    // Holiday events
    public static boolean disableAllHolidayEvents = true;
    
    // Entity skins
    public static int enitiySpawnWithSkinsChance = 75;
    public static int entityDropSkinChance = 10;
    public static String enitiySpawnSkinTargetPath = "/";
    
    // Cache
    public static int skinCacheExpireTime;
    public static int skinCacheMaxSize;
    
    // Config sync
    public static UUID remotePlayerId;

    public static void init(File file) {
        if (config == null) {
            config = new Configuration(file, "1");
            loadConfigFile();
        }
    }

    public static void loadConfigFile() {
        loadCategoryGeneral();
        loadCategoryWardrobe();
        loadCategoryLibrary();
        loadCategoryRecipe();
        loadCategoryHolidayEvents();
        loadCategoryEntitySkins();
        loadCategoryCache();
        
        if (config.hasChanged()) {
            config.save();
        }
    }
    
    private static void loadCategoryGeneral() {
        config.setCategoryComment(CATEGORY_GENERAL, "General settings.");
        
        maxUndos = config.getInt("maxUndos", CATEGORY_GENERAL, 100, 0, 1000,
                "Max number of undos a player has for block painting.");
        
        lockDyesOnSkins = config.getBoolean("lockDyesOnSkins", CATEGORY_GENERAL, false,
                "When enabled players will not be able to remove dyes from skins in the dye table.");
        
        instancedDyeTable = config.getBoolean("instancedDyeTable", CATEGORY_GENERAL, false,
                "If true the dye table will be instanced for each player. Items will be dropped when the table is closed.");
        
        serverSkinSendRate = config.getInt("serverModelSendRate", CATEGORY_GENERAL, 4000, 0, 8000,
                "The maximum number of skins the server is allow to send every minute.\n"
                + "Less that 1 equals unlimited. (not recommended may cause bandwidth and cpu spikes on the server)");
        
        serverCompressesSkins = config.getBoolean("serverCompressesSkins", CATEGORY_GENERAL, true,
                "If enabled the server will compress skins before sending them to clients.\n" +
                "Highly recommended unless the server has a very slow CPU.");
    }
    
    private static void loadCategoryWardrobe() {
        config.setCategoryComment(CATEGORY_WARDROBE, "Setting for the players wardrobe.");
        
        allowOpeningWardrobe = config.getBoolean("allowOpeningWardrobe", CATEGORY_WARDROBE, true,
                "Allow the player to open the wardrobe GUI.");
        
        startingWardrobeSlots = config.getInt("startingWardrobeSlots", CATEGORY_WARDROBE, 3, 1, 8,
                "Number of slot columns the player starts with for skins.");
        
        dropSkinsOnDeath = config.getInt("dropSkinsOnDeath", CATEGORY_WARDROBE, 0, 0, 2,
                "Should skins be dropped on player death.\n"
                + "0 = use keep inventory rule\n"
                + "1 = never drop\n"
                + "2 = always drop");
    }
    
    private static void loadCategoryLibrary() {
        config.setCategoryComment(CATEGORY_LIBRARY, "Setting for the library blocks.");
        
        allowDownloadingSkins = config.getBoolean("allowDownloadingSkins", CATEGORY_LIBRARY, false,
                "Allows clients to save skins from a server to their local computer using the library.");
        
        allowUploadingSkins = config.getBoolean("allowUploadingSkins", CATEGORY_LIBRARY, true,
                "Allows clients to load skins from their local computer onto the server using the library.");
        
        extractOfficialSkins = config.getBoolean("extractOfficialSkins", CATEGORY_LIBRARY, true,
                "Allow the mod to extract the official skins that come with the mod into the library folder.");
        
        libraryShowsModelPreviews = config.getBoolean("libraryShowsModelPreviews", CATEGORY_LIBRARY, true,
                        "Shows model previews in the library.\n"
                        + "Causes a lot of extra load on servers.\n"
                        + "Best to turn off on high population servers");
    }
    
    private static void loadCategoryRecipe() {
        config.setCategoryComment(CATEGORY_RECIPE, "Setting for mod recipes.");
        
        disableRecipes = config.getBoolean("disableRecipes", CATEGORY_RECIPE, false,
                "Disable vanilla recipes. Use if you want to manually add recipes for a mod pack.");
        
        disableDollRecipe = config.getBoolean("disableDollRecipe", CATEGORY_RECIPE, false,
                "Disable hidden in world doll recipe.");
        
        disableSkinningRecipes = config.getBoolean("disableSkinningRecipes", CATEGORY_RECIPE, false,
                "Disable skinning table recipes.");
        
        hideDollFromCreativeTabs = config.getBoolean("hideDollFromCreativeTabs", CATEGORY_RECIPE, true,
                "Hides the doll block from the creative tab and NEI.");
        
        enableRecoveringSkins = config.getBoolean("enableRecoveringSkins", CATEGORY_RECIPE, false,
                "Enable copying the skin off an item in the skinning table");
    }
    
    private static void loadCategoryHolidayEvents() {
        config.setCategoryComment(CATEGORY_HOLIDAY, "Enable/disable holiday events.");
        
        disableAllHolidayEvents = config.getBoolean("disableAllHolidayEvents", CATEGORY_HOLIDAY, true,
                "Setting to true will disable all holiday events. What's wrong with you!");
        
        SimpleDateFormat sdf = new SimpleDateFormat("MM:dd:HH", Locale.ENGLISH);
        
        for (Holiday holiday : ModHolidays.getHolidays()) {
            boolean holidayEnabled = config.getBoolean("holiday-" + holiday.getName() + "-enabled", CATEGORY_HOLIDAY, true,
                    "Enable holiday.");
            
            Calendar startDate = holiday.getStartDate();
            Calendar endDate = holiday.getEndDate();
            
            String dates = config.getString("holiday-" + holiday.getName() + "-range", CATEGORY_HOLIDAY,
                    sdf.format(startDate.getTime()) + "-" + sdf.format(endDate.getTime()),
                    "Holiday date range. Format (Start Date-End Date) (MONTH:DAY:HOUR-MONTH:DAY:HOUR)");

            String startDateStr = sdf.format(startDate.getTime());
            String endDateStr = sdf.format(endDate.getTime());
            
            if (dates.contains(":")) {
                String[] split = dates.split(":");
                startDateStr = split[0];
                endDateStr = split[1];
            }
            
            try {
                Date date = sdf.parse(startDateStr);
                startDate.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                Date date = sdf.parse(endDateStr);
                endDate.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
            holiday.setEnabled(holidayEnabled);
            holiday.setStartDate(startDate);
            holiday.setEndDate(endDate);
        }
    }
    
    private static void loadCategoryEntitySkins() {
        config.setCategoryComment(CATEGORY_ENTITY_SKINS, "Control how/if entities spawm with skin on them.");
        
        enitiySpawnWithSkinsChance = config.getInt("enitiySpawnWithSkinsChance", CATEGORY_ENTITY_SKINS, 0, 0, 100,
                        "Percentage change that entities will spawn with skins equipped.");
        
        entityDropSkinChance = config.getInt("entityDropSkinChance", CATEGORY_ENTITY_SKINS, 0, 0, 100,
                        "Percentage change that entities will drop equipped skins when killed.");
        
        enitiySpawnSkinTargetPath = "/" + config.getString("enitiySpawnSkinTargetPath", CATEGORY_ENTITY_SKINS, "",
                        "Target library path for skin spawned on entities.\n"
                        + "Examples: 'official/' for only skins in the official folder or 'downloads/' for skins in the downloads folder.\n"
                        + "Leave black for all skins.");
    }
    

    
    private static void loadCategoryCache() {
        config.setCategoryComment(CATEGORY_CACHE, "Change (memory use/IO access) ratio by category setting in this category.");
        
        skinCacheExpireTime = config.getInt("skinCacheExpireTime", CATEGORY_CACHE, 6000, 1, 3600,
                "How long in seconds the server will keep skins in it's cache.\n"
                + "Default 600 seconds is 10 minutes.");
        
        skinCacheMaxSize = config.getInt("skinCacheMaxSize", CATEGORY_CACHE, 2000, 0, 10000,
                "Max size the skin cache can reach before skins are removed. Setting to 0 turns off this option.");
        
    }
}
