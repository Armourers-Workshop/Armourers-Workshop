package riskyken.armourersWorkshop.common.equipment.skin;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.minecraft.util.StatCollector;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class SkinTypeRegistry {
    
    public static SkinTypeRegistry INSTANCE;
    
    private LinkedHashMap<String, ISkinType> skinTypeMap;
    
    public static void init() {
        INSTANCE = new SkinTypeRegistry();
    }
    
    public SkinTypeRegistry() {
        skinTypeMap = new LinkedHashMap<String, ISkinType>();
        registerSkins();
    }
    
    private void registerSkins() {
        registerSkin(new SkinHead());
        registerSkin(new SkinChest());
        registerSkin(new SkinLegs());
        registerSkin(new SkinSkirt());
        registerSkin(new SkinFeet());
    }
    
    private void registerSkin(ISkinType skinType) {
        skinTypeMap.put(skinType.getRegistryName(), skinType);
        ModLogger.log("Registering equipment skin type : " + skinType.getRegistryName());
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
        default:
            return null;
        }
    }
    
    public ArrayList<ISkinType> getRegisteredSkins() {
        ArrayList<ISkinType> skinTypes = new ArrayList<ISkinType>();
        for (int i = 0; i < skinTypeMap.size(); i++) {
            String key = (String) skinTypeMap.keySet().toArray()[i];
            skinTypes.add(skinTypeMap.get(key));
        }
        
        return skinTypes;
    }
    
    @SideOnly(Side.CLIENT)
    public String getLocalizedSkinTypeName(ISkinType skinType) {
        String localizedName = "skinType." + LibModInfo.ID.toLowerCase() + ":" + skinType.getRegistryName() + ".name";
        localizedName = StatCollector.translateToLocal(localizedName);
        return localizedName;
    }
}
