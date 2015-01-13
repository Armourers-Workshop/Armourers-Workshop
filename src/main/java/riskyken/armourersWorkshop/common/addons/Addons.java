package riskyken.armourersWorkshop.common.addons;

import java.util.ArrayList;

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
    }
    
    public static void initRenderers() {
        for (int i = 0; i < loadedAddons.size(); i++) {
            loadedAddons.get(i).init();
        }
        for (int i = 0; i < loadedAddons.size(); i++) {
            loadedAddons.get(i).initRenderers();
        }
    }
}
