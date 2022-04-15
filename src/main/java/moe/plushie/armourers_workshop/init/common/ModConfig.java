package moe.plushie.armourers_workshop.init.common;

public class ModConfig {

    // Performance
    public static int renderDistanceSkin;
    public static int renderDistanceBlockSkin;
    public static int renderDistanceMannequinEquipment;
    public static int modelBakingThreadCount;
    public static double lodDistance = 32F;
    public static boolean multipassSkinRendering = true;
    public static int maxLodLevels = 4;
    public static boolean useClassicBlockModels;
    public static int prefersSeatHoldingTick = 60;

    // Misc
    public static int skinLoadAnimationTime;
    public static boolean enableLibraryManage = false;

    // Cache
    public static int skinCacheExpireTime;
    public static int skinCacheMaxSize;
    public static int modelPartCacheExpireTime;
    public static int modelPartCacheMaxSize;
    public static int textureCacheExpireTime;
    public static int textureCacheMaxSize;
    public static int maxSkinRequests;
    public static int fastCacheSize;
    public static int maxSkinSlots = 10;

    // Skin preview
    public static boolean skinPreEnabled = true;
    public static boolean skinPreDrawBackground = true;
    public static int skinPreSize = 96;
    public static double skinPreLocHorizontal = 0.0;
    public static double skinPreLocVertical = 0.5;
    public static boolean skinPreLocFollowMouse = true;

    // Tool-tip
    public static boolean tooltipHasSkin = true;
    public static boolean tooltipSkinName = true;
    public static boolean tooltipSkinAuthor = true;
    public static boolean tooltipSkinType = true;
    public static boolean tooltipFlavour = true;
    public static boolean tooltipProperties = false;
    public static boolean tooltipOpenWardrobe = true;
    public static boolean debugTooltip = false;

    // Debug
    public static int texturePaintingType;
    public static boolean showF3DebugInfo;
    public static float ploOffset = -0.01f;

    public static boolean enableEntityPlacementHighlight = true;
    public static boolean enableBlockPlacementHighlight = true;

    public static boolean enableModelOverridden = true;
    public static boolean enableWireframeRender = false;
    public static boolean enableMagicWhenContributor = false;

    // Wardrobe
//    public static boolean wardrobeAllowOpening = true;
    public static boolean showWardrobeSkins = true;
    public static boolean showWardrobeOutfits = true;
    public static boolean showWardrobeDisplaySettings = true;
    public static boolean showWardrobeColourSettings = true;
    public static boolean showWardrobeDyeSetting = true;
    public static boolean showWardrobeContributorSetting = true;

    public static int prefersWardrobeSlots = 3;
    public static int prefersWardrobeMobSlots = 3;
    public static int prefersWardrobeDropOnDeath = 0;

    // Debug tool
    public static boolean showArmourerDebugRender;
    public static boolean showLodLevels;
    public static boolean showSkinBlockBounds;
    public static boolean showSkinRenderBounds;
    public static boolean showSortOrderToolTip;


    public static boolean debugSkinnableBlock = false;
    public static boolean debugHologramProjectorBlock = false;

    public static boolean debugSkinBounds = false;
    public static boolean debugSkinOrigin = false;

    public static boolean debugSkinPartBounds = false;
    public static boolean debugSkinPartOrigin = false;

    public static boolean debugTargetBounds = false;

    public static boolean showDebugTextureBounds = false;
    public static boolean showDebugSpin = false;

    public static boolean enablePolygonOffset = true;

    // Library
    public static boolean extractOfficialSkins;
    public static boolean libraryShowsModelPreviews = true;
    public static boolean allowDownloadingSkins = false;
    public static boolean allowUploadingSkins = true;

    public static int getNumberOfRenderLayers() {
        if (multipassSkinRendering) {
            return 4;
        } else {
            return 2;
        }
    }

    public static TexturePaintType getTexturePaintType() {
        if (ModConfig.texturePaintingType < 0) {
            return TexturePaintType.DISABLED;
        }
        if (ModConfig.texturePaintingType == 0) {
//            if (ModLoader.isModLoaded("tlauncher_custom_cape_skin")) {
//                return TexturePaintType.MODEL_REPLACE_AW;
//            }
            return TexturePaintType.TEXTURE_REPLACE;
        }
        return TexturePaintType.values()[ModConfig.texturePaintingType];
    }

    public enum TexturePaintType {
        DISABLED, TEXTURE_REPLACE, MODEL_REPLACE_MC, MODEL_REPLACE_AW
    }

}
