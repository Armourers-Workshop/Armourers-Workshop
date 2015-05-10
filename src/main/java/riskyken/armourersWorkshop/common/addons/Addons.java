package riskyken.armourersWorkshop.common.addons;

import java.util.ArrayList;

import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import riskyken.armourersWorkshop.utils.EventState;
import cpw.mods.fml.common.Loader;

public final class Addons {
    
    private static ArrayList<AbstractAddon> loadedAddons = new ArrayList<AbstractAddon>(); 
    
    public static boolean weaponmodCompatibility;
    public static boolean betterStorageCompatibility;
    public static boolean botaniaCompatibility;
    public static boolean minecraftCompatibility;
    public static boolean tConstructCompatibility;
    public static boolean thaumcraftCompatibility;
    public static boolean zeldaswordskillsCompatibility;
    public static boolean moreSwordsModCompatibility;
    public static boolean battlegear2Compatibility = true;
    public static boolean mekanismToolsCompatibility;
    
    public static void init() {
        if (minecraftCompatibility) {
            loadedAddons.add(new AddonMinecraft()); 
        }
        if (Loader.isModLoaded("Botania") & botaniaCompatibility) {
            loadedAddons.add(new AddonBotania());
        }
        if (Loader.isModLoaded("betterstorage") & betterStorageCompatibility) {
            loadedAddons.add(new AddonBetterStorage());
        }
        if (Loader.isModLoaded("Thaumcraft") & thaumcraftCompatibility) {
            loadedAddons.add(new AddonThaumcraft());
        }
        if (Loader.isModLoaded("weaponmod") & weaponmodCompatibility) {
            loadedAddons.add(new AddonBalkonsWeaponMod());
        }
        if (Loader.isModLoaded("TConstruct") & tConstructCompatibility) {
            loadedAddons.add(new AddonTConstruct());
        }
        if (Loader.isModLoaded("zeldaswordskills") & zeldaswordskillsCompatibility) {
            loadedAddons.add(new AddonZeldaSwordSkills());
        }
        if (Loader.isModLoaded("MSM3") & moreSwordsModCompatibility) {
            loadedAddons.add(new AddonMoreSwordsMod());
        }
        if (Loader.isModLoaded("battlegear2") & battlegear2Compatibility) {
            loadedAddons.add(new AddonBattlegear2());
        }
        if (Loader.isModLoaded("MekanismTools") & mekanismToolsCompatibility) {
            loadedAddons.add(new AddonMekanismTools());
        }
        
        for (int i = 0; i < loadedAddons.size(); i++) {
            loadedAddons.get(i).init();
        }
    }
    
    public static void onWeaponRender(ItemRenderType type, EventState state) {
        for (int i = 0; i < loadedAddons.size(); i++) {
            loadedAddons.get(i).onWeaponRender(type, state);
        }
    }
    
    public static void initRenderers() {    
        for (int i = 0; i < loadedAddons.size(); i++) {
            loadedAddons.get(i).initRenderers();
        }
    }
}
