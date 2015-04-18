package riskyken.armourersWorkshop.api.common.equipment;

import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinType;


public interface IEntityEquipment {
    
    public void addEquipment(ISkinType skinType, int equipmentId);
    
    public void removeEquipment(ISkinType skinType);
    
    public boolean haveEquipment(ISkinType skinType);
    
    public int getEquipmentId(ISkinType skinType);
}
