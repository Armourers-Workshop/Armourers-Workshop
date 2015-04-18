package riskyken.armourersWorkshop.common.equipment.skin;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.minecraft.util.StatCollector;
import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinType;
import riskyken.armourersWorkshop.common.equipment.skin.type.SkinChest;
import riskyken.armourersWorkshop.common.equipment.skin.type.SkinFeet;
import riskyken.armourersWorkshop.common.equipment.skin.type.SkinHead;
import riskyken.armourersWorkshop.common.equipment.skin.type.SkinLegs;
import riskyken.armourersWorkshop.common.equipment.skin.type.SkinSkirt;
import riskyken.armourersWorkshop.common.equipment.skin.type.SkinSword;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class SkinTypeRegistry {
    
    public static SkinTypeRegistry INSTANCE;
    
    public static ISkinType skinHead;
    public static ISkinType skinChest;
    public static ISkinType skinLegs;
    public static ISkinType skinSkirt;
    public static ISkinType skinFeet;
    public static ISkinType skinSword;
    public static ISkinType skinBow;
    
    private LinkedHashMap<String, ISkinType> skinTypeMap;
    
    public static void init() {
        INSTANCE = new SkinTypeRegistry();
    }
    
    public SkinTypeRegistry() {
        skinTypeMap = new LinkedHashMap<String, ISkinType>();
        registerSkins();
    }
    
    private void registerSkins() {
        skinHead = new SkinHead();
        skinChest = new SkinChest();
        skinLegs = new SkinLegs();
        skinSkirt = new SkinSkirt();
        skinFeet = new SkinFeet();
        skinSword = new SkinSword();
        
        registerSkin(skinHead);
        registerSkin(skinChest);
        registerSkin(skinLegs);
        registerSkin(skinSkirt);
        registerSkin(skinFeet);
        registerSkin(skinSword);
    }
    
    private void registerSkin(ISkinType skinType) {
        skinType.setId(skinTypeMap.size());
        skinTypeMap.put(skinType.getRegistryName(), skinType);
        ModLogger.log("Registering equipment skin id: " + skinType.getId() + " type:" + skinType.getRegistryName());
    }
    
    public ISkinType getSkinFromRegistryName(String registryName) {
        return skinTypeMap.get(registryName);
    }
    
    public ISkinType getSkinFromLegacyId(int legacyId) {
        switch (legacyId) {
        case 0:
            return getSkinFromRegistryName("armourers:head");
        case 1:
            return getSkinFromRegistryName("armourers:chest");
        case 2:
            return getSkinFromRegistryName("armourers:legs");
        case 3:
            return getSkinFromRegistryName("armourers:skirt");
        case 4:
            return getSkinFromRegistryName("armourers:feet");
        case 5:
            return getSkinFromRegistryName("armourers:sword");
        case 6:
            return getSkinFromRegistryName("armourers:bow");
        default:
            return null;
        }
    }
    
    public int getLegacyIdForSkin(ISkinType skinType) {
        return skinType.getId();
    }
    
    public ArrayList<ISkinType> getRegisteredSkins() {
        ArrayList<ISkinType> skinTypes = new ArrayList<ISkinType>();
        for (int i = 0; i < skinTypeMap.size(); i++) {
            String key = (String) skinTypeMap.keySet().toArray()[i];
            skinTypes.add(skinTypeMap.get(key));
        }
        return skinTypes;
    }
    
    public int getNumberOfSkinRegistered() {
        return skinTypeMap.size();
    }
    
    @SideOnly(Side.CLIENT)
    public String getLocalizedSkinTypeName(ISkinType skinType) {
        String localizedName = "skinType." + LibModInfo.ID.toLowerCase() + ":" + skinType.getRegistryName() + ".name";
        localizedName = StatCollector.translateToLocal(localizedName);
        return localizedName;
    }
}
