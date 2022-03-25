package moe.plushie.armourers_workshop.api.skin.entity;


import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.skin.ISkinDye;

public interface IEntityEquipment {
    
    public void addEquipment(ISkinType skinType, int slotIndex, ISkinDescriptor skinPointer);
    
    public void removeEquipment(ISkinType skinType, int slotIndex);
    
    public boolean haveEquipment(ISkinType skinType, int slotIndex);
    
    @Deprecated
    public int getEquipmentId(ISkinType skinType, int slotIndex);
    
    public ISkinDescriptor getSkinPointer(ISkinType skinType, int slotIndex);
    
    @Deprecated
    public ISkinDye getSkinDye(ISkinType skinType, int slotIndex);
    
    @Deprecated
    public int getNumberOfSlots();
}
