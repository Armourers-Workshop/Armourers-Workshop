package moe.plushie.armourers_workshop.init;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class ModConfig {

    public static class Client {

        // General
        public static int enableEmbeddedSkinRenderer = 0;
        public static int enableFirstPersonSkinRenderer = 0;

        // Performance
        public static int renderDistanceSkin;
        public static int renderDistanceBlockSkin;
        public static int renderDistanceMannequinEquipment;
        public static int modelBakingThreadCount = 4;
        public static double lodDistance = 32F;
        public static boolean multipassSkinRendering = true;
        public static int maxLodLevels = 4;
        public static int prefersSeatHoldingTick = 60;

        // Misc
        public static int skinLoadAnimationTime;
        public static boolean enableEntityInInventoryClip = true;
        public static boolean enableSkinLimitLimbs = true;

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
        public static boolean tooltipOpenWardrobe = true;

        // Debug
        public static int texturePaintingType;
        public static boolean showF3DebugInfo;
        public static float ploOffset = -0.01f;

        public static boolean enablePartSubdivide = false;
        public static boolean enableShaderDebug = true;

        public static boolean enableEntityPlacementHighlight = true;
        public static boolean enableBlockPlacementHighlight = true;
        public static boolean enablePaintToolPlacementHighlight = true;

        public static boolean enableMagicWhenContributor = false;

        public static int getNumberOfRenderLayers() {
            if (multipassSkinRendering) {
                return 4;
            } else {
                return 2;
            }
        }

        public static TexturePaintType getTexturePaintType() {
            if (ModConfig.Client.texturePaintingType < 0) {
                return TexturePaintType.DISABLED;
            }
            if (ModConfig.Client.texturePaintingType == 0) {
//            if (ModLoader.isModLoaded("tlauncher_custom_cape_skin")) {
//                return TexturePaintType.MODEL_REPLACE_AW;
//            }
                return TexturePaintType.TEXTURE_REPLACE;
            }
            return TexturePaintType.values()[ModConfig.Client.texturePaintingType];
        }
    }

    public static class Common {

        // General
        public static int maxUndos = 100;
        public static int blockTaskRate = 10; // 10 blocks/t
        public static boolean lockDyesOnSkins = false;
        public static boolean instancedDyeTable = false;
        public static boolean enableProtocolCheck = true;
        public static int serverSkinSendRate = 4000;
        public static boolean enableServerCompressesSkins = true;
        public static int enableEmbeddedSkinRenderer = 0;
        public static int enableFirstPersonSkinRenderer = 0;
        public static boolean enableMatchingByItemId = true;

        // Wardrobe
        public static boolean wardrobeAllowOpening = true;
        public static boolean showWardrobeSkins = true;
        public static boolean showWardrobeOutfits = true;
        public static boolean showWardrobeDisplaySettings = true;
        public static boolean showWardrobeColorSettings = true;
        public static boolean showWardrobeDyeSetting = true;
        public static boolean showWardrobeContributorSetting = true;

        public static int prefersWardrobePlayerSlots = 3;
        public static int prefersWardrobeMobSlots = 3;
        public static int prefersWardrobeDropOnDeath = 0;

        // Library
        public static boolean extractOfficialSkins;
        public static boolean allowLibraryPreviews = true;
        public static boolean allowDownloadingSkins = false;
        public static boolean allowUploadingSkins = true;
        public static boolean allowLibraryRemoteManage = false;

        // Holiday events
        public static boolean disableAllHolidayEvents = false;

        // Cache
        public static int skinCacheExpireTime = 600;
        public static int skinCacheMaxSize = 2000;

        // Global Skin Library
        public static ArrayList<String> customSkinServerURLs = new ArrayList<>();

        // Overrides
        public static ArrayList<String> overrides = new ArrayList<>();
        public static ArrayList<String> disableMatchingItems = new ArrayList<>();

        public static boolean isGlobalSkinServer() {
            // if use specify a skin server, it a private skin server.
            return customSkinServerURLs.isEmpty();
        }

        public static boolean canOpenWardrobe(Entity target, Player operator) {
            if (!wardrobeAllowOpening) {
                return false;
            }
            if (operator.isCreative()) {
                return true;
            }
            // No wardrobe tabs are active.
            return showWardrobeSkins || showWardrobeOutfits || showWardrobeDisplaySettings || showWardrobeColorSettings || showWardrobeDyeSetting;
        }
    }

    public enum TexturePaintType {
        DISABLED, TEXTURE_REPLACE, MODEL_REPLACE_MC, MODEL_REPLACE_AW
    }

    public static void init() {
        ModConfigSpec.init();
    }

    public static boolean enableEmbeddedSkinRenderer() {
        int flags = Client.enableEmbeddedSkinRenderer;
        if (flags == 0) {
            flags = Common.enableEmbeddedSkinRenderer;
        }
        // 0 auto(reserve), 1 disable, 2 enable
        return flags == 2;
    }

    public static boolean enableFirstPersonSkinRenderer() {
        int flags = Client.enableFirstPersonSkinRenderer;
        if (flags == 0) {
            flags = Common.enableFirstPersonSkinRenderer;
        }
        // 0 auto(reserve), 1 disable, 2 enable
        return flags == 2;
    }
}
