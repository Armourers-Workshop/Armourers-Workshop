package moe.plushie.armourers_workshop.client.config;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConfigHandlerClient {
    
    public static final String CATEGORY_MISC = "misc";
    public static final String CATEGORY_PERFORMANCE = "performance";
    public static final String CATEGORY_CACHE = "cache";
    public static final String CATEGORY_SKIN_PREVIEW = "skin-preview";
    public static final String CATEGORY_DEBUG = "debug";
    
    // Performance
    public static int renderDistanceSkin;
    public static int renderDistanceBlockSkin;
    public static int renderDistanceMannequinEquipment;
    public static int modelBakingThreadCount;
    public static AtomicInteger modelBakingUpdateRate = new AtomicInteger(40);
    public static double lodDistance = 32F;
    public static boolean multipassSkinRendering = true;
    public static boolean slowModelBaking = true;
    public static int maxLodLevels = 4;
    public static boolean useClassicBlockModels;
    
    // Misc
    public static int skinLoadAnimationTime;
    
    // Cache
    public static int skinCacheExpireTime;
    public static int skinCacheMaxSize;
    public static int modelPartCacheExpireTime;
    public static int modelPartCacheMaxSize;
    public static int textureCacheExpireTime;
    public static int textureCacheMaxSize;
    public static int maxSkinRequests;
    
    // Skin preview
    public static boolean skinPreEnabled = false;
    public static boolean skinPreDrawBackground = true;
    public static float skinPreSize = 96F;
    public static float skinPreLocHorizontal = 1F;
    public static float skinPreLocVertical = 0.5F;
    public static boolean skinPreLocFollowMouse = false;
    
    // Debug
    public static int skinRenderType;
    public static boolean showF3DebugInfo;
    public static boolean showSkinTooltipDebugInfo;
    public static int texturePainting;
    
    // Debug tool
    public static boolean showArmourerDebugRender;
    public static boolean wireframeRender;
    public static boolean showLodLevels;
    public static boolean showSkinBlockBounds;
    public static boolean showSkinRenderBounds;
    public static boolean showSortOrderToolTip;
    
    public static Configuration config;
    
    public static void init(File file) {
        if (config == null) {
            config = new Configuration(file, "1");
            loadConfigFile();
        }
    }
    
    public static void loadConfigFile() {
        loadCategoryMisc();
        loadCategoryPerformance();
        loadCategoryCache();
        loadCategorySkinPreview();
        loadCategoryDebug();
        if (config.hasChanged()) {
            config.save();
        }
    }
    
    private static void loadCategoryMisc() {
        config.setCategoryComment(CATEGORY_MISC, "Miscellaneous settings.");
        
        skinLoadAnimationTime = config.getInt("skinLoadAnimationTime", CATEGORY_MISC, 200, 0, 10000,
                "How long skins will display their loading animation for in milliseconds\n"
                + "Settings this to 0 will disable loading animations.");
    }
    
    private static void loadCategoryPerformance() {
        config.setCategoryComment(CATEGORY_PERFORMANCE, "Change (visual quality/performance) ratio by category setting in this category.");
        
        renderDistanceSkin = config.getInt("renderDistanceSkin", CATEGORY_PERFORMANCE, 128, 16, 512,
                "The max distance in blocks that skins will render.");
        
        renderDistanceBlockSkin = config.getInt("renderDistanceBlockSkin", CATEGORY_PERFORMANCE, 128, 16, 512,
                "The max distance in blocks that block skins will be rendered.");
        renderDistanceBlockSkin = renderDistanceBlockSkin * renderDistanceBlockSkin;
        
        renderDistanceMannequinEquipment = config.getInt("renderDistanceMannequinEquipment", CATEGORY_PERFORMANCE, 64, 16, 512,
                "The max distance in blocks that equipment will be rendered on mannequins.");
        
        
        slowModelBaking = config.getBoolean("slowModelBaking", CATEGORY_PERFORMANCE, true,
                "Limits how fast models can be baked to provide a smoother frame rate.");
        
        int cores = Runtime.getRuntime().availableProcessors();
        int bakingCores = MathHelper.ceil((float)cores / 2F);
        bakingCores = MathHelper.clamp(bakingCores, 1, 16);
        modelBakingThreadCount = config.getInt("modelBakingThreadCount", CATEGORY_PERFORMANCE, bakingCores, 1, 16, "");
        config.getCategory(CATEGORY_PERFORMANCE).get("modelBakingThreadCount").setComment(
                "The maximum number of threads that will be used to bake models. [range: " + 1 + " ~ " + 16 + ", default: " + "core count / 2" + "]");
        config.getCategory(CATEGORY_PERFORMANCE).get("modelBakingThreadCount").setRequiresMcRestart(true);
        
        int updateRate = config.getInt("modelBakingUpdateRate", CATEGORY_PERFORMANCE, 40, 10, 1000,
                "How fast models are allowed to bake. Lower values will give smoother frame rate but models will load slower.\n"
                + "Has no effect if 'slowModelBaking' is turned off.");
        modelBakingUpdateRate.set(updateRate);

        multipassSkinRendering = config.getBoolean("multipassSkinRendering", CATEGORY_PERFORMANCE, true,
                "When enabled skin will render in multiple passes to reduce visual artifacts.\n"
                + "Disabling this will improve skin rendering performance at the cost of visual quality.");
        
        lodDistance = config.getFloat("lodDistance", CATEGORY_PERFORMANCE, 32F, 8F, 128F,
                "Distance away that skins will have lod applied to them.");
        
        maxLodLevels = config.getInt("maxLodLevels", CATEGORY_PERFORMANCE, 4, 0, 4,
                "Number of LOD models to create. Higher number should give a boost to framerate at a small cost to VRAM.");
        
        useClassicBlockModels = config.getBoolean("useClassicBlockModels", CATEGORY_PERFORMANCE, false,
                "Use classic block models instead of the 3D model versions.");
    }
    
    private static void loadCategoryCache() {
        config.setCategoryComment(CATEGORY_CACHE, "Change (memory use/IO access) ratio by category setting in this category.");
        
        // Skin cache
        skinCacheExpireTime = config.getInt("skinCacheExpireTime", CATEGORY_CACHE, 600, 1, 3600,
                "How long in seconds the client will keep skins in it's cache.\n"
                + "Default 600 seconds is 10 minutes.");
        config.getCategory(CATEGORY_CACHE).get("skinCacheExpireTime").setRequiresMcRestart(true);
        
        skinCacheMaxSize = config.getInt("skinCacheMaxSize", CATEGORY_CACHE, 2000, 0, 10000,
                "Max size the skin cache can reach before skins are removed. Setting to 0 turns off this option.");
        config.getCategory(CATEGORY_CACHE).get("skinCacheMaxSize").setRequiresMcRestart(true);
        
        // Model cache
        modelPartCacheExpireTime = config.getInt("modelPartCacheExpireTime", CATEGORY_CACHE, 600, 1, 3600,
                "How long in seconds the client will keep skins in it's cache.\n"
                + "Default 600 seconds is 10 minutes.");
        config.getCategory(CATEGORY_CACHE).get("modelPartCacheExpireTime").setRequiresMcRestart(true);
        
        modelPartCacheMaxSize = config.getInt("modelPartCacheMaxSize", CATEGORY_CACHE, 2000, 0, 10000,
                "Max size the skin cache can reach before skins are removed. Setting to 0 turns off this option.");
        config.getCategory(CATEGORY_CACHE).get("modelPartCacheMaxSize").setRequiresMcRestart(true);
        
        // Texture cache
        textureCacheExpireTime = config.getInt("textureCacheExpireTime", CATEGORY_CACHE, 600, 1, 3600,
                "How long in seconds the client will keep textures in it's cache.\n" + 
                "Default 600 seconds is 10 minutes.");
        config.getCategory(CATEGORY_CACHE).get("textureCacheExpireTime").setRequiresMcRestart(true);
        
        textureCacheMaxSize = config.getInt("textureCacheMaxSize", CATEGORY_CACHE, 1000, 0, 5000,
                "Max size the texture cache can reach before textures are removed. Setting to 0 turns off this option.");
        config.getCategory(CATEGORY_CACHE).get("textureCacheMaxSize").setRequiresMcRestart(true);
        
        maxSkinRequests = config.getInt("maxSkinRequests", CATEGORY_CACHE, 10, 1, 50,
                "Maximum number of skin the client can request at one time.");
    }
    
    private static void loadCategorySkinPreview() {
        config.setCategoryComment(CATEGORY_SKIN_PREVIEW, "Setting to configure the skin preview box.");
        
        skinPreEnabled = config.getBoolean("skinPreEnabled", CATEGORY_SKIN_PREVIEW, true,
                "Enables a larger skin preview box when hovering the mouse over a skin.");
        
        skinPreDrawBackground = config.getBoolean("skinPreDrawBackground", CATEGORY_SKIN_PREVIEW, true,
                "Draw a background box for the skin preview.");
        
        skinPreSize = config.getFloat("skinPreSize", CATEGORY_SKIN_PREVIEW, 96F, 16F, 256F,
                "Size of the skin preview.");
        
        skinPreLocHorizontal = config.getFloat("skinPreLocHorizontal", CATEGORY_SKIN_PREVIEW, 0F, 0F, 1F,
                "Horizontal location of the skin preview: 0 = left, 1 = right.");
        
        skinPreLocVertical = config.getFloat("skinPreLocVertical", CATEGORY_SKIN_PREVIEW, 0.5F, 0F, 1F,
                "Vertical location of the skin preview: 0 = top, 1 = bottom.");
        
        skinPreLocFollowMouse = config.getBoolean("skinPreLocFollowMouse", CATEGORY_SKIN_PREVIEW, true,
                "Skin preview will be rendered next to the mouse.");
    }
    
    private static void loadCategoryDebug() {
        config.setCategoryComment(CATEGORY_DEBUG, "Debug settings.");
        
        skinRenderType = config
                .getInt("skinRenderType", CATEGORY_DEBUG, 0, 0, 3,
                "Only change this if you are having rendering issues with skins on players." +
                "(normally fixes skins not rotating on players)\n" +
                "\n" +
                "0 = auto\n" +
                "1 = render event\n" +
                "2 = model attachment\n" +
                "3 = render layer");
        
        showF3DebugInfo = config
                .get(CATEGORY_DEBUG, "showF3DebugInfo", true,
                "Shows extra info on the F3 debug screen.")
                .getBoolean(true);
        
        showSkinTooltipDebugInfo = config
                .get(CATEGORY_DEBUG, "showSkinTooltipDebugInfo", false,
                "Shows extra debug info on skin tooltips.")
                .getBoolean(false);
        
        texturePainting = config
                .getInt("texturePainting", CATEGORY_DEBUG, 0, 0, 2,
                "Texture painting replacing the players texture with a painted version.\n" + 
                "Turning this off may fix issues with the players texture rendering\n" + 
                "incorrectly or showing the steve skin.\n" +
                "\n" +
                "0 = auto\n" +
                "1 = on\n" +
                "2 = off\n");
    }
}
