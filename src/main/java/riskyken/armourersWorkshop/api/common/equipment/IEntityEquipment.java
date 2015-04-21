package riskyken.armourersWorkshop.api.common.equipment;

import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinType;


public interface IEntityEquipment {
    
    public void addEquipment(IEquipmentSkinType equipmentSkinType, int equipmentId);
    
    public void removeEquipment(IEquipmentSkinType equipmentSkinType);
    
    public boolean haveEquipment(IEquipmentSkinType equipmentSkinType);
    
    public int getEquipmentId(IEquipmentSkinType equipmentSkinType);
}
