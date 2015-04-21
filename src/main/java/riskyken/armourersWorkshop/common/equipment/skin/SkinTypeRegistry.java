package riskyken.armourersWorkshop.common.equipment.skin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinPart;
import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinType;
import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinTypeRegistry;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.equipment.skin.type.SkinBow;
import riskyken.armourersWorkshop.common.equipment.skin.type.SkinChest;
import riskyken.armourersWorkshop.common.equipment.skin.type.SkinFeet;
import riskyken.armourersWorkshop.common.equipment.skin.type.SkinHead;
import riskyken.armourersWorkshop.common.equipment.skin.type.SkinLegs;
import riskyken.armourersWorkshop.common.equipment.skin.type.SkinSkirt;
import riskyken.armourersWorkshop.common.equipment.skin.type.SkinSword;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class SkinTypeRegistry implements IEquipmentSkinTypeRegistry {
    
    public static SkinTypeRegistry INSTANCE;
    
    public static IEquipmentSkinType skinHead;
    public static IEquipmentSkinType skinChest;
    public static IEquipmentSkinType skinLegs;
    public static IEquipmentSkinType skinSkirt;
    public static IEquipmentSkinType skinFeet;
    public static IEquipmentSkinType skinSword;
    public static IEquipmentSkinType skinBow;
    
    private LinkedHashMap<String, IEquipmentSkinType> skinTypeMap;
    private HashMap<String, IEquipmentSkinPart> skinPartMap;
    
    public static void init() {
        INSTANCE = new SkinTypeRegistry();
    }
    
    public SkinTypeRegistry() {
        MinecraftForge.EVENT_BUS.register(this);
        skinTypeMap = new LinkedHashMap<String, IEquipmentSkinType>();
        skinPartMap = new HashMap<String, IEquipmentSkinPart>();
        registerSkins();
    }
    
    private void registerSkins() {
        skinHead = new SkinHead();
        skinChest = new SkinChest();
        skinLegs = new SkinLegs();
        skinSkirt = new SkinSkirt();
        skinFeet = new SkinFeet();
        skinSword = new SkinSword();
        skinBow = new SkinBow();
        
        registerSkin(skinHead);
        registerSkin(skinChest);
        registerSkin(skinLegs);
        registerSkin(skinSkirt);
        registerSkin(skinFeet);
        registerSkin(skinSword);
        registerSkin(skinBow);
    }
    
    @Override
    public void registerSkin(IEquipmentSkinType skinType) {
        skinType.setId(skinTypeMap.size());
        ModLogger.log("Registering skin type - id:" + skinType.getId() + " name:" + skinType.getRegistryName());
        skinTypeMap.put(skinType.getRegistryName(), skinType);
        ArrayList<IEquipmentSkinPart> skinParts = skinType.getSkinParts();
        for (int i = 0; i < skinParts.size(); i++) {
            IEquipmentSkinPart skinPart = skinParts.get(i);
            String partName = skinType.getRegistryName() + "." + skinPart.getPartName();
            ModLogger.log("Registering skin part - name:" + partName);
            skinPartMap.put(partName, skinPart);
        }
    }
    
    public boolean isSkinDisabled(IEquipmentSkinType skinType) {
        for (int i = 0; i < ConfigHandler.disabledSkins.length; i++) {
            if (skinType.getRegistryName().equals(ConfigHandler.disabledSkins[i])) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public IEquipmentSkinType getSkinTypeFromRegistryName(String registryName) {
        IEquipmentSkinType skinType = skinTypeMap.get(registryName);
        if (skinType != null && isSkinDisabled(skinType)) {
            return null;
        }
        return skinType;
    }
    
    public IEquipmentSkinType getSkinTypeFromLegacyId(int legacyId) {
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
    
    public int getLegacyIdForSkin(IEquipmentSkinType skinType) {
        return skinType.getId();
    }
    
    @Override
    public IEquipmentSkinPart getSkinPartFromRegistryName(String registryName) {
        return skinPartMap.get(registryName);
    }
    
    public IEquipmentSkinPart getSkinPartFromLegacyId(int legacyId) {
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
    
    @Override
    public ArrayList<IEquipmentSkinType> getRegisteredSkinTypes() {
        ArrayList<IEquipmentSkinType> skinTypes = new ArrayList<IEquipmentSkinType>();
        for (int i = 0; i < skinTypeMap.size(); i++) {
            String registryName = (String) skinTypeMap.keySet().toArray()[i];
            IEquipmentSkinType skinType = getSkinTypeFromRegistryName(registryName);
            if (skinType != null) {
                skinTypes.add(skinType);
            }
        }
        return skinTypes;
    }
    
    @Override
    public int getNumberOfSkinRegistered() {
        return skinTypeMap.size();
    }
    
    @SideOnly(Side.CLIENT)
    public String getLocalizedSkinTypeName(IEquipmentSkinType skinType) {
        String localizedName = "skinType." + LibModInfo.ID.toLowerCase() + ":" + skinType.getRegistryName() + ".name";
        localizedName = StatCollector.translateToLocal(localizedName);
        return localizedName;
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 1) {
            for (int i = 0; i < skinTypeMap.size(); i++) {
                String registryName = (String) skinTypeMap.keySet().toArray()[i];
                IEquipmentSkinType skinType = getSkinTypeFromRegistryName(registryName);
                if (skinType != null) {
                    skinType.registerIcon(event.map);
                }
            }
        }
    }
}
