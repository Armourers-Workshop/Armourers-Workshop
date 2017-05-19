package riskyken.armourersWorkshop.common.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandlerClient {
    
    public static String CATEGORY_CLIENT = "client";
    public static String CATEGORY_DEBUG = "debug";
    
    public static int clientModelCacheTime = 600000;
    public static int clientTextureCacheTime = 600000;
    public static int maxSkinRenderDistance = 128;
    public static int maxModelBakingThreads = 1;
    public static boolean slowModelBaking = true;
    public static boolean multipassSkinRendering = true;
    public static int mannequinMaxEquipmentRenderDistance = 1024;
    public static int blockSkinMaxRenderDistance = 2500;
    public static double lodDistance = 32F;
    public static int skinLoadAnimationTime = 500;
    public static int maxLodLevels = 4;
    
    //debug
    public static boolean skinTextureRenderOverride;
    public static int skinRenderType;
    public static boolean showF3DebugInfo;
    public static boolean showSkinTooltipDebugInfo;
    public static boolean showArmourerDebugRender;
    public static boolean wireframeRender;
    public static boolean disableTexturePainting;
    public static boolean showLodLevels;
    public static boolean showSkinBlockBounds;
    public static boolean showSkinRenderBounds;
    
    public static String globalLibraryUsername = "";
    public static String globalLibraryPassword = "";
    public static String globalLibraryAccessKey = "";
    public static boolean globalLibraryLoggedIn = false;
    
    public static Configuration config;
    
    public static void init(File file) {
        if (config == null) {
            config = new Configuration(file, "1");
            loadConfigFile();
        }
    }
    
    public static void loadConfigFile() {
        loadCategoryClient();
        loadCategoryDebug();
        if (config.hasChanged()) {
            config.save();
        }
    }
    
    private static void loadCategoryClient() {
        maxSkinRenderDistance = config
                .get(CATEGORY_CLIENT, "maxSkinRenderDistance", 8192,
                "The max distance away squared that skins will render.")
                .getInt(8192);
        
        maxModelBakingThreads = config.getInt("maxModelBakingThreads", CATEGORY_CLIENT, 1, 1, 20,
                "The maximum number of threads that will be used to bake models.");
        
        slowModelBaking = config.getBoolean("slowModelBaking", CATEGORY_CLIENT, true,
                "Limits how fast models can be baked to provide a smoother frame rate.");
        
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
        
        mannequinMaxEquipmentRenderDistance = config.getInt("mannequinMaxEquipmentRenderDistance", CATEGORY_CLIENT, 2048, 1, 4096,
                "The max distance squared that equipment will be rendered on mannequins.");
        
        blockSkinMaxRenderDistance = config.getInt("blockSkinMaxRenderDistance", CATEGORY_CLIENT, 8192, 1, 65536,
                "The max distance squared that block skins will be rendered.");
        
        lodDistance = config.getFloat("lodDistance", CATEGORY_CLIENT, 32F, 8, 128,
                "Distance away that skins will have lod applied to them.");
        
        skinLoadAnimationTime = config.getInt("skinLoadAnimationTime", CATEGORY_CLIENT, 500, 0, 10000,
                "How long skins will display their loading animation for in milliseconds\n"
                + "Settings this to 0 will disable loading animations.");
        
        maxLodLevels = config.getInt("maxLodLevels", CATEGORY_CLIENT, 4, 0, 4,
                "Number of LOD models to create. Higher number should give a boost to framerate at a small cost to VRAM.");
    }
    
    private static void loadCategoryDebug() {
        skinRenderType = config
                .getInt("skinRenderType", CATEGORY_DEBUG, 0, 0, 2,
                "Only change this if you are having rendering issues with skins on players." +
                "(normally fixes skins not rotating on players)\n" +
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
        
        disableTexturePainting = config.getBoolean("disableTexturePainting", CATEGORY_DEBUG, false,
                "Disables replacing the players texture with a painted version.\n"
                + "Disabling this may fix issues with the players texture rendering\n"
                + "incorrectly or showing the steve skin.");
    }
}
