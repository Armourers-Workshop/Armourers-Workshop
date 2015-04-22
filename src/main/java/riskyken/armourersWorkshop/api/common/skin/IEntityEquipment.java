package riskyken.armourersWorkshop.api.common.skin;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;


public interface IEntityEquipment {
    
    public void addEquipment(ISkinType equipmentSkinType, int equipmentId);
    
    public void removeEquipment(ISkinType equipmentSkinType);
    
    public boolean haveEquipment(ISkinType equipmentSkinType);
    
    public int getEquipmentId(ISkinType equipmentSkinType);
}
