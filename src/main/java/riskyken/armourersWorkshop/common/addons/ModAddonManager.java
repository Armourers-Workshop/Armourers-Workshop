package riskyken.armourersWorkshop.common.addons;

import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import net.minecraftforge.fml.common.Loader;
import riskyken.armourersWorkshop.utils.ModLogger;

public final class ModAddonManager {
    
    private static ArrayList<ModAddon> loadedAddons = new ArrayList<ModAddon>(); 
    
    public static ArrayList<String> itemOverrides = new ArrayList<String>();
    
    public static AddonAquaTweaks addonAquaTweaks;
    public static AddonBalkonsWeaponMod addonBalkonsWeaponMod;
    public static AddonBattlegear2 addonBattlegear2;
    public static AddonBetterStorage addonBetterStorage;
    public static AddonBotania addonBotania;
    public static AddonBuildCraft addonBuildCraft;
    public static AddonColoredLights addonColoredLights;
    public static AddonCustomNPCS addonCustomNPCS;
    public static AddonGlassShards addonGlassShards;
    public static AddonJBRAClient addonJBRAClient;
    public static AddonLittleMaidMob addonLittleMaidMob;
    public static AddonMaplecrafted addonMaplecrafted;
    public static AddonMekanismTools addonMekanismTools;
    public static AddonMetallurgy addonMetallurgy;
    public static AddonMinecraft addonMinecraft;
    public static AddonMinecraftComesAlive addonMinecraftComesAlive;
    public static AddonMorePlayerModels addonMorePlayerModels;
    public static AddonMoreSwordsMod addonMoreSwordsMod;
    public static AddonNEI addonNEI;
    public static AddonOreSpawn addonOreSpawn;
    public static AddonShaders addonShaders;
    public static AddonSmartMoving addonSmartMoving;
    public static AddonThaumcraft addonThaumcraft;
    public static AddonTinkersConstruct addonTinkersConstruct;
    public static AddonTwilightForest addonTwilightForest;
    public static AddonZeldaSwordSkills addonZeldaSwordSkills;
    
    public static void preInit() {
        loadAddons();
        for (int i = 0; i < loadedAddons.size(); i++) {
            loadedAddons.get(i).preInit();
        }
    }
    
    private static void loadAddons() {
        ModLogger.log("Loading addons");
        addonAquaTweaks = new AddonAquaTweaks();
        addonBalkonsWeaponMod = new AddonBalkonsWeaponMod();
        addonBattlegear2 = new AddonBattlegear2();
        addonBetterStorage = new AddonBetterStorage();
        addonBotania = new AddonBotania();
        addonBuildCraft = new AddonBuildCraft();
        addonColoredLights = new AddonColoredLights();
        addonCustomNPCS = new AddonCustomNPCS();
        addonGlassShards = new AddonGlassShards();
        addonJBRAClient = new AddonJBRAClient();
        addonLittleMaidMob = new AddonLittleMaidMob();
        addonMaplecrafted = new AddonMaplecrafted();
        addonMekanismTools = new AddonMekanismTools();
        addonMetallurgy = new AddonMetallurgy();
        addonMinecraft = new AddonMinecraft();
        addonMinecraftComesAlive = new AddonMinecraftComesAlive();
        addonMorePlayerModels = new AddonMorePlayerModels();
        addonMoreSwordsMod = new AddonMoreSwordsMod();
        addonNEI = new AddonNEI();
        addonOreSpawn = new AddonOreSpawn();
        addonShaders = new AddonShaders();
        addonSmartMoving = new AddonSmartMoving();
        addonThaumcraft = new AddonThaumcraft();
        addonTinkersConstruct = new AddonTinkersConstruct();
        addonTwilightForest = new AddonTwilightForest();
        addonZeldaSwordSkills = new AddonZeldaSwordSkills();
        
        loadedAddons.add(addonAquaTweaks);
        loadedAddons.add(addonBalkonsWeaponMod);
        loadedAddons.add(addonBattlegear2);
        loadedAddons.add(addonCustomNPCS);
        loadedAddons.add(addonBetterStorage);
        loadedAddons.add(addonBotania);
        loadedAddons.add(addonBuildCraft);
        loadedAddons.add(addonGlassShards);
        loadedAddons.add(addonJBRAClient);
        loadedAddons.add(addonLittleMaidMob);
        loadedAddons.add(addonMaplecrafted);
        loadedAddons.add(addonMekanismTools);
        loadedAddons.add(addonMetallurgy);
        loadedAddons.add(addonMinecraft);
        loadedAddons.add(addonMinecraftComesAlive);
        loadedAddons.add(addonMoreSwordsMod);
        loadedAddons.add(addonNEI);
        loadedAddons.add(addonOreSpawn);
        loadedAddons.add(addonThaumcraft);
        loadedAddons.add(addonTinkersConstruct);
        loadedAddons.add(addonTwilightForest);
        loadedAddons.add(addonZeldaSwordSkills);
    }
    
