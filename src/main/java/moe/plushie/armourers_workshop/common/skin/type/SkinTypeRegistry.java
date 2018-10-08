package moe.plushie.armourers_workshop.common.skin.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinTypeRegistry;
import moe.plushie.armourers_workshop.common.skin.type.arrow.SkinArrow;
import moe.plushie.armourers_workshop.common.skin.type.block.SkinBlock;
import moe.plushie.armourers_workshop.common.skin.type.bow.SkinBow;
import moe.plushie.armourers_workshop.common.skin.type.chest.SkinChest;
import moe.plushie.armourers_workshop.common.skin.type.feet.SkinFeet;
import moe.plushie.armourers_workshop.common.skin.type.head.SkinHead;
import moe.plushie.armourers_workshop.common.skin.type.item.SkinItem;
import moe.plushie.armourers_workshop.common.skin.type.legs.SkinLegs;
import moe.plushie.armourers_workshop.common.skin.type.legs.SkinSkirt;
import moe.plushie.armourers_workshop.common.skin.type.wings.SkinWings;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class SkinTypeRegistry implements ISkinTypeRegistry {
    
    public static SkinTypeRegistry INSTANCE;
    
    public static ISkinType skinHead;
    public static ISkinType skinChest;
    public static ISkinType skinLegs;
    public static ISkinType skinSkirt;
    public static ISkinType skinFeet;
    public static ISkinType skinSword;
    public static ISkinType skinShield;
    public static ISkinType skinPickaxe;
    public static ISkinType skinAxe;
    public static ISkinType skinShovel;
    public static ISkinType skinHoe;
    public static ISkinType skinItem;
    public static ISkinType skinBow;
    public static ISkinType skinArrow;
    public static ISkinType skinBlock;
    public static ISkinType skinWings;
    
    private LinkedHashMap<String, ISkinType> skinTypeMap;
    private HashMap<String, ISkinPartType> skinPartMap;
    
    public static void init() {
        INSTANCE = new SkinTypeRegistry();
    }
    
    public SkinTypeRegistry() {
        skinTypeMap = new LinkedHashMap<String, ISkinType>();
        skinPartMap = new HashMap<String, ISkinPartType>();
        registerSkins();
    }
    
    private void registerSkins() {
        skinHead = new SkinHead();
        skinChest = new SkinChest();
        skinLegs = new SkinLegs();
        skinSkirt = new SkinSkirt();
        skinFeet = new SkinFeet();
        skinSword = new SkinItem("Sword");
        skinShield = new SkinItem("Shield");
        skinPickaxe = new SkinItem("Pickaxe");
        skinAxe = new SkinItem("Axe");
        skinShovel = new SkinItem("Shovel");
        skinHoe = new SkinItem("Hoe");
        skinItem = new SkinItem("Item");
        skinBow = new SkinBow();
        skinArrow = new SkinArrow();
        skinBlock = new SkinBlock();
        skinWings = new SkinWings();
        
        registerSkin(skinHead);
        registerSkin(skinChest);
        registerSkin(skinLegs);
        //registerSkin(skinSkirt);
        registerSkin(skinFeet);
        registerSkin(skinSword);
        registerSkin(skinPickaxe);
        registerSkin(skinAxe);
        registerSkin(skinShovel);
        registerSkin(skinHoe);
        registerSkin(skinItem);
        
        registerSkin(skinBow);
        //registerSkin(skinArrow);
        registerSkin(skinBlock);
        registerSkin(skinWings);
    }
    
    @Override
    public boolean registerSkin(ISkinType skinType) {
        if (skinType == null) {
            ModLogger.log(Level.WARN, "A mod tried to register a null skin type.");
            return false;
        }
        if (skinType.getRegistryName() == null || skinType.getRegistryName().trim().isEmpty()) {
            ModLogger.log(Level.WARN, "A mod tried to register a skin type with an invalid registry name.");
            return false;
        }
        if (skinPartMap.containsKey(skinType.getRegistryName())) {
            ModLogger.log(Level.WARN, "A mod tried to register a skin type with a registry name that is in use.");
            return false;
        }
        if (skinType.getSkinParts() == null || skinType.getSkinParts().size() == 0) {
            ModLogger.log(Level.WARN, "A mod tried to register a skin type no skin type parts.");
            return false;
        }
        
        ModLogger.log(String.format("Registering skin: %s", skinType.getRegistryName()));
        skinTypeMap.put(skinType.getRegistryName(), skinType);
        ArrayList<ISkinPartType> skinParts = skinType.getSkinParts();
        for (int i = 0; i < skinParts.size(); i++) {
            ISkinPartType skinPart = skinParts.get(i);
            skinPartMap.put(skinPart.getRegistryName(), skinPart);
        }
        return true;
    }
    
    @Override
    public ISkinType getSkinTypeFromRegistryName(String registryName) {
        if (registryName == null | registryName.trim().isEmpty()) {
            return null;
        }
        if(registryName.equals(skinSkirt.getRegistryName())) {
            return skinLegs;
        }
        if(registryName.equals(skinArrow.getRegistryName())) {
            return skinBow;
        }
        ISkinType skinType = skinTypeMap.get(registryName);
        return skinType;
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
            return getSkinTypeFromRegistryName("armourers:legs");
            //return getSkinTypeFromRegistryName("armourers:skirt");
        case 4:
            return getSkinTypeFromRegistryName("armourers:feet");
        case 5:
            return getSkinTypeFromRegistryName("armourers:sword");
        case 6:
            return getSkinTypeFromRegistryName("armourers:bow");
        case 7:
            return getSkinTypeFromRegistryName("armourers:bow");
            //return getSkinTypeFromRegistryName("armourers:arrow");
        default:
            return null;
        }
    }
    
    @Override
    public ISkinPartType getSkinPartFromRegistryName(String registryName) {
        if (registryName == null | registryName.trim().isEmpty()) {
            return null;
        }
        return skinPartMap.get(registryName);
    }
    
    public ISkinPartType getSkinPartFromLegacyId(int legacyId) {
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
            //return getSkinPartFromRegistryName("armourers:skirt.base");
            return getSkinPartFromRegistryName("armourers:legs.skirt");
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
    public ArrayList<ISkinType> getRegisteredSkinTypes() {
        ArrayList<ISkinType> skinTypes = new ArrayList<ISkinType>();
        for (int i = 0; i < skinTypeMap.size(); i++) {
            String registryName = (String) skinTypeMap.keySet().toArray()[i];
            ISkinType skinType = getSkinTypeFromRegistryName(registryName);
            if (skinType != null) {
                skinTypes.add(skinType);
            }
        }
        return skinTypes;
    }
    
    public int getNumberOfSkinRegistered() {
        return skinTypeMap.size();
    }
    
    public String getLocalizedSkinTypeName(ISkinType skinType) {
        String localizedName = "skinType." + skinType.getRegistryName() + ".name";
        return I18n.format(localizedName);
    }
    
    @SideOnly(Side.CLIENT)
    public String getLocalizedSkinPartTypeName(ISkinPartType skinPartType) {
        String localizedName = "skinPartType." + skinPartType.getRegistryName() + ".name";
        return I18n.format(localizedName);
    }
    /*
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 1) {
            for (int i = 0; i < skinTypeMap.size(); i++) {
                String registryName = (String) skinTypeMap.keySet().toArray()[i];
                ISkinType skinType = getSkinTypeFromRegistryName(registryName);
                if (skinType != null) {
                    skinType.registerIcon(event.map);
                }
            }
        }
    }
     */
}
