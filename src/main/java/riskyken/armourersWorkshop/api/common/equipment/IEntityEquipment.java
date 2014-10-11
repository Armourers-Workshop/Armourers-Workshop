package riskyken.armourersWorkshop.api.common.equipment;


public interface IEntityEquipment {
    
    public void addEquipment(EnumEquipmentType type, int equipmentId);
    
    public void removeEquipment(EnumEquipmentType type);
    
    public boolean haveEquipment(EnumEquipmentType type);
    
    public int getEquipmentId(EnumEquipmentType type);
}
