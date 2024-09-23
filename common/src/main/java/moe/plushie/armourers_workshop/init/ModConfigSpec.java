package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.config.IConfigBuilder;
import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.network.UpdateContextPacket;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.MathUtils;

import java.util.ArrayList;

public class ModConfigSpec {

    public static final IConfigSpec CLIENT = EnvironmentManager.getClientConfigSpec();
    public static final IConfigSpec COMMON = EnvironmentManager.getCommonConfigSpec();

    public abstract static class Client extends ModConfig.Client implements IConfigBuilder {

        public Client() {
            defineCategory("general", "General settings.", () -> {
                defineInRange("enableEmbeddedSkinRenderer", 0, 0, 2, "Using embedded skin renderer to replace the original item renderer.", "0 = auto", "1 = disable", "2 = enable").bind(v -> enableEmbeddedSkinRenderer = v, () -> enableEmbeddedSkinRenderer);
                defineInRange("enableFirstPersonSkinRenderer", 0, 0, 2, "Using skin renderer to replace the original first person hands renderer.", "0 = auto", "1 = always disable", "2 = always enable").bind(v -> enableFirstPersonSkinRenderer = v, () -> enableFirstPersonSkinRenderer);
            });
            defineCategory("misc", "Miscellaneous settings.", () -> {
                defineInRange("skinLoadAnimationTime", 200, 0, 10000, "How long skins will display their loading animation for in milliseconds", "Settings this to 0 will disable loading animations.").bind(v -> skinLoadAnimationTime = v, () -> skinLoadAnimationTime);
                define("enableSkinLimitLimbs", true, "Whether allows skin to limit limbs of entities.").bind(v -> enableSkinLimitLimbs = v, () -> enableSkinLimitLimbs);
                define("enableClipInInventory", true, "Whether clip the excess skin in the inventory gui.").bind(v -> enableEntityInInventoryClip = v, () -> enableEntityInInventoryClip);
            });
            defineCategory("performance", "Change (visual quality/performance) ratio by changing setting in this category.", () -> {
                defineInRange("renderDistanceSkin", 128, 16, 512, "The max distance in blocks that skins will render.").bind(v -> renderDistanceSkin = v, () -> renderDistanceSkin);
                defineInRange("renderDistanceBlockSkin", 128, 16, 512, "The max distance in blocks that block skins will be rendered.").bind(v -> renderDistanceBlockSkin = v, () -> renderDistanceBlockSkin);
                defineInRange("renderDistanceMannequinEquipment", 64, 16, 512, "The max distance in blocks that equipment will be rendered on mannequins.").bind(v -> renderDistanceMannequinEquipment = v, () -> renderDistanceMannequinEquipment);
                defineInRange("modelBakingThreadCount", getBakingCores(), 1, 16, "The maximum number of threads that will be used to bake models. ", "[range: " + 1 + " ~ " + 16 + ", default: " + "core count / 2" + "]").bind(v -> modelBakingThreadCount = v, () -> modelBakingThreadCount);
                define("multipassSkinRendering", true, "When enabled skin will render in multiple passes to reduce visual artifacts.", "Disabling this will improve skin rendering performance at the cost of visual quality.").bind(v -> multipassSkinRendering = v, () -> multipassSkinRendering);
                defineInRange("lodDistance", 32.0, 8.0, 128.0, "Distance away that skins will have lod applied to them.").bind(v -> lodDistance = v, () -> lodDistance);
                defineInRange("maxLodLevels", 4, 0, 4, "Number of LOD models to create. Higher number should give a boost to framerate at a small cost to VRAM.").bind(v -> maxLodLevels = v, () -> maxLodLevels);
            });
            defineCategory("cache", "Change (memory use/IO access) ratio by changing setting in this category.", () -> {
                // Skin cache
                defineInRange("skinCacheExpireTime", 600, 0, 3600, "How long in seconds the client will keep skins in it's cache.", "Default 600 seconds is 10 minutes.", "Setting to 0 turns off this option.").bind(v -> skinCacheExpireTime = v, () -> skinCacheExpireTime); // setRequiresMcRestart
                defineInRange("skinCacheMaxSize", 2000, 0, 10000, "Max size the skin cache can reach before skins are removed.", "Setting to 0 turns off this option.").bind(v -> skinCacheMaxSize = v, () -> skinCacheMaxSize); // setRequiresMcRestart

                // Model cache
                defineInRange("modelPartCacheExpireTime", 600, 0, 3600, "How long in seconds the client will keep model parts in it's cache.", "Default 600 seconds is 10 minutes.", "Setting to 0 turns off this option.").bind(v -> modelPartCacheExpireTime = v, () -> modelPartCacheExpireTime); // setRequiresMcRestart
                defineInRange("modelPartCacheMaxSize", 2000, 0, 10000, "Max size the cache can reach before model parts are removed.", "Setting to 0 turns off this option.").bind(v -> modelPartCacheMaxSize = v, () -> modelPartCacheMaxSize); // setRequiresMcRestart

                // Texture cache
                defineInRange("textureCacheExpireTime", 600, 0, 3600, "How long in seconds the client will keep textures in it's cache", "Default 600 seconds is 10 minutes.", "Setting to 0 turns off this option.").bind(v -> textureCacheExpireTime = v, () -> textureCacheExpireTime); // setRequiresMcRestart
                defineInRange("textureCacheMaxSize", 1000, 0, 5000, "Max size the texture cache can reach before textures are removed.", "Setting to 0 turns off this option.").bind(v -> textureCacheMaxSize = v, () -> textureCacheMaxSize); // setRequiresMcRestart
                defineInRange("maxSkinRequests", 10, 1, 50, "Maximum number of skin the client can request at one time.").bind(v -> maxSkinRequests = v, () -> maxSkinRequests);
                defineInRange("fastCacheSize", 5000, 0, Integer.MAX_VALUE, "Size of client size cache.", "Setting to 0 turns off this option.").bind(v -> fastCacheSize = v, () -> fastCacheSize);
            });
            defineCategory("skin-preview", "Setting to configure the skin preview box.", () -> {
                define("skinPreEnabled", true, "Enables a larger skin preview box when hovering the mouse over a skin.").bind(n -> skinPreEnabled = n, () -> skinPreEnabled);
                define("skinPreDrawBackground", true, "Draw a background box for the skin preview.").bind(v -> skinPreDrawBackground = v, () -> skinPreDrawBackground);
                defineInRange("skinPreSize", 96, 16, 256, "Size of the skin preview.").bind(v -> skinPreSize = v, () -> skinPreSize);
                defineInRange("skinPreLocHorizontal", 0F, 0F, 1F, "Horizontal location of the skin preview: 0 = left, 1 = right.").bind(v -> skinPreLocHorizontal = v, () -> skinPreLocHorizontal);
                defineInRange("skinPreLocVertical", 0.5F, 0F, 1F, "Vertical location of the skin preview: 0 = top, 1 = bottom.").bind(v -> skinPreLocVertical = v, () -> skinPreLocVertical);
                define("skinPreLocFollowMouse", true, "Skin preview will be rendered next to the mouse.").bind(v -> skinPreLocFollowMouse = v, () -> skinPreLocFollowMouse);
            });
            defineCategory("debug", "Debug Settings.", () -> {
                define("shader", false, "Shows shader mixin results in logs.").bind(v -> enableShaderDebug = v, () -> enableShaderDebug);
                define("animation", false, "Shows animation running states in logs.").bind(v -> enableAnimationDebug = v, () -> enableAnimationDebug);
                define("molang", false, "Shows molang compile states in logs.").bind(v -> enableMolangDebug = v, () -> enableMolangDebug);
                define("showF3DebugInfo", true, "Shows extra info on the F3 debug screen.").bind(v -> showF3DebugInfo = v, () -> showF3DebugInfo);
                defineInRange("texturePaintingType", 0, -1, 2, "Texture painting replacing the players texture with a painted version.", "Turning this off may fix issues with the players texture rendering", "incorrectly or showing the steve skin.", "", "-1 = disabled", "0 = auto", "1 = texture_replace (replaces the players texture - LEGACY)", "2 = model_replace_mc (render using a mc model - slower, more compatible - NOT IMPLEMENTED)", "3 = model_replace_aw (render using a aw model - faster, less compatible)").bind(n -> texturePaintingType = n, () -> texturePaintingType);
            });
        }

