package moe.plushie.armourers_workshop.init.common;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ArmourersConfig extends ModConfig {

    public static boolean enableEntityInInventoryClip = true;
    public static ClientConfig CLIENT;
    public static ForgeConfigSpec CLIENT_SPEC;

    public static void bakeConfig() {
        skinLoadAnimationTime = CLIENT.skinLoadAnimationTime.get();

        renderDistanceSkin = CLIENT.renderDistanceSkin.get();
        renderDistanceBlockSkin = CLIENT.renderDistanceBlockSkin.get() * CLIENT.renderDistanceBlockSkin.get();
        renderDistanceMannequinEquipment = CLIENT.renderDistanceMannequinEquipment.get();

        modelBakingThreadCount = CLIENT.modelBakingThreadCount.get();
        multipassSkinRendering = CLIENT.multipassSkinRendering.get();
        lodDistance = CLIENT.lodDistance.get();
        maxLodLevels = CLIENT.maxLodLevels.get();
        useClassicBlockModels = CLIENT.useClassicBlockModels.get();

        skinCacheExpireTime = CLIENT.skinCacheExpireTime.get();
        skinCacheMaxSize = CLIENT.skinCacheMaxSize.get();
        modelPartCacheExpireTime = CLIENT.modelPartCacheExpireTime.get();
        modelPartCacheMaxSize = CLIENT.modelPartCacheMaxSize.get();
        textureCacheExpireTime = CLIENT.textureCacheExpireTime.get();
        textureCacheMaxSize = CLIENT.textureCacheMaxSize.get();
        maxSkinRequests = CLIENT.maxSkinRequests.get();
        fastCacheSize = CLIENT.fastCacheSize.get();

        skinPreEnabled = CLIENT.skinPreEnabled.get();
        skinPreDrawBackground = CLIENT.skinPreDrawBackground.get();
        skinPreSize = CLIENT.skinPreSize.get();
        skinPreLocHorizontal = CLIENT.skinPreLocHorizontal.get();
        skinPreLocVertical = CLIENT.skinPreLocVertical.get();
        skinPreLocFollowMouse = CLIENT.skinPreLocFollowMouse.get();

        tooltipHasSkin = CLIENT.tooltipHasSkin.get();
        tooltipSkinName = CLIENT.tooltipSkinName.get();
        tooltipSkinAuthor = CLIENT.tooltipSkinAuthor.get();
        tooltipSkinType = CLIENT.tooltipSkinType.get();
        debugTooltip = CLIENT.tooltipDebug.get();
        tooltipFlavour = CLIENT.tooltipFlavour.get();
        tooltipOpenWardrobe = CLIENT.tooltipOpenWardrobe.get();

        showF3DebugInfo = CLIENT.showF3DebugInfo.get();
        texturePaintingType = CLIENT.texturePaintingType.get();
    }

    public static void init() {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
        bakeConfig();
    }

    static class ClientConfig {

        // Performance
        ForgeConfigSpec.IntValue renderDistanceSkin;
        ForgeConfigSpec.IntValue renderDistanceBlockSkin;
        ForgeConfigSpec.IntValue renderDistanceMannequinEquipment;
        ForgeConfigSpec.IntValue modelBakingThreadCount;
        ForgeConfigSpec.DoubleValue lodDistance;
        ForgeConfigSpec.BooleanValue multipassSkinRendering;
        ForgeConfigSpec.IntValue maxLodLevels;
        ForgeConfigSpec.BooleanValue useClassicBlockModels;

        // Misc
        ForgeConfigSpec.IntValue skinLoadAnimationTime;

        // Cache
        ForgeConfigSpec.IntValue skinCacheExpireTime;
        ForgeConfigSpec.IntValue skinCacheMaxSize;
        ForgeConfigSpec.IntValue modelPartCacheExpireTime;
        ForgeConfigSpec.IntValue modelPartCacheMaxSize;
        ForgeConfigSpec.IntValue textureCacheExpireTime;
        ForgeConfigSpec.IntValue textureCacheMaxSize;
        ForgeConfigSpec.IntValue maxSkinRequests;
        ForgeConfigSpec.IntValue fastCacheSize;

        // Skin preview
        ForgeConfigSpec.BooleanValue skinPreEnabled;
        ForgeConfigSpec.BooleanValue skinPreDrawBackground;
        ForgeConfigSpec.IntValue skinPreSize;
        ForgeConfigSpec.DoubleValue skinPreLocHorizontal;
        ForgeConfigSpec.DoubleValue skinPreLocVertical;
        ForgeConfigSpec.BooleanValue skinPreLocFollowMouse;

        // Tool-tip
        ForgeConfigSpec.BooleanValue tooltipHasSkin;
        ForgeConfigSpec.BooleanValue tooltipSkinName;
        ForgeConfigSpec.BooleanValue tooltipSkinAuthor;
        ForgeConfigSpec.BooleanValue tooltipSkinType;
        ForgeConfigSpec.BooleanValue tooltipDebug;
        ForgeConfigSpec.BooleanValue tooltipFlavour;
        ForgeConfigSpec.BooleanValue tooltipOpenWardrobe;

        // Debug
        ForgeConfigSpec.IntValue texturePaintingType;
        ForgeConfigSpec.BooleanValue showF3DebugInfo;

        ForgeConfigSpec.Builder builder;

        ClientConfig(ForgeConfigSpec.Builder builder) {
            this.builder = builder;

            // misc
            category("misc", "Miscellaneous settings.", () -> {
                skinLoadAnimationTime = defineInRange("skinLoadAnimationTime", 200, 0, 10000,
                        "How long skins will display their loading animation for in milliseconds",
                        "Settings this to 0 will disable loading animations.");
            });

            // performance
            category("performance", "Change (visual quality/performance) ratio by changing setting in this category.", () -> {
                renderDistanceSkin = defineInRange("renderDistanceSkin", 128, 16, 512,
                        "The max distance in blocks that skins will render.");

                renderDistanceBlockSkin = defineInRange("renderDistanceBlockSkin", 128, 16, 512,
                        "The max distance in blocks that block skins will be rendered.");

                renderDistanceMannequinEquipment = defineInRange("renderDistanceMannequinEquipment", 64, 16, 512,
                        "The max distance in blocks that equipment will be rendered on mannequins.");


                int cores = Runtime.getRuntime().availableProcessors();
                int bakingCores = MathHelper.ceil(cores / 2F);
                bakingCores = MathHelper.clamp(bakingCores, 1, 16);
                modelBakingThreadCount = defineInRange("modelBakingThreadCount", bakingCores, 1, 16,
                        "The maximum number of threads that will be used to bake models. "
                                + "[range: " + 1 + " ~ " + 16 + ", default: " + "core count / 2" + "]");

                multipassSkinRendering = define("multipassSkinRendering", true,
                        "When enabled skin will render in multiple passes to reduce visual artifacts.",
                        "Disabling this will improve skin rendering performance at the cost of visual quality.");

                lodDistance = defineInRange("lodDistance", 32.0, 8.0, 128.0,
                        "Distance away that skins will have lod applied to them.");

                maxLodLevels = defineInRange("maxLodLevels", 4, 0, 4,
                        "Number of LOD models to create. Higher number should give a boost to framerate at a small cost to VRAM.");

                useClassicBlockModels = define("useClassicBlockModels", false,
                        "Use classic block models instead of the 3D model versions.");
            });

            // cache
            category("cache", "Change (memory use/IO access) ratio by changing setting in this category.", () -> {
                // Skin cache
                skinCacheExpireTime = defineInRange("skinCacheExpireTime", 600, 0, 3600,
                        "How long in seconds the client will keep skins in it's cache.",
                        "Default 600 seconds is 10 minutes.",
                        "Setting to 0 turns off this option."); // setRequiresMcRestart

                skinCacheMaxSize = defineInRange("skinCacheMaxSize", 2000, 0, 10000,
                        "Max size the skin cache can reach before skins are removed.",
                        "Setting to 0 turns off this option."); // setRequiresMcRestart

                // Model cache
                modelPartCacheExpireTime = defineInRange("modelPartCacheExpireTime", 600, 0, 3600,
                        "How long in seconds the client will keep model parts in it's cache.",
                        "Default 600 seconds is 10 minutes.",
                        "Setting to 0 turns off this option."); // setRequiresMcRestart

                modelPartCacheMaxSize = defineInRange("modelPartCacheMaxSize", 2000, 0, 10000,
                        "Max size the cache can reach before model parts are removed.",
                        "Setting to 0 turns off this option."); // setRequiresMcRestart

                // Texture cache
                textureCacheExpireTime = defineInRange("textureCacheExpireTime", 600, 0, 3600,
                        "How long in seconds the client will keep textures in it's cache",
                        "Default 600 seconds is 10 minutes.",
                        "Setting to 0 turns off this option."); // setRequiresMcRestart

                textureCacheMaxSize = defineInRange("textureCacheMaxSize", 1000, 0, 5000,
                        "Max size the texture cache can reach before textures are removed.",
                        "Setting to 0 turns off this option."); // setRequiresMcRestart

                maxSkinRequests = defineInRange("maxSkinRequests", 10, 1, 50,
                        "Maximum number of skin the client can request at one time.");

                fastCacheSize = defineInRange("fastCacheSize", 5000, 0, Integer.MAX_VALUE,
                        "Size of client size cache.",
                        "Setting to 0 turns off this option.");
            });

            // Skin Preview
            category("skin-preview", "Setting to configure the skin preview box.", () -> {
                skinPreEnabled = define("skinPreEnabled", true,
                        "Enables a larger skin preview box when hovering the mouse over a skin.");

                skinPreDrawBackground = define("skinPreDrawBackground", true,
                        "Draw a background box for the skin preview.");

                skinPreSize = defineInRange("skinPreSize", 96, 16, 256,
                        "Size of the skin preview.");

                skinPreLocHorizontal = defineInRange("skinPreLocHorizontal", 0F, 0F, 1F,
                        "Horizontal location of the skin preview: 0 = left, 1 = right.");

                skinPreLocVertical = defineInRange("skinPreLocVertical", 0.5F, 0F, 1F,
                        "Vertical location of the skin preview: 0 = top, 1 = bottom.");

                skinPreLocFollowMouse = define("skinPreLocFollowMouse", true,
                        "Skin preview will be rendered next to the mouse.");
            });

            // Tooltip
            category("tooltip", "Setting to configure tooltips on skinned items.", () -> {
                tooltipHasSkin = define("tooltipHasSkin", true, "Show has skin tooltip on skinned items.");
                tooltipSkinName = define("tooltipSkinName", true, "Show skin name tooltip on items.");
                tooltipSkinAuthor = define("tooltipSkinAuthor", true, "Show skin author tooltip on items.");
                tooltipSkinType = define("tooltipSkinType", true, "Show skin type tooltip on items.");
                tooltipDebug = define("tooltipDebug", false, "Show skin debug info on items.");
                tooltipFlavour = define("tooltipFlavour", true, "Show skin flavoue text tooltip on items.");
                tooltipOpenWardrobe = define("tooltipOpenWardrobe", true, "Show open wardrobe message on skins.");
            });

            // Debug
            category("debug", "Debug Settings.", () -> {
                showF3DebugInfo = define("showF3DebugInfo", true, "Shows extra info on the F3 debug screen.");
                texturePaintingType = defineInRange("texturePaintingType", 0, -1, 2,
                        "Texture painting replacing the players texture with a painted version.",
                        "Turning this off may fix issues with the players texture rendering",
                        "incorrectly or showing the steve skin.",
                        "",
                        "-1 = disabled",
                        "0 = auto",
                        "1 = texture_replace (replaces the players texture - LEGACY)",
                        "2 = model_replace_mc (render using a mc model - slower, more compatible - NOT IMPLEMENTED)",
                        "3 = model_replace_aw (render using a aw model - faster, less compatible)");
            });
        }

        private ForgeConfigSpec.BooleanValue define(String path, boolean defaultValue, String... description) {
            return builder.comment(description).define(path, defaultValue);
        }

        private ForgeConfigSpec.IntValue defineInRange(String path, int defaultValue, int min, int max, String... description) {
            return builder.comment(description).defineInRange(path, defaultValue, min, max);
        }

        private ForgeConfigSpec.DoubleValue defineInRange(String path, double defaultValue, double min, double max, String... description) {
            return builder.comment(description).defineInRange(path, defaultValue, min, max);
        }

        private void category(String name, String description, Runnable runnable) {
            builder.comment(description);
            builder.push(name);
            runnable.run();
            builder.pop();
        }
    }
}


//    @SubscribeEvent
//    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
//        if (configEvent.getConfig().getSpec() == YourConfig.CLIENT_SPEC) {
//            bakeConfig();
//        }
//    }
