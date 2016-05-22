package riskyken.armourersWorkshop.common.skin.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinTypeRegistry;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.skin.type.arrow.SkinArrow;
import riskyken.armourersWorkshop.common.skin.type.block.SkinBlock;
import riskyken.armourersWorkshop.common.skin.type.bow.SkinBow;
import riskyken.armourersWorkshop.common.skin.type.chest.SkinChest;
import riskyken.armourersWorkshop.common.skin.type.feet.SkinFeet;
import riskyken.armourersWorkshop.common.skin.type.head.SkinHead;
import riskyken.armourersWorkshop.common.skin.type.legs.SkinLegs;
import riskyken.armourersWorkshop.common.skin.type.legs.SkinSkirt;
import riskyken.armourersWorkshop.common.skin.type.sword.SkinSword;
import riskyken.armourersWorkshop.common.skin.type.wings.SkinWings;
import riskyken.armourersWorkshop.utils.ModLogger;

public final class SkinTypeRegistry implements ISkinTypeRegistry {
    
    public static SkinTypeRegistry INSTANCE;
    
    public static ISkinType skinHead;
    public static ISkinType skinChest;
    public static ISkinType skinLegs;
    public static ISkinType skinSkirt;
    public static ISkinType skinFeet;
    public static ISkinType skinSword;
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
        MinecraftForge.EVENT_BUS.register(this);
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
        skinSword = new SkinSword();
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
        registerSkin(skinBow);
        registerSkin(skinArrow);
        registerSkin(skinBlock);
        //registerSkin(skinWings);
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
    
    public boolean isSkinDisabled(ISkinType skinType) {
        for (int i = 0; i < ConfigHandler.disabledSkins.length; i++) {
            if (skinType.getRegistryName().equals(ConfigHandler.disabledSkins[i])) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public ISkinType getSkinTypeFromRegistryName(String registryName) {
        if (registryName == null | registryName.trim().isEmpty()) {
            return null;
        }
        if(registryName.equals(skinSkirt.getRegistryName())) {
            return skinLegs;
        }
        ISkinType skinType = skinTypeMap.get(registryName);
        if (skinType != null && isSkinDisabled(skinType)) {
            return null;
        }
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
            return getSkinTypeFromRegistryName("armourers:arrow");
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
    
    @SideOnly(Side.CLIENT)
    public String getLocalizedSkinTypeName(ISkinType skinType) {
        String localizedName = "skinType." + skinType.getRegistryName() + ".name";
        return StatCollector.translateToLocal(localizedName);
    }
    
    @SideOnly(Side.CLIENT)
    public String getLocalizedSkinPartTypeName(ISkinPartType skinPartType) {
        String localizedName = "skinPartType." + skinPartType.getRegistryName() + ".name";
        return StatCollector.translateToLocal(localizedName);
    }
    
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

    @Override
    public ISkinType getSkinTypeHead() {
        return skinHead;
    }

    @Override
    public ISkinType getSkinTypeChest() {
        return skinChest;
    }

    @Override
    public ISkinType getSkinTypeLegs() {
        return skinLegs;
    }

    @Override
    public ISkinType getSkinTypeSkirt() {
        return skinSkirt;
    }

    @Override
    public ISkinType getSkinTypeFeet() {
        return skinFeet;
    }
}
