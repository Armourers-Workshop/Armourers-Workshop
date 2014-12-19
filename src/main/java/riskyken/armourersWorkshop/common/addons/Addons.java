package riskyken.armourersWorkshop.common.addons;

import java.util.ArrayList;

import cpw.mods.fml.common.Loader;

public final class Addons {
    
    private static ArrayList<AbstractAddon> loadedAddons = new ArrayList<AbstractAddon>(); 
    
    public static void init() {
        if (Loader.isModLoaded("Botania")) {
            loadedAddons.add(new AddonBotania());
        }
        if (Loader.isModLoaded("betterstorage")) {
            loadedAddons.add(new AddonBetterStorage());
        }
        if (Loader.isModLoaded("Thaumcraft")) {
            loadedAddons.add(new AddonThaumcraft());
        }
        if (Loader.isModLoaded("weaponmod")) {
            loadedAddons.add(new AddonBalkonsWeaponMod());
        }
        if (Loader.isModLoaded("TConstruct")) {
            loadedAddons.add(new AddonTConstruct());
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
