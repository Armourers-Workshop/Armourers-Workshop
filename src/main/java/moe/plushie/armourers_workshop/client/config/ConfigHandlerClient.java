package moe.plushie.armourers_workshop.client.config;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConfigHandlerClient {
    
    public static String CATEGORY_CLIENT = "client";
    public static String CATEGORY_SKIN_PREVIEW = "skin-preview";
    public static String CATEGORY_DEBUG = "debug";
    
    public static int skinCacheExpireTime;
    public static int skinCacheMaxSize;
    public static int textureCacheTime;
    public static int textureCacheMaxSize;
    public static int modelBakingThreadCount;
    public static AtomicInteger modelBakingUpdateRate = new AtomicInteger(40);
    
    public static int skinMaxRenderDistance;
    public static boolean slowModelBaking = true;
    public static boolean multipassSkinRendering = true;
    public static int mannequinMaxEquipmentRenderDistance = 1024;
    public static int blockSkinMaxRenderDistance = 2500;
    public static double lodDistance = 32F;
    public static int skinLoadAnimationTime;
    public static int maxLodLevels = 4;
    
    // Skin preview
    public static boolean skinPreEnabled = false;
    public static boolean skinPreDrawBackground = true;
    public static float skinPreSize = 96F;
    public static float skinPreLocHorizontal = 1F;
    public static float skinPreLocVertical = 0.5F;
    public static boolean skinPreLocFollowMouse = false;
    
    // Debug
    public static boolean skinTextureRenderOverride;
    public static int skinRenderType;
    public static boolean showF3DebugInfo;
    public static boolean showSkinTooltipDebugInfo;
    public static boolean showArmourerDebugRender;
    public static boolean wireframeRender;
    public static int texturePainting;
    public static boolean showLodLevels;
    public static boolean showSkinBlockBounds;
    public static boolean showSkinRenderBounds;
    
    public static Configuration config;
    
    public static void init(File file) {
        if (config == null) {
            config = new Configuration(file, "2");
            if (config.getLoadedConfigVersion().equals("1")) {
                config.getCategory(CATEGORY_DEBUG).remove("disableTexturePainting");
            }
            loadConfigFile();
        }
    }
    
    public static void loadConfigFile() {
        loadCategoryClient();
        loadCategorySkinPreview();
        loadCategoryDebug();
        if (config.hasChanged()) {
            config.save();
        }
    }
    
    private static void loadCategoryClient() {
        skinCacheExpireTime = config.getInt("skinCacheExpireTime", CATEGORY_CLIENT, 600, 1, 3600,
                "How long in seconds the client will keep skins in it's cache.\n"
                + "Default 600 seconds is 10 minutes.");
        config.getCategory(CATEGORY_CLIENT).get("skinCacheExpireTime").setRequiresMcRestart(true);
        
        skinCacheMaxSize = config.getInt("skinCacheMaxSize", CATEGORY_CLIENT, 2000, 0, 10000,
                "Max size the skin cache can reach before skins are removed. Setting to 0 turns off this option.");
        config.getCategory(CATEGORY_CLIENT).get("skinCacheMaxSize").setRequiresMcRestart(true);
        
        textureCacheTime = config
                .getInt("textureCacheTime", CATEGORY_CLIENT, 600, 1, 3600,
                "How long in seconds the client will keep textures in it's cache.\n" + 
                "Default 600 seconds is 10 minutes.");
        config.getCategory(CATEGORY_CLIENT).get("textureCacheTime").setRequiresMcRestart(true);
        
        textureCacheTime = config.getInt("textureCacheTime", CATEGORY_CLIENT, 500, 1, 10000,
                "Max size the texture cache can reach before textures are removed. Setting to 0 turns off this option.");
        config.getCategory(CATEGORY_CLIENT).get("textureCacheTime").setRequiresMcRestart(true);
        
        int cores = Runtime.getRuntime().availableProcessors();
        int bakingCores = MathHelper.ceil((float)cores / 2F);
        bakingCores = MathHelper.clamp(bakingCores, 1, 16);
        
        modelBakingThreadCount = config.getInt("modelBakingThreadCount", CATEGORY_CLIENT, bakingCores, 1, 16, "");
        config.getCategory(CATEGORY_CLIENT).get("modelBakingThreadCount").setComment(
                "The maximum number of threads that will be used to bake models. [range: " + 1 + " ~ " + 16 + ", default: " + "core count / 2" + "]");
        config.getCategory(CATEGORY_CLIENT).get("modelBakingThreadCount").setRequiresMcRestart(true);
        
        int updateRate = config.getInt("modelBakingUpdateRate", CATEGORY_CLIENT, 40, 10, 1000,
                "How fast models are allowed to bake. Lower values will give smoother frame rate but models will load slower.");
        modelBakingUpdateRate.set(updateRate);
        
        skinMaxRenderDistance = config
                .get(CATEGORY_CLIENT, "skinMaxRenderDistance", 8192,
                "The max distance away squared that skins will render.")
                .getInt(8192);
        
        slowModelBaking = config.getBoolean("slowModelBaking", CATEGORY_CLIENT, true,
                "Limits how fast models can be baked to provide a smoother frame rate.");
        
        multipassSkinRendering = config.getBoolean("multipassSkinRendering", CATEGORY_CLIENT, true,
                "When enabled skin will render in multiple passes to reduce visual artifacts.\n"
                + "Disabling this will improve skin rendering performance at the cost of visual quality.");
        
        mannequinMaxEquipmentRenderDistance = config.getInt("mannequinMaxEquipmentRenderDistance", CATEGORY_CLIENT, 2048, 1, 4096,
                "The max distance squared that equipment will be rendered on mannequins.");
        
        blockSkinMaxRenderDistance = config.getInt("blockSkinMaxRenderDistance", CATEGORY_CLIENT, 8192, 1, 65536,
                "The max distance squared that block skins will be rendered.");
        
        lodDistance = config.getFloat("lodDistance", CATEGORY_CLIENT, 32F, 8F, 128F,
                "Distance away that skins will have lod applied to them.");
        
        skinLoadAnimationTime = config.getInt("skinLoadAnimationTime", CATEGORY_CLIENT, 200, 0, 10000,
                "How long skins will display their loading animation for in milliseconds\n"
                + "Settings this to 0 will disable loading animations.");
        
        maxLodLevels = config.getInt("maxLodLevels", CATEGORY_CLIENT, 4, 0, 4,
                "Number of LOD models to create. Higher number should give a boost to framerate at a small cost to VRAM.");
    }
    
    private static void loadCategorySkinPreview() {
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
        skinRenderType = config
                .getInt("skinRenderType", CATEGORY_DEBUG, 0, 0, 3,
                "Only change this if you are having rendering issues with skins on players." +
                "(normally fixes skins not rotating on players)\n" +
                "\n" +
                "0 = auto\n" +
                "1 = render event\n" +
                "2 = model attachment\n" +
                "3 = render layer");
        
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
