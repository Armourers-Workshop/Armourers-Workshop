package moe.plushie.armourers_workshop.client.config;

import java.io.File;

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
    public static final String CATEGORY_TOOLTIP = "tooltip";
    public static final String CATEGORY_DEBUG = "debug";

    // Performance
    public static int renderDistanceSkin;
    public static int renderDistanceBlockSkin;
    public static int renderDistanceMannequinEquipment;
    public static int modelBakingThreadCount;
    public static double lodDistance = 32F;
    public static boolean multipassSkinRendering = true;
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
    public static int fastCacheSize;

    // Skin preview
    public static boolean skinPreEnabled = false;
    public static boolean skinPreDrawBackground = true;
    public static float skinPreSize = 96F;
    public static float skinPreLocHorizontal = 1F;
    public static float skinPreLocVertical = 0.5F;
    public static boolean skinPreLocFollowMouse = false;

    // Tool-tip
    public static boolean tooltipHasSkin;
    public static boolean tooltipSkinName;
    public static boolean tooltipSkinAuthor;
    public static boolean tooltipSkinType;
    public static boolean tooltipDebug;
    public static boolean tooltipFlavour;
    public static boolean tooltipOpenWardrobe;

    // Debug
    public static int texturePaintingType;
    public static boolean showF3DebugInfo;

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
        loadCategoryTooltip();
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
        config.setCategoryComment(CATEGORY_PERFORMANCE, "Change (visual quality/performance) ratio by changing setting in this category.");
        
        renderDistanceSkin = config.getInt("renderDistanceSkin", CATEGORY_PERFORMANCE, 128, 16, 512,
                "The max distance in blocks that skins will render.");
        
        renderDistanceBlockSkin = config.getInt("renderDistanceBlockSkin", CATEGORY_PERFORMANCE, 128, 16, 512,
                "The max distance in blocks that block skins will be rendered.");
        renderDistanceBlockSkin = renderDistanceBlockSkin * renderDistanceBlockSkin;
        
        renderDistanceMannequinEquipment = config.getInt("renderDistanceMannequinEquipment", CATEGORY_PERFORMANCE, 64, 16, 512,
                "The max distance in blocks that equipment will be rendered on mannequins.");
        
        
        int cores = Runtime.getRuntime().availableProcessors();
        int bakingCores = MathHelper.ceil(cores / 2F);
        bakingCores = MathHelper.clamp(bakingCores, 1, 16);
        modelBakingThreadCount = config.getInt("modelBakingThreadCount", CATEGORY_PERFORMANCE, bakingCores, 1, 16, "");
        config.getCategory(CATEGORY_PERFORMANCE).get("modelBakingThreadCount").setComment(
                "The maximum number of threads that will be used to bake models. [range: " + 1 + " ~ " + 16 + ", default: " + "core count / 2" + "]");
        config.getCategory(CATEGORY_PERFORMANCE).get("modelBakingThreadCount").setRequiresMcRestart(true);

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
        config.setCategoryComment(CATEGORY_CACHE, "Change (memory use/IO access) ratio by changing setting in this category.");
        
        // Skin cache
        skinCacheExpireTime = config.getInt("skinCacheExpireTime", CATEGORY_CACHE, 600, 0, 3600,
                "How long in seconds the client will keep skins in it's cache.\n"
                + "Default 600 seconds is 10 minutes.\n"
                + "Setting to 0 turns off this option.");
        config.getCategory(CATEGORY_CACHE).get("skinCacheExpireTime").setRequiresMcRestart(true);
        
        skinCacheMaxSize = config.getInt("skinCacheMaxSize", CATEGORY_CACHE, 2000, 0, 10000,
                "Max size the skin cache can reach before skins are removed.\n"
                + "Setting to 0 turns off this option.");
        config.getCategory(CATEGORY_CACHE).get("skinCacheMaxSize").setRequiresMcRestart(true);
        
        // Model cache
        modelPartCacheExpireTime = config.getInt("modelPartCacheExpireTime", CATEGORY_CACHE, 600, 0, 3600,
                "How long in seconds the client will keep model parts in it's cache.\n"
                + "Default 600 seconds is 10 minutes.\n"
                + "Setting to 0 turns off this option.");
        config.getCategory(CATEGORY_CACHE).get("modelPartCacheExpireTime").setRequiresMcRestart(true);
        
        modelPartCacheMaxSize = config.getInt("modelPartCacheMaxSize", CATEGORY_CACHE, 2000, 0, 10000,
                "Max size the cache can reach before model parts are removed.\n"
                + "Setting to 0 turns off this option.");
        config.getCategory(CATEGORY_CACHE).get("modelPartCacheMaxSize").setRequiresMcRestart(true);
        
        // Texture cache
        textureCacheExpireTime = config.getInt("textureCacheExpireTime", CATEGORY_CACHE, 600, 0, 3600,
                "How long in seconds the client will keep textures in it's cache.\n" + 
                "Default 600 seconds is 10 minutes.\n"
                + "Setting to 0 turns off this option.");
        config.getCategory(CATEGORY_CACHE).get("textureCacheExpireTime").setRequiresMcRestart(true);
        
        textureCacheMaxSize = config.getInt("textureCacheMaxSize", CATEGORY_CACHE, 1000, 0, 5000,
                "Max size the texture cache can reach before textures are removed.\n"
                + "Setting to 0 turns off this option.");
        config.getCategory(CATEGORY_CACHE).get("textureCacheMaxSize").setRequiresMcRestart(true);
        
        maxSkinRequests = config.getInt("maxSkinRequests", CATEGORY_CACHE, 10, 1, 50,
                "Maximum number of skin the client can request at one time.");
        
        fastCacheSize = config.getInt("fastCacheSize", CATEGORY_CACHE, 5000, 0, Integer.MAX_VALUE,
                "Size of client size cache.\n"
                + "Setting to 0 turns off this option.");
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

    private static void loadCategoryTooltip() {
        config.setCategoryComment(CATEGORY_TOOLTIP, "Setting to configure tooltips on skinned items.");

        tooltipHasSkin = config.getBoolean("tooltipHasSkin", CATEGORY_TOOLTIP, true, "Show has skin tooltip on skinned items.");
        tooltipSkinName = config.getBoolean("tooltipSkinName", CATEGORY_TOOLTIP, true, "Show skin name tooltip on items.");
        tooltipSkinAuthor = config.getBoolean("tooltipSkinAuthor", CATEGORY_TOOLTIP, true, "Show skin author tooltip on items.");
        tooltipSkinType = config.getBoolean("tooltipSkinType", CATEGORY_TOOLTIP, true, "Show skin type tooltip on items.");
        tooltipDebug = config.getBoolean("tooltipDebug", CATEGORY_TOOLTIP, false, "Show skin debug info on items.");
        tooltipFlavour = config.getBoolean("tooltipFlavour", CATEGORY_TOOLTIP, true, "Show skin flavoue text tooltip on items.");
        tooltipOpenWardrobe = config.getBoolean("tooltipOpenWardrobe", CATEGORY_TOOLTIP, true, "Show open wardrobe message on skins.");
    }

    private static void loadCategoryDebug() {
        config.setCategoryComment(CATEGORY_DEBUG, "Debug settings.");
        
        showF3DebugInfo = config
                .get(CATEGORY_DEBUG, "showF3DebugInfo", true,
                "Shows extra info on the F3 debug screen.")
                .getBoolean(true);
        
        texturePaintingType = config
                .getInt("texturePaintingType", CATEGORY_DEBUG, 0, -1, 2,
                "Texture painting replacing the players texture with a painted version.\n" + 
                "Turning this off may fix issues with the players texture rendering\n" + 
                "incorrectly or showing the steve skin.\n" +
                "\n" +
                "-1 = disabled\n" +
                "0 = auto\n" +
                "1 = texture_replace (replaces the players texture - LEGACY)\n" +
                "2 = model_replace_mc (render using a mc model - slower, more compatible - NOT IMPLEMENTED)\n" +
                "3 = model_replace_aw (render using a aw model - faster, less compatible)\n");
    }
}
