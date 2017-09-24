package riskyken.armourersWorkshop.api.common.skin;

import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;


public interface IEntityEquipment {
    
    public void addEquipment(ISkinType skinType, int slotIndex, ISkinPointer skinPointer);
    
    public void removeEquipment(ISkinType skinType, int slotIndex);
    
    public boolean haveEquipment(ISkinType skinType, int slotIndex);
    
    @Deprecated
    public int getEquipmentId(ISkinType skinType, int slotIndex);
    
    public ISkinPointer getSkinPointer(ISkinType skinType, int slotIndex);
    @Deprecated
    public ISkinDye getSkinDye(ISkinType skinType, int slotIndex);
    
    @Deprecated
    public int getNumberOfSlots();
}
