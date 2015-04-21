package riskyken.armourersWorkshop.api.common.equipment.skin;

import java.util.ArrayList;

/**
 * Skin type registry is used to register new ISkinType's
 * and get register ISkinType's and ISkinPart's.
 * 
 * @author RiskyKen
 *
 */
public interface IEquipmentSkinTypeRegistry {
    
    /**
     * Register a new skin type.
     * @param skinType
     */
    public void registerSkin(IEquipmentSkinType skinType) ;
    
    public IEquipmentSkinType getSkinTypeFromRegistryName(String registryName);
    
    public IEquipmentSkinPart getSkinPartFromRegistryName(String registryName);
    
    public ArrayList<IEquipmentSkinType> getRegisteredSkinTypes();
    
    public int getNumberOfSkinRegistered();
}
