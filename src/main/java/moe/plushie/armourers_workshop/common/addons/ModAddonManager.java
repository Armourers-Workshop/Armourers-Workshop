package moe.plushie.armourers_workshop.common.addons;

import java.util.ArrayList;
import java.util.HashSet;

import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModAddonManager {
    
    private static final ArrayList<ModAddon> LOADED_ADDONS = new ArrayList<ModAddon>(); 
    
    private static final HashSet<String> ITEM_OVERRIDES = new HashSet<String>();
    
    //public static AddonAquaTweaks addonAquaTweaks;
    //public static AddonBalkonsWeaponMod addonBalkonsWeaponMod;
    //public static AddonBattlegear2 addonBattlegear2;
    //public static AddonBetterStorage addonBetterStorage;
    //public static AddonBotania addonBotania;
    public static AddonBuildCraft addonBuildCraft;
    public static ModAddon addonColoredLights;
    public static AddonCustomNPCS addonCustomNPCS;
    //public static AddonGlassShards addonGlassShards;
    public static AddonJBRAClient addonJBRAClient;
    //public static AddonLittleMaidMob addonLittleMaidMob;
    //public static AddonMaplecrafted addonMaplecrafted;
    //public static AddonMekanismTools addonMekanismTools;
    //public static AddonMetallurgy addonMetallurgy;
    public static AddonMinecraft addonMinecraft;
    //public static AddonMinecraftComesAlive addonMinecraftComesAlive;
    public static ModAddon addonMorePlayerModels;
    //public static AddonMoreSwordsMod addonMoreSwordsMod;
    public static AddonNEI addonNEI;
    //public static AddonOreSpawn addonOreSpawn;
    public static AddonShaders addonShaders;
    public static ModAddon addonSmartMoving;
    //public static AddonThaumcraft addonThaumcraft;
    //public static AddonTinkersConstruct addonTinkersConstruct;
    //public static AddonTwilightForest addonTwilightForest;
    //public static AddonZeldaSwordSkills addonZeldaSwordSkills;
    
    private ModAddonManager() {
    }
    
    public static void preInit() {
        loadAddons();
        for (ModAddon modAddon : LOADED_ADDONS) {
            modAddon.preInit();
        }
    }
    
    private static void loadAddons() {
        ModLogger.log("Loading addons");
        //addonAquaTweaks = new AddonAquaTweaks();
        //addonBalkonsWeaponMod = new AddonBalkonsWeaponMod();
        //addonBattlegear2 = new AddonBattlegear2();
        //addonBetterStorage = new AddonBetterStorage();
        //addonBotania = new AddonBotania();
        addonBuildCraft = new AddonBuildCraft();
        addonColoredLights = new ModAddon("easycoloredlights", "Colored Lights");
        addonCustomNPCS = new AddonCustomNPCS();
        //addonGlassShards = new AddonGlassShards();
        addonJBRAClient = new AddonJBRAClient();
        //addonLittleMaidMob = new AddonLittleMaidMob();
        //addonMaplecrafted = new AddonMaplecrafted();
        //addonMekanismTools = new AddonMekanismTools();
        //addonMetallurgy = new AddonMetallurgy();
        addonMinecraft = new AddonMinecraft();
        //addonMinecraftComesAlive = new AddonMinecraftComesAlive();
        addonMorePlayerModels = new ModAddon("moreplayermodels", "More Player Models");
        //addonMoreSwordsMod = new AddonMoreSwordsMod();
        addonNEI = new AddonNEI();
        //addonOreSpawn = new AddonOreSpawn();
        addonShaders = new AddonShaders();
        addonSmartMoving = new ModAddon("SmartMoving", "Smart Moving");
        //addonThaumcraft = new AddonThaumcraft();
        //addonTinkersConstruct = new AddonTinkersConstruct();
        //addonTwilightForest = new AddonTwilightForest();
        //addonZeldaSwordSkills = new AddonZeldaSwordSkills();
    }
    
    public static void init() {
        for (ModAddon modAddon : LOADED_ADDONS) {
            modAddon.init();
            if (modAddon.isItemSkinningSupport() & modAddon.isModLoaded()) {
                ITEM_OVERRIDES.addAll(modAddon.getItemOverrides());
            }
        }
        String[] keys = ITEM_OVERRIDES.toArray(new String[ITEM_OVERRIDES.size()]);
        for (int i = 0; i < ITEM_OVERRIDES.size(); i++) {
            ModLogger.log(keys[i]);
        }
    }
    
    public static void postInit() {
        for (ModAddon modAddon : LOADED_ADDONS) {
            modAddon.postInit();
        }
    }
    
    @SideOnly(Side.CLIENT)
    public static void initRenderers() {
        for (ModAddon modAddon : LOADED_ADDONS) {
            modAddon.initRenderers();
        }
    }
    
    public static HashSet<String> getItemOverrides() {
        return ITEM_OVERRIDES;
    }
    
    public static ArrayList<ModAddon> getLoadedAddons() {
        return LOADED_ADDONS;
    }
    
    public static void setItemOverrides(String[] itemOverrides) {
        ITEM_OVERRIDES.clear();
        for (int i = 0; i < itemOverrides.length; i++) {
            ITEM_OVERRIDES.add(itemOverrides[i]);
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
    
    public static enum ItemOverrideType {
        SWORD,
        SHIELD,
        BOW,
        
        PICKAXE,
        AXE,
        SHOVEL,
        HOE,
        
        ITEM;
    }
}
