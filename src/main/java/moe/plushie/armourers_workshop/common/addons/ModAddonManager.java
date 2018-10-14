package moe.plushie.armourers_workshop.common.addons;

import java.util.ArrayList;
import java.util.HashSet;

import moe.plushie.armourers_workshop.common.addons.ModAddon.ItemOverrideType;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.item.Item;

public final class ModAddonManager {
    
    private static final ArrayList<ModAddon> LOADED_ADDONS = new ArrayList<ModAddon>(); 
    
    private static final HashSet<String> ITEM_OVERRIDES = new HashSet<String>();
    
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
    
    private ModAddonManager() {
    }
    
    public static void preInit() {
        loadAddons();
        for (int i = 0; i < LOADED_ADDONS.size(); i++) {
            if (LOADED_ADDONS.get(i).isModLoaded()) {
                LOADED_ADDONS.get(i).preInit();
            }
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
        
        LOADED_ADDONS.add(addonAquaTweaks);
        LOADED_ADDONS.add(addonBalkonsWeaponMod);
        LOADED_ADDONS.add(addonBattlegear2);
        LOADED_ADDONS.add(addonCustomNPCS);
        LOADED_ADDONS.add(addonBetterStorage);
        LOADED_ADDONS.add(addonBotania);
        LOADED_ADDONS.add(addonBuildCraft);
        LOADED_ADDONS.add(addonGlassShards);
        LOADED_ADDONS.add(addonJBRAClient);
        LOADED_ADDONS.add(addonLittleMaidMob);
        LOADED_ADDONS.add(addonMaplecrafted);
        LOADED_ADDONS.add(addonMekanismTools);
        LOADED_ADDONS.add(addonMetallurgy);
        LOADED_ADDONS.add(addonMinecraft);
        LOADED_ADDONS.add(addonMinecraftComesAlive);
        LOADED_ADDONS.add(addonMoreSwordsMod);
        LOADED_ADDONS.add(addonNEI);
        LOADED_ADDONS.add(addonOreSpawn);
        LOADED_ADDONS.add(addonThaumcraft);
        LOADED_ADDONS.add(addonTinkersConstruct);
        LOADED_ADDONS.add(addonTwilightForest);
        LOADED_ADDONS.add(addonZeldaSwordSkills);
    }
    
    public static void init() {
        for (int i = 0; i < LOADED_ADDONS.size(); i++) {
            if (LOADED_ADDONS.get(i).isModLoaded()) {
                LOADED_ADDONS.get(i).init();
                ITEM_OVERRIDES.addAll(LOADED_ADDONS.get(i).getItemOverrides());
            }
        }
        String[] keys = ITEM_OVERRIDES.toArray(new String[ITEM_OVERRIDES.size()]);
        for (int i = 0; i < ITEM_OVERRIDES.size(); i++) {
            ModLogger.log(keys[i]);
        }
    }
    
    public static void postInit() {
        for (int i = 0; i < LOADED_ADDONS.size(); i++) {
            if (LOADED_ADDONS.get(i).isModLoaded()) {
                LOADED_ADDONS.get(i).postInit();
            }
        }
    }
    
    public static void initRenderers() {
        for (int i = 0; i < LOADED_ADDONS.size(); i++) {
            if (LOADED_ADDONS.get(i).isModLoaded()) {
                LOADED_ADDONS.get(i).initRenderers();
            }
        }
    }
    
    public static HashSet<String> getItemOverrides() {
        return ITEM_OVERRIDES;
    }
    
    public static void setItemOverrides(String[] itemOverrides) {
        ITEM_OVERRIDES.clear();
        for (int i = 0; i < itemOverrides.length; i++) {
            ITEM_OVERRIDES.add(itemOverrides[i]);
        }
        String[] keys = ITEM_OVERRIDES.toArray(new String[ITEM_OVERRIDES.size()]);
        for (int i = 0; i < ITEM_OVERRIDES.size(); i++) {
            ModLogger.log(keys[i]);
        }
    }
    
    public static boolean isOverrideItem(ItemOverrideType type, Item item) {
        String key = type.toString().toLowerCase() + ":" + item.getRegistryName().toString();
        return ITEM_OVERRIDES.contains(key);
    }
    
    public static void addOverrideItem(ItemOverrideType type, Item item) {
        String key = type.toString().toLowerCase() + item.getRegistryName().toString();
        ITEM_OVERRIDES.add(key);
    }
    
    public static enum RenderType {
        SWORD,
        BOW;
    }
}
