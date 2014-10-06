package riskyken.armourersWorkshop.api.common.equipment;

import riskyken.armourersWorkshop.api.common.equipment.armour.EnumArmourType;

public interface IEntityEquipment {
    
    public void addEquipment(EnumArmourType type, int equipmentId);
    
    public void removeEquipment(EnumArmourType type);
    
    public boolean haveEquipment(EnumArmourType type);
    
    public int getEquipmentId(EnumArmourType type);
}
