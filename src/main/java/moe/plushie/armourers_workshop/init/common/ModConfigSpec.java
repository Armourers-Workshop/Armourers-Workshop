package moe.plushie.armourers_workshop.init.common;

import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateContextPacket;
import moe.plushie.armourers_workshop.utils.slot.SkinSlotType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ModConfigSpec {

    public static Map<String, Object> SERVER;

    public static Pair<Client, ForgeConfigSpec> CLIENT;
    public static Pair<Common, ForgeConfigSpec> COMMON;

    static  {
        CLIENT = new ForgeConfigSpec.Builder().configure(builder -> new Client(() -> builder));
        COMMON = new ForgeConfigSpec.Builder().configure(builder -> new Common(() -> builder));
    }

    public static void reloadSpec(Map<String, Object> server) {
        SERVER = server;
        reload(COMMON.getRight());
    }

    public static void reload(ForgeConfigSpec spec) {
        if (CLIENT.getValue() == spec) {
            ModLog.debug("apply client config from spec");
            Client.Serializer.apply(CLIENT.getKey());
        }
        if (COMMON.getValue() == spec) {
            // the server config is activated we use it directly.
            if (SERVER != null) {
                ModLog.debug("apply common config from server");
                Common.Serializer.apply(SERVER);
                return;
            }
            ModLog.debug("apply common config from spec");
            Common.Serializer.apply(COMMON.getKey());
            if (FMLEnvironment.dist.isDedicatedServer() && ModContext.t0() != null) {
                // when the server config is changes, we need to synchronize it again
                NetworkHandler.getInstance().sendToAll(new UpdateContextPacket());
            }
        }
    }

    public interface SpecBuilder {

        ForgeConfigSpec.Builder getBuilder();

        default ForgeConfigSpec.BooleanValue define(String path, boolean defaultValue, String... description) {
            return getBuilder().comment(description).define(path, defaultValue);
        }

        default ForgeConfigSpec.IntValue defineInRange(String path, int defaultValue, int min, int max, String... description) {
            return getBuilder().comment(description).defineInRange(path, defaultValue, min, max);
        }

        default ForgeConfigSpec.DoubleValue defineInRange(String path, double defaultValue, double min, double max, String... description) {
            return getBuilder().comment(description).defineInRange(path, defaultValue, min, max);
        }

        default void defineCategory(String name, String description, Runnable runnable) {
            getBuilder().comment(description);
            getBuilder().push(name);
            runnable.run();
            getBuilder().pop();
        }
    }


    public static class Client {

        // Performance
        ForgeConfigSpec.IntValue renderDistanceSkin;
        ForgeConfigSpec.IntValue renderDistanceBlockSkin;
        ForgeConfigSpec.IntValue renderDistanceMannequinEquipment;
        ForgeConfigSpec.IntValue modelBakingThreadCount;
        ForgeConfigSpec.DoubleValue lodDistance;
        ForgeConfigSpec.BooleanValue multipassSkinRendering;
        ForgeConfigSpec.IntValue maxLodLevels;

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
        ForgeConfigSpec.BooleanValue enableEmbeddedSkinRenderer;

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

        Client(SpecBuilder builder) {
            builder.defineCategory("misc", "Miscellaneous settings.", () -> {
                skinLoadAnimationTime = builder.defineInRange("skinLoadAnimationTime", 200, 0, 10000,
                        "How long skins will display their loading animation for in milliseconds",
                        "Settings this to 0 will disable loading animations.");
            });
            builder.defineCategory("performance", "Change (visual quality/performance) ratio by changing setting in this category.", () -> {
                renderDistanceSkin = builder.defineInRange("renderDistanceSkin", 128, 16, 512,
                        "The max distance in blocks that skins will render.");

                renderDistanceBlockSkin = builder.defineInRange("renderDistanceBlockSkin", 128, 16, 512,
                        "The max distance in blocks that block skins will be rendered.");

                renderDistanceMannequinEquipment = builder.defineInRange("renderDistanceMannequinEquipment", 64, 16, 512,
                        "The max distance in blocks that equipment will be rendered on mannequins.");


                int cores = Runtime.getRuntime().availableProcessors();
                int bakingCores = MathHelper.ceil(cores / 2F);
                bakingCores = MathHelper.clamp(bakingCores, 1, 16);
                modelBakingThreadCount = builder.defineInRange("modelBakingThreadCount", bakingCores, 1, 16,
                        "The maximum number of threads that will be used to bake models. ",
                        "[range: " + 1 + " ~ " + 16 + ", default: " + "core count / 2" + "]");

                multipassSkinRendering = builder.define("multipassSkinRendering", true,
                        "When enabled skin will render in multiple passes to reduce visual artifacts.",
                        "Disabling this will improve skin rendering performance at the cost of visual quality.");

                lodDistance = builder.defineInRange("lodDistance", 32.0, 8.0, 128.0,
                        "Distance away that skins will have lod applied to them.");

                maxLodLevels = builder.defineInRange("maxLodLevels", 4, 0, 4,
                        "Number of LOD models to create. Higher number should give a boost to framerate at a small cost to VRAM.");
            });
            builder.defineCategory("cache", "Change (memory use/IO access) ratio by changing setting in this category.", () -> {
                // Skin cache
                skinCacheExpireTime = builder.defineInRange("skinCacheExpireTime", 600, 0, 3600,
                        "How long in seconds the client will keep skins in it's cache.",
                        "Default 600 seconds is 10 minutes.",
                        "Setting to 0 turns off this option."); // setRequiresMcRestart

                skinCacheMaxSize = builder.defineInRange("skinCacheMaxSize", 2000, 0, 10000,
                        "Max size the skin cache can reach before skins are removed.",
                        "Setting to 0 turns off this option."); // setRequiresMcRestart

                // Model cache
                modelPartCacheExpireTime = builder.defineInRange("modelPartCacheExpireTime", 600, 0, 3600,
                        "How long in seconds the client will keep model parts in it's cache.",
                        "Default 600 seconds is 10 minutes.",
                        "Setting to 0 turns off this option."); // setRequiresMcRestart

                modelPartCacheMaxSize = builder.defineInRange("modelPartCacheMaxSize", 2000, 0, 10000,
                        "Max size the cache can reach before model parts are removed.",
                        "Setting to 0 turns off this option."); // setRequiresMcRestart

                // Texture cache
                textureCacheExpireTime = builder.defineInRange("textureCacheExpireTime", 600, 0, 3600,
                        "How long in seconds the client will keep textures in it's cache",
                        "Default 600 seconds is 10 minutes.",
                        "Setting to 0 turns off this option."); // setRequiresMcRestart

                textureCacheMaxSize = builder.defineInRange("textureCacheMaxSize", 1000, 0, 5000,
                        "Max size the texture cache can reach before textures are removed.",
                        "Setting to 0 turns off this option."); // setRequiresMcRestart

                maxSkinRequests = builder.defineInRange("maxSkinRequests", 10, 1, 50,
                        "Maximum number of skin the client can request at one time.");

                fastCacheSize = builder.defineInRange("fastCacheSize", 5000, 0, Integer.MAX_VALUE,
                        "Size of client size cache.",
                        "Setting to 0 turns off this option.");
            });
            builder.defineCategory("skin-preview", "Setting to configure the skin preview box.", () -> {
                skinPreEnabled = builder.define("skinPreEnabled", true,
                        "Enables a larger skin preview box when hovering the mouse over a skin.");

                skinPreDrawBackground = builder.define("skinPreDrawBackground", true,
                        "Draw a background box for the skin preview.");

                skinPreSize = builder.defineInRange("skinPreSize", 96, 16, 256,
                        "Size of the skin preview.");

                skinPreLocHorizontal = builder.defineInRange("skinPreLocHorizontal", 0F, 0F, 1F,
                        "Horizontal location of the skin preview: 0 = left, 1 = right.");

                skinPreLocVertical = builder.defineInRange("skinPreLocVertical", 0.5F, 0F, 1F,
                        "Vertical location of the skin preview: 0 = top, 1 = bottom.");

                skinPreLocFollowMouse = builder.define("skinPreLocFollowMouse", true,
                        "Skin preview will be rendered next to the mouse.");

                enableEmbeddedSkinRenderer = builder.define("enableEmbeddedSkinRenderer", false,
                        "Using embedded skin renderer to replace the original item renderer.");
            });
            builder.defineCategory("tooltip", "Setting to configure tooltips on skinned items.", () -> {
                tooltipHasSkin = builder.define("tooltipHasSkin", true, "Show has skin tooltip on skinned items.");
                tooltipSkinName = builder.define("tooltipSkinName", true, "Show skin name tooltip on items.");
                tooltipSkinAuthor = builder.define("tooltipSkinAuthor", true, "Show skin author tooltip on items.");
                tooltipSkinType = builder.define("tooltipSkinType", true, "Show skin type tooltip on items.");
                tooltipDebug = builder.define("tooltipDebug", false, "Show skin debug info on items.");
                tooltipFlavour = builder.define("tooltipFlavour", true, "Show skin flavoue text tooltip on items.");
                tooltipOpenWardrobe = builder.define("tooltipOpenWardrobe", true, "Show open wardrobe message on skins.");
            });
            builder.defineCategory("debug", "Debug Settings.", () -> {
                showF3DebugInfo = builder.define("showF3DebugInfo", true, "Shows extra info on the F3 debug screen.");
                texturePaintingType = builder.defineInRange("texturePaintingType", 0, -1, 2,
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

        public static class Serializer extends ModConfig.Client {

            public static void apply(Client spec) {
                skinLoadAnimationTime = spec.skinLoadAnimationTime.get();

                renderDistanceSkin = spec.renderDistanceSkin.get();
                renderDistanceBlockSkin = spec.renderDistanceBlockSkin.get() * spec.renderDistanceBlockSkin.get();
                renderDistanceMannequinEquipment = spec.renderDistanceMannequinEquipment.get();

                modelBakingThreadCount = spec.modelBakingThreadCount.get();
                multipassSkinRendering = spec.multipassSkinRendering.get();
                lodDistance = spec.lodDistance.get();
                maxLodLevels = spec.maxLodLevels.get();

                skinCacheExpireTime = spec.skinCacheExpireTime.get();
                skinCacheMaxSize = spec.skinCacheMaxSize.get();
                modelPartCacheExpireTime = spec.modelPartCacheExpireTime.get();
                modelPartCacheMaxSize = spec.modelPartCacheMaxSize.get();
                textureCacheExpireTime = spec.textureCacheExpireTime.get();
                textureCacheMaxSize = spec.textureCacheMaxSize.get();
                maxSkinRequests = spec.maxSkinRequests.get();
                fastCacheSize = spec.fastCacheSize.get();

                skinPreEnabled = spec.skinPreEnabled.get();
                skinPreDrawBackground = spec.skinPreDrawBackground.get();
                skinPreSize = spec.skinPreSize.get();
                skinPreLocHorizontal = spec.skinPreLocHorizontal.get();
                skinPreLocVertical = spec.skinPreLocVertical.get();
                skinPreLocFollowMouse = spec.skinPreLocFollowMouse.get();
                enableEmbeddedSkinRenderer = spec.enableEmbeddedSkinRenderer.get();

                tooltipHasSkin = spec.tooltipHasSkin.get();
                tooltipSkinName = spec.tooltipSkinName.get();
                tooltipSkinAuthor = spec.tooltipSkinAuthor.get();
                tooltipSkinType = spec.tooltipSkinType.get();
                debugTooltip = spec.tooltipDebug.get();
                tooltipFlavour = spec.tooltipFlavour.get();
                tooltipOpenWardrobe = spec.tooltipOpenWardrobe.get();

                showF3DebugInfo = spec.showF3DebugInfo.get();
                texturePaintingType = spec.texturePaintingType.get();
            }

            public static void write(Client spec) {

            }
        }
    }

    public static class Common {

        // General
        ForgeConfigSpec.IntValue maxUndos;
        ForgeConfigSpec.BooleanValue lockDyesOnSkins;
        ForgeConfigSpec.BooleanValue instancedDyeTable;
        ForgeConfigSpec.IntValue serverSkinSendRate;
        ForgeConfigSpec.BooleanValue serverCompressesSkins;

        // Wardrobe
        ForgeConfigSpec.BooleanValue wardrobeAllowOpening;
        ForgeConfigSpec.BooleanValue showWardrobeSkins;
        ForgeConfigSpec.BooleanValue showWardrobeOutfits;
        ForgeConfigSpec.BooleanValue showWardrobeDisplaySettings;
        ForgeConfigSpec.BooleanValue showWardrobeColourSettings;
        ForgeConfigSpec.BooleanValue showWardrobeDyeSetting;
        ForgeConfigSpec.IntValue prefersWardrobePlayerSlots;
        ForgeConfigSpec.IntValue prefersWardrobeMobSlots;
        ForgeConfigSpec.IntValue prefersWardrobeDropOnDeath;

        // Library
        ForgeConfigSpec.BooleanValue extractOfficialSkins;
        ForgeConfigSpec.BooleanValue allowLibraryPreviews;
        ForgeConfigSpec.BooleanValue allowDownloadingSkins;
        ForgeConfigSpec.BooleanValue allowUploadingSkins;
        ForgeConfigSpec.BooleanValue allowLibraryRemoteManage;

        // Recipes
        ForgeConfigSpec.BooleanValue disableRecipes;
        ForgeConfigSpec.BooleanValue disableDollRecipe;
        ForgeConfigSpec.BooleanValue disableSkinningRecipes;
        ForgeConfigSpec.BooleanValue hideDollFromCreativeTabs;
        ForgeConfigSpec.BooleanValue hideGiantFromCreativeTabs;
        ForgeConfigSpec.BooleanValue enableRecoveringSkins;

        // Holiday events
        ForgeConfigSpec.BooleanValue disableAllHolidayEvents;

        // Entity skins
        ForgeConfigSpec.IntValue enitiySpawnWithSkinsChance;
        ForgeConfigSpec.IntValue entityDropSkinChance;

        // Cache
        ForgeConfigSpec.IntValue skinCacheExpireTime;
        ForgeConfigSpec.IntValue skinCacheMaxSize;

        Common(SpecBuilder builder) {
            builder.defineCategory("general", "General settings.", () -> {
                maxUndos = builder.defineInRange("maxUndos", 100, 0, 1000,
                        "Max number of undos a player has for block painting.");

                lockDyesOnSkins = builder.define("lockDyesOnSkins", false,
                        "When enabled players will not be able to remove dyes from skins in the dye table.");

                instancedDyeTable = builder.define("instancedDyeTable", false,
                        "If true the dye table will be instanced for each player. Items will be dropped when the table is closed.");

                serverSkinSendRate = builder.defineInRange("serverModelSendRate", 4000, 0, 8000,
                        "The maximum number of skins the server is allow to send every minute.",
                        "Less that 1 equals unlimited. (not recommended may cause bandwidth and cpu spikes on the server)");

                serverCompressesSkins = builder.define("serverCompressesSkins", true,
                        "If enabled the server will compress skins before sending them to clients.",
                        "Highly recommended unless the server has a very slow CPU.");

//                if (!LibModInfo.MOD_VERSION.startsWith("@VER")) {
//                    lastVersion = config.getString("lastVersion", CATEGORY_GENERAL, "0.0",
//                            "Used by the mod to check if it has been updated.");
//                }
            });
            builder.defineCategory("wardrobe", "Setting for the players wardrobe.", () -> {
                wardrobeAllowOpening = builder.define("allowOpening", true,
                        "Allow the player to open the wardrobe GUI.");

                showWardrobeSkins = builder.define("enableSkinTab", true,
                        "Enable the wardrobe skins tab.");

                showWardrobeOutfits = builder.define("enableOutfitTab", true,
                        "Enable the wardrobe outfits tab.");

                showWardrobeDisplaySettings = builder.define("enableDisplayTab", true,
                        "Enable the wardrobe display settings tab.");

                showWardrobeColourSettings = builder.define("enableColourTab", true,
                        "Enable the wardrobe colour settings tab.");

                showWardrobeDyeSetting = builder.define("enableDyeTab", true,
                        "Enable the wardrobe dyes tab.");

                prefersWardrobeMobSlots = builder.defineInRange("mobStartingSlots", 3, 1, SkinSlotType.getMaxSlotSize(),
                        "Number of slot columns the mob starts with for skins.");

                prefersWardrobePlayerSlots = builder.defineInRange("playerStartingSlots", 3, 1, SkinSlotType.getMaxSlotSize(),
                        "Number of slot columns the player starts with for skins.");

                prefersWardrobeDropOnDeath = builder.defineInRange("playerDropSkinsOnDeath", 0, 0, 2,
                        "Should skins be dropped on player death.",
                        "0 = use keep inventory rule",
                        "1 = never drop",
                        "2 = always drop");
            });
            builder.defineCategory("library", "Setting for the library blocks.", () -> {
                allowDownloadingSkins = builder.define("allowDownloadingSkins", false,
                        "Allows clients to save skins from a server to their local computer using the library.");

                allowUploadingSkins = builder.define("allowUploadingSkins", true,
                        "Allows clients to load skins from their local computer onto the server using the library.");

                extractOfficialSkins = builder.define("extractOfficialSkins", true,
                        "Allow the mod to extract the official skins that come with the mod into the library folder.");

                allowLibraryPreviews = builder.define("allowPreviewSkins", true,
                        "Shows model previews in the library.",
                        "Causes a lot of extra load on servers.",
                        "Best to turn off on high population servers");

                allowLibraryRemoteManage = builder.define("allowManageSkins", false,
                        "Allows clients to manage skins of the server computer library.",
                        "Required permission level 5 or higher.");
            });
            builder.defineCategory("recipe", "Setting for mod recipes.", () -> {
                disableRecipes = builder.define("disableRecipes", false,
                        "Disable vanilla recipes. Use if you want to manually add recipes for a mod pack.");

                disableDollRecipe = builder.define("disableDollRecipe", false,
                        "Disable hidden in world doll recipe.");

                disableSkinningRecipes = builder.define("disableSkinningRecipes", false,
                        "Disable skinning table recipes.");

                hideDollFromCreativeTabs = builder.define("hideDollFromCreativeTabs", true,
                        "Hides the doll block from the creative tab and NEI.");

                hideGiantFromCreativeTabs = builder.define("hideGiantFromCreativeTabs", true,
                        "Hides the giant block from the creative tab and NEI.");

                enableRecoveringSkins = builder.define("enableRecoveringSkins", false,
                        "Enable copying the skin off an item in the skinning table");
            });

            builder.defineCategory("holiday-events", "Enable/disable holiday events.", () -> {

                disableAllHolidayEvents = builder.define("disableAllHolidayEvents", false,
                        "Setting to true will disable all holiday events. What's wrong with you!");

//                SimpleDateFormat sdf = new SimpleDateFormat("MM:dd:HH", Locale.ENGLISH);
//                for (Holiday holiday : ModHolidays.getHolidays()) {
//                    boolean holidayEnabled = builder.define("holiday-" + holiday.getName() + "-enabled", true,
//                            "Enable holiday.");
//
//                    Calendar startDate = holiday.getStartDate();
//                    Calendar endDate = holiday.getEndDate();
//
//                    String dates = config.getString("holiday-" + holiday.getName() + "-range",
//                            sdf.format(startDate.getTime()) + "-" + sdf.format(endDate.getTime()),
//                            "Holiday date range. Format (Start Date-End Date) (MONTH:DAY:HOUR-MONTH:DAY:HOUR)");
//
//                    String startDateStr = sdf.format(startDate.getTime());
//                    String endDateStr = sdf.format(endDate.getTime());
//
//                    if (dates.contains("-")) {
//                        String[] split = dates.split("-");
//                        startDateStr = split[0];
//                        endDateStr = split[1];
//                    }
//
//                    try {
//                        Date date = sdf.parse(startDateStr);
//                        startDate.setTime(date);
//                        startDate.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        Date date = sdf.parse(endDateStr);
//                        endDate.setTime(date);
//                        endDate.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//
//                    holiday.setEnabled(holidayEnabled);
//                    holiday.setStartDate(startDate);
//                    holiday.setEndDate(endDate);
//                }
            });

            builder.defineCategory("entity-skins", "Control how/if entities spawm with skin on them.", () -> {

                enitiySpawnWithSkinsChance = builder.defineInRange("enitiySpawnWithSkinsChance", 0, 0, 100,
                        "Percentage change that entities will spawn with skins equipped.");

                entityDropSkinChance = builder.defineInRange("entityDropSkinChance", 0, 0, 100,
                        "Percentage change that entities will drop equipped skins when killed.");

//            enitiySpawnSkinTargetPath = "/" + config.getString("enitiySpawnSkinTargetPath", "",
//                    "Target library path for skin spawned on entities.\n"
//                            + "Examples: 'official/' for only skins in the official folder or 'downloads/' for skins in the downloads folder.\n"
//                            + "Leave black for all skins.");
            });

            builder.defineCategory("cache", "Change (memory use/IO access) ratio by category setting in this category.", () -> {
                skinCacheExpireTime = builder.defineInRange("expireTime", 6000, 0, 36000,
                        "How long in seconds the server will keep skins in it's cache.",
                        "Default 600 seconds is 10 minutes.",
                        "Setting to 0 turns off this option.");

                skinCacheMaxSize = builder.defineInRange("maxSize", 2000, 0, 10000,
                        "Max size the skin cache can reach before skins are removed.",
                        "Setting to 0 turns off this option.");
            });

//            config.setCategoryComment(CATEGORY_COMPATIBILITY, "Allows auto item skinning for supported mod to be enable/disable.");
//            for (ModAddon modAddon : ModAddonManager.getLoadedAddons()) {
//                if (modAddon.hasItemOverrides()) {
//                    boolean itemSkinningSupport = config.getBoolean(
//                            String.format("enable-%s-compat", modAddon.getModId()),
//                            CATEGORY_COMPATIBILITY, true,
//                            String.format("Enable auto item support for %s.", modAddon.getModName()));
//                    modAddon.setItemSkinningSupport(itemSkinningSupport);
//                }
//            }


//            config.setCategoryComment(CATEGORY_OVERRIDES,
//                    "Custom list of items that can be skinned.\n"
//                            + "Format [override type:mod id:item name]\n"
//                            + "Valid override types are: sword, shield, bow, pickaxe, axe, shovel, hoe and item\n"
//                            + "example sword:minecraft:iron_sword");
//            if (propOverrides == null) {
//                propOverrides = config.get(CATEGORY_OVERRIDES, "itemOverrides", new String[] {});
//                propOverrides.setLanguageKey("itemOverrides");
//                overrides.clear();
//                for (String override : propOverrides.getStringList()) {
//                    overrides.add(override);
//                }
//            }
        }

//        private static void checkIfUpdated() {
//            String localVersion = LibModInfo.MOD_VERSION;
//            if (LibModInfo.MOD_VERSION.startsWith("@VER")) {
//                return;
//            }
//            if (versionCompare(lastVersion.replaceAll("-", "."), localVersion.replaceAll("-", ".")) < 0) {
//                ModLogger.log(String.format("Updated from version %s to version %s.", lastVersion, localVersion));
//                config.getCategory(CATEGORY_GENERAL).get("lastVersion").set(localVersion);
//                if (config.hasChanged()) {
//                    config.save();
//                }
//                hasUpdated = true;
//            } else {
//                hasUpdated = false;
//            }
//        }
//
//        private static int versionCompare(String str1, String str2) {
//            String[] vals1 = str1.split("\\.");
//            String[] vals2 = str2.split("\\.");
//            int i = 0;
//            // set index to first non-equal ordinal or length of shortest version
//            // string
//            while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
//                i++;
//            }
//            // compare first non-equal ordinal number
//            if (i < vals1.length && i < vals2.length) {
//                int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
//                return Integer.signum(diff);
//            }
//            // the strings are equal or one string is a substring of the other
//            // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
//            else {
//                return Integer.signum(vals1.length - vals2.length);
//            }
//        }

        public static class Serializer extends ModConfig.Common {

            public static Map<String, Object> snapshot() {
                HashMap<String, Object> fields = new HashMap<>();
                try {
                    Class<?> object = ModConfig.Common.class;
                    for (Field field : object.getDeclaredFields()) {
                        if (field.getModifiers() == (Modifier.PUBLIC | Modifier.STATIC)) {
                            fields.put(field.getName(), field.get(object));
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return fields;
            }

            public static void apply(Map<String, Object> fields) {
                try {
                    Class<?> object = ModConfig.Common.class;
                    for (Field field : object.getDeclaredFields()) {
                        Object value = fields.get(field.getName());
                        if (value != null && field.getModifiers() == (Modifier.PUBLIC | Modifier.STATIC)) {
                            field.set(object, value);
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            public static void apply(Common spec) {
                maxUndos = spec.maxUndos.get();
                lockDyesOnSkins = spec.lockDyesOnSkins.get();
                instancedDyeTable = spec.instancedDyeTable.get();
                serverSkinSendRate = spec.serverSkinSendRate.get();
                serverCompressesSkins = spec.serverCompressesSkins.get();

                wardrobeAllowOpening = spec.wardrobeAllowOpening.get();
                showWardrobeSkins = spec.showWardrobeSkins.get();
                showWardrobeOutfits = spec.showWardrobeOutfits.get();
                showWardrobeDisplaySettings = spec.showWardrobeDisplaySettings.get();
                showWardrobeColourSettings = spec.showWardrobeColourSettings.get();
                showWardrobeDyeSetting = spec.showWardrobeDyeSetting.get();
                prefersWardrobePlayerSlots = spec.prefersWardrobePlayerSlots.get();
                prefersWardrobeMobSlots = spec.prefersWardrobeMobSlots.get();
                prefersWardrobeDropOnDeath = spec.prefersWardrobeDropOnDeath.get();

                extractOfficialSkins = spec.extractOfficialSkins.get();
                allowDownloadingSkins = spec.allowDownloadingSkins.get();
                allowUploadingSkins = spec.allowUploadingSkins.get();
                allowLibraryPreviews = spec.allowLibraryPreviews.get();
                allowLibraryRemoteManage = spec.allowLibraryRemoteManage.get();

                disableRecipes = spec.disableRecipes.get();
                disableDollRecipe = spec.disableDollRecipe.get();
                disableSkinningRecipes = spec.disableSkinningRecipes.get();
                hideDollFromCreativeTabs = spec.hideDollFromCreativeTabs.get();
                hideGiantFromCreativeTabs = spec.hideGiantFromCreativeTabs.get();
                enableRecoveringSkins = spec.enableRecoveringSkins.get();

                disableAllHolidayEvents = spec.disableAllHolidayEvents.get();

                enitiySpawnWithSkinsChance = spec.enitiySpawnWithSkinsChance.get();
                entityDropSkinChance = spec.entityDropSkinChance.get();
                enitiySpawnSkinTargetPath = "/"; //spec.enitiySpawnSkinTargetPath.get();

                skinCacheExpireTime = spec.skinCacheExpireTime.get();
                skinCacheMaxSize = spec.skinCacheMaxSize.get();

//                remotePlayerId = spec.skinCacheMaxSize.get();
//                lastVersion = spec.skinCacheMaxSize.get();
//                hasUpdated = spec.hasUpdated.get();
            }

            public static void write(Client spec) {

            }
        }
    }
}


//    @SubscribeEvent
//    public static void onModConfigEvent(final ModConfig.Client.ModConfigEvent configEvent) {
//        if (configEvent.getConfig().getSpec() == YourConfig.CLIENT_SPEC) {
//            bakeConfig();
//        }
//    }
