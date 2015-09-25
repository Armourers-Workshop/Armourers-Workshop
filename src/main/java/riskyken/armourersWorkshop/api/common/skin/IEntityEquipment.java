package riskyken.armourersWorkshop.api.common.skin;

import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;


public interface IEntityEquipment {
    
    public void addEquipment(ISkinType equipmentSkinType, ISkinPointer skinPointer);
    
    public void removeEquipment(ISkinType equipmentSkinType);
    
    public boolean haveEquipment(ISkinType equipmentSkinType);
    
    public int getEquipmentId(ISkinType equipmentSkinType);
    
    public ISkinDye getSkinDye(ISkinType skinType);
}