        private int getBakingCores() {
            int cores = Runtime.getRuntime().availableProcessors();
            int bakingCores = MathUtils.ceil(cores / 2F);
            return MathUtils.clamp(bakingCores, 1, 16);
        }
    }

    public abstract static class Common extends ModConfig.Common implements IConfigBuilder {

        public Common() {
            defineCategory("general", "General settings.", () -> {
                defineInRange("maxUndos", 100, 0, 1000, "Max number of undos a player has for block painting.").bind(v -> maxUndos = v, () -> maxUndos);
                defineInRange("blockTaskRate", 10, 1, 1000, "Max number of processing blocks in per tick.").bind(v -> blockTaskRate = v, () -> blockTaskRate);
                define("lockDyesOnSkins", false, "When enabled players will not be able to remove dyes from skins in the dye table.").bind(v -> lockDyesOnSkins = v, () -> lockDyesOnSkins);
                define("instancedDyeTable", false, "If true the dye table will be instanced for each player. Items will be dropped when the table is closed.").bind(v -> instancedDyeTable = v, () -> instancedDyeTable);
                define("enableProtocolCheck", true, "If enabled the server will check the client protocol version in the login.", "Highly recommended unless the server does not support handshake.").bind(v -> enableProtocolCheck = v, () -> enableProtocolCheck);
                define("enablePermissionCheck", true, "If enabled the server will check permission node in the each operation.", "Highly recommended in the forge server.").bind(v -> enablePermissionCheck = v, () -> enablePermissionCheck);
                defineInRange("serverModelSendRate", 4000, 0, 8000, "The maximum number of skins the server is allow to send every minute.", "Less that 1 equals unlimited. (not recommended may cause bandwidth and cpu spikes on the server)").bind(n -> serverSkinSendRate = n, () -> serverSkinSendRate);
                define("serverCompressesSkins", true, "If enabled the server will compress skins before sending them to clients.", "Highly recommended unless the server has a very slow CPU.").bind(v -> enableServerCompressesSkins = v, () -> enableServerCompressesSkins);
                defineInRange("enableEmbeddedSkinRenderer", 0, 0, 2, "Using embedded skin renderer to replace the original item renderer.", "0 = use client config", "1 = always disable", "2 = always enable").bind(v -> enableEmbeddedSkinRenderer = v, () -> enableEmbeddedSkinRenderer);
                defineInRange("enableFirstPersonSkinRenderer", 0, 0, 2, "Using skin renderer to replace the original first person hands renderer.", "0 = use client config", "1 = always disable", "2 = always enable").bind(v -> enableFirstPersonSkinRenderer = v, () -> enableFirstPersonSkinRenderer);
            });
            defineCategory("tooltip", "Setting to configure tooltips on skinned items.", () -> {
                define("tooltipHasSkin", true, "Show has skin tooltip on skinned items.").bind(v -> tooltipHasSkin = v, () -> tooltipHasSkin);
                define("tooltipSkinName", true, "Show skin name tooltip on items.").bind(v -> tooltipSkinName = v, () -> tooltipSkinName);
                define("tooltipSkinAuthor", true, "Show skin author tooltip on items.").bind(v -> tooltipSkinAuthor = v, () -> tooltipSkinAuthor);
                define("tooltipSkinType", true, "Show skin type tooltip on items.").bind(v -> tooltipSkinType = v, () -> tooltipSkinType);
                define("tooltipFlavour", true, "Show skin flavour text tooltip on items.").bind(v -> tooltipFlavour = v, () -> tooltipFlavour);
                define("tooltipSkinPreview", true, "Show skin preview tooltip on items.").bind(v -> tooltipSkinPreview = v, () -> tooltipSkinPreview);
                define("tooltipOpenWardrobe", true, "Show open wardrobe message on skins.").bind(v -> tooltipOpenWardrobe = v, () -> tooltipOpenWardrobe);
            });
            defineCategory("wardrobe", "Setting for the players wardrobe.", () -> {
                define("allowOpening", true, "Allow the player to open the wardrobe GUI.").bind(v -> wardrobeAllowOpening = v, () -> wardrobeAllowOpening);
                define("enableSkinTab", true, "Enable the wardrobe skins tab.").bind(v -> showWardrobeSkins = v, () -> showWardrobeSkins);
                define("enableOutfitTab", true, "Enable the wardrobe outfits tab.").bind(v -> showWardrobeOutfits = v, () -> showWardrobeOutfits);
                define("enableDisplayTab", true, "Enable the wardrobe display settings tab.").bind(v -> showWardrobeDisplaySettings = v, () -> showWardrobeDisplaySettings);
                define("enableColourTab", true, "Enable the wardrobe colour settings tab.").bind(v -> showWardrobeColorSettings = v, () -> showWardrobeColorSettings);
                define("enableDyeTab", true, "Enable the wardrobe dyes tab.").bind(v -> showWardrobeDyeSetting = v, () -> showWardrobeDyeSetting);
                define("onlySkinIntoSlots", false, "Only allows the player place to skin item into slots.").bind(v -> onlySkinIntoSlots = v, () -> onlySkinIntoSlots);
                defineInRange("mobStartingSlots", 3, 1, SkinSlotType.getMaxSlotSize(), "Number of slot columns the mob starts with for skins.").bind(v -> prefersWardrobeMobSlots = v, () -> prefersWardrobeMobSlots);
                defineInRange("playerStartingSlots", 3, 1, SkinSlotType.getMaxSlotSize(), "Number of slot columns the player starts with for skins.").bind(v -> prefersWardrobePlayerSlots = v, () -> prefersWardrobePlayerSlots);
                defineInRange("playerDropSkinsOnDeath", 0, 0, 2, "Should skins be dropped on player death.", "0 = use keep inventory rule", "1 = never drop", "2 = always drop").bind(v -> prefersWardrobeDropOnDeath = v, () -> prefersWardrobeDropOnDeath);
            });
            defineCategory("library", "Setting for the library blocks.", () -> {
                define("allowDownloadingSkins", false, "Allows clients to save skins from a server to their local computer using the library.").bind(v -> allowDownloadingSkins = v, () -> allowDownloadingSkins);
                define("allowUploadingSkins", true, "Allows clients to load skins from their local computer onto the server using the library.").bind(v -> allowUploadingSkins = v, () -> allowUploadingSkins);
                define("extractOfficialSkins", true, "Allow the mod to extract the official skins that come with the mod into the library folder.").bind(v -> extractOfficialSkins = v, () -> extractOfficialSkins);
                define("allowPreviewSkins", true, "Shows model previews in the library.", "Causes a lot of extra load on servers.", "Best to turn off on high population servers").bind(n -> allowLibraryPreviews = n, () -> allowLibraryPreviews);
                define("allowManageSkins", false, "Allows clients to manage skins of the server computer library.", "Required permission level 5 or higher.").bind(v -> allowLibraryRemoteManage = v, () -> allowLibraryRemoteManage);

                defineList("skinServerURLs", String.class, "We priority use https for the access token APIs.").bind(v -> customSkinServerURLs = new ArrayList<>(v), () -> new ArrayList<>(customSkinServerURLs));
            });
            defineCategory("database", "Setting for the Database.", () -> {
                define("skin", "", "Save/Load skin data for the database.", "example1: \"jdbc:mysql://<localhost>[:3306]/<database>[?user=<username>][&password=<password>]\"", "example2: \"jdbc:sqlite://</path/name.db>\"").bind(v -> skinDatabaseURL = v, null);
                defineInRange("fallback", 0, 0, 2, "Use fallback when database is specified.", "0 = migrate", "1 = disable", "2 = enable").bind(v -> skinDatabaseFallback = v, null);
                defineInRange("keepalive", 600, 0, 86400, "Keep alive time check when database is specified.", "the unit is seconds, 0 is disabled.").bind(v -> skinDatabaseKeepAlive = v, null);
            });

            defineCategory("holiday-events", "Enable/disable holiday events.", () -> {
                define("disableAllHolidayEvents", false, "Setting to true will disable all holiday events. What's wrong with you!").bind(v -> disableAllHolidayEvents = v, () -> disableAllHolidayEvents);
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

            defineCategory("cache", "Change (memory use/IO access) ratio by category setting in this category.", () -> {
                defineInRange("expireTime", 6000, 0, 36000, "How long in seconds the server will keep skins in it's cache.", "Default 600 seconds is 10 minutes.", "Setting to 0 turns off this option.").bind(v -> skinCacheExpireTime = v, () -> skinCacheExpireTime);
                defineInRange("maxSize", 2000, 0, 10000, "Max size the skin cache can reach before skins are removed.", "Setting to 0 turns off this option.").bind(v -> skinCacheMaxSize = v, () -> skinCacheMaxSize);
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
            defineCategory("overrides", "Custom list of items that can be skinned.", () -> {
                defineList("itemOverrides", String.class, "Format [\"override type:mod id:item name\"]", "Valid override types are: sword, shield, bow, pickaxe, axe, shovel, hoe and item", "example [\"sword:minecraft:iron_sword\",\"sword:minecraft:gold_sword\"]").bind(n -> overrides = new ArrayList<>(n), () -> new ArrayList<>(overrides));

                define("enableMatchingByItemId", true, "Tries to automatically assign the correct type of skin type without config and tag.").bind(v -> enableMatchingByItemId = v, () -> enableMatchingByItemId);
                defineList("matchingBlacklistByItemId", String.class, "If the matching system wrong, you can add the item id here to this ignore it.").bind(v -> disableMatchingItems = new ArrayList<>(v), () -> new ArrayList<>(disableMatchingItems));
            });
        }
    }

    public static void init() {
        EnvironmentExecutor.didSetup(EnvironmentType.COMMON, () -> () -> COMMON.notify(() -> {
            // when the server config is changes, we need to synchronize it again.
            if (EnvironmentManager.getServer() != null && EnvironmentManager.isDedicatedServer()) {
                NetworkManager.sendToAll(new UpdateContextPacket());
            }
        }));
    }
}