    public static String[] getDefaultOverrides() {
        ArrayList<String> overrides = new ArrayList<String>();
        for (int i = 0; i < loadedAddons.size(); i++) {
            if (loadedAddons.get(i).getItemOverrides().size() > 0) {
                overrides.addAll(loadedAddons.get(i).getItemOverrides());
                if (i != loadedAddons.size() - 1) {
                    overrides.add("");
                }
            }
        }
        return overrides.toArray(new String[0]);
    }
    
    public static void init() {
        for (int i = 0; i < loadedAddons.size(); i++) {
            loadedAddons.get(i).init();
        }
    }
    
    public static void postInit() {
        for (int i = 0; i < loadedAddons.size(); i++) {
            loadedAddons.get(i).postInit();
        }
    }
    
    public static void initRenderers() {
        checkForDuplicateItemOverrides();
        overrideItemRenders();
    }
    
    private static void overrideItemRenders() {
        for (int i = 0; i < itemOverrides.size(); i++) {
            String arrayItem = itemOverrides.get(i);
            int splitterCount = arrayItem.length() - arrayItem.replace(":", "").length();
            if (splitterCount > 1) {
                String type = arrayItem.substring(0, arrayItem.indexOf(":"));
                arrayItem = arrayItem.substring(arrayItem.indexOf(":") + 1);
                String modId = arrayItem.substring(0, arrayItem.indexOf(":"));
                String itemId = arrayItem.substring(arrayItem.indexOf(":") + 1);
                if (Loader.isModLoaded(modId) | modId.equalsIgnoreCase("minecraft")) {
                    if (type.equalsIgnoreCase("bow")) {
                        overrideItemRenderer(modId, itemId, RenderType.BOW);
                    } else {
                        overrideItemRenderer(modId, itemId, RenderType.SWORD);
                    }
                }
            } else {
                if (!arrayItem.isEmpty()) {
                    ModLogger.log(Level.ERROR, String.format("Invalid item override in config file: %s", arrayItem));
                }
            }
        }
    }
    
    private static void checkForDuplicateItemOverrides() {
        for (int i = 0; i < itemOverrides.size(); i++) {
            if (!itemOverrides.get(i).isEmpty()) {
                if (countNumberOfAppearancesInArray(itemOverrides, itemOverrides.get(i)) > 1) {
                    ModLogger.log("Removing duplicate item override: " + itemOverrides.get(i));
                    itemOverrides.remove(i);
                }
            }
        }
    }
    
    private static int countNumberOfAppearancesInArray(ArrayList<String> list, String item) {
        int count = 0;
        item = item.trim();
        for (int i = 0; i < list.size(); i++) {
            if (item.equalsIgnoreCase(list.get(i).trim())) {
                count++;
            }
        }
        return count;
    }
    
    private static void overrideItemRenderer(String modId, String itemName, RenderType renderType) {
        /*
        Item item = GameRegistry.findItem(modId, itemName);
        if (item != null) {
            ItemStack stack = new ItemStack(item);
            IItemRenderer renderer = getItemRenderer(stack);
            
            if (renderer != null && renderer instanceof RenderItemEquipmentSkin) {
                ModLogger.log(Level.WARN, String.format("Tried to override the render on %s:%s but it has already been overridden.", modId, itemName));
                return;
            }
            
            ModLogger.log(String.format("Overriding render on %s:%s.", modId, itemName));
            switch (renderType) {
            case SWORD:
                MinecraftForgeClient.registerItemRenderer(item, new RenderItemSwordSkin(renderer));
                break;
            case BOW:
                MinecraftForgeClient.registerItemRenderer(item, new RenderItemBowSkin(renderer));
                break;
            }
        } else {
            ModLogger.log(Level.WARN, String.format("Unable to override item renderer for %s:%s. Can not find item.", modId, itemName));
        }
        */
    }
    /*
    private static IItemRenderer getItemRenderer(ItemStack stack) {
        try {
            IdentityHashMap<Item, IItemRenderer> customItemRenderers = null;
            customItemRenderers = ReflectionHelper.getPrivateValue(MinecraftForgeClient.class, null, "customItemRenderers");
            IItemRenderer renderer = customItemRenderers.get(stack.getItem());
            if (renderer != null) {
                return renderer;
            }
        } catch (Exception e) {
        }
        return null;
    }*/
    
    public static enum RenderType {
        SWORD,
        BOW;
    }
}
