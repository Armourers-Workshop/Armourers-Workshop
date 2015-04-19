package riskyken.armourersWorkshop.common.equipment.skin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import net.minecraft.util.StatCollector;
import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinPart;
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
    private HashMap<String, ISkinPart> skinPartMap;
    
    public static void init() {
        INSTANCE = new SkinTypeRegistry();
    }
    
    public SkinTypeRegistry() {
        skinTypeMap = new LinkedHashMap<String, ISkinType>();
        skinPartMap = new HashMap<String, ISkinPart>();
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
        ModLogger.log("Registering skin type - id: " + skinType.getId() + " name:" + skinType.getRegistryName());
        skinTypeMap.put(skinType.getRegistryName(), skinType);
        ArrayList<ISkinPart> skinParts = skinType.getSkinParts();
        for (int i = 0; i < skinParts.size(); i++) {
            ISkinPart skinPart = skinParts.get(i);
            String partName = skinType.getRegistryName() + "." + skinPart.getPartName();
            ModLogger.log("Registering skin part - name:" + partName);
            skinPartMap.put(partName, skinPart);
        }
    }
    
    public ISkinType getSkinTypeFromRegistryName(String registryName) {
        return skinTypeMap.get(registryName);
    }
    
    public ISkinType getSkinTypeFromLegacyId(int legacyId) {
        switch (legacyId) {
        case 0:
            return getSkinTypeFromRegistryName("armourers:head");
        case 1:
            return getSkinTypeFromRegistryName("armourers:chest");
        case 2:
            return getSkinTypeFromRegistryName("armourers:legs");
        case 3:
            return getSkinTypeFromRegistryName("armourers:skirt");
        case 4:
            return getSkinTypeFromRegistryName("armourers:feet");
        case 5:
            return getSkinTypeFromRegistryName("armourers:sword");
        case 6:
            return getSkinTypeFromRegistryName("armourers:bow");
        default:
            return null;
        }
    }
    
    public int getLegacyIdForSkin(ISkinType skinType) {
        return skinType.getId();
    }
    
    public ISkinPart getSkinPartFromRegistryName(String registryName) {
        return skinPartMap.get(registryName);
    }
    
    public ISkinPart getSkinPartFromLegacyId(int legacyId) {
        switch (legacyId) {
        case 0:
            return getSkinPartFromRegistryName("armourers:head.base");
        case 1:
            return getSkinPartFromRegistryName("armourers:chest.base");
        case 2:
            return getSkinPartFromRegistryName("armourers:chest.leftArm");
        case 3:
            return getSkinPartFromRegistryName("armourers:chest.rightArm");
        case 4:
            return getSkinPartFromRegistryName("armourers:legs.leftLeg");
        case 5:
            return getSkinPartFromRegistryName("armourers:legs.rightLeg");
        case 6:
            return getSkinPartFromRegistryName("armourers:skirt.base");
        case 7:
            return getSkinPartFromRegistryName("armourers:feet.leftFoot");
        case 8:
            return getSkinPartFromRegistryName("armourers:feet.rightFoot");
        case 9:
            return getSkinPartFromRegistryName("armourers:sword.base");
        case 10:
            return getSkinPartFromRegistryName("armourers:bow.base");
        default:
            return null;
        }
    }
    
    public ArrayList<ISkinType> getRegisteredSkinTypes() {
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
