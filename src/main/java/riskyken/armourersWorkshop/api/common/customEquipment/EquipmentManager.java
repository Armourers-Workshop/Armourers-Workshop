package riskyken.armourersWorkshop.api.common.customEquipment;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourType;
import riskyken.armourersWorkshop.api.common.event.AddEntityEquipmentEvent;
import riskyken.armourersWorkshop.api.common.event.GetEquipmentTypeEvent;
import riskyken.armourersWorkshop.api.common.event.RemoveEntityEquipmentEvent;

public class EquipmentManager {
    
    public static void addCustomEquipmentToEntity(Entity entity, ItemStack equipmentStack) {
        int equipmentId = EquipmentNBTHelper.getEquipmentIdFromStack(equipmentStack);
        EnumArmourType armourType = GetEquipmentTypeEvent.call(equipmentId);
        addCustomEquipmentToEntity(entity, armourType, equipmentId);
    }
    
    public static void addCustomEquipmentToEntity(Entity entity, EnumArmourType armourType, int equipmentId) {
        AddEntityEquipmentEvent.call(entity, armourType, equipmentId);
    }
    
    public static void removeCustomEquipmentFromEntity(Entity entity, EnumArmourType armourType) {
        RemoveEntityEquipmentEvent.call(entity, armourType);
    }
    
    public static void removeAllCustomEquipmentFromEntity(Entity entity) {
        RemoveEntityEquipmentEvent.call(entity, EnumArmourType.HEAD);
        RemoveEntityEquipmentEvent.call(entity, EnumArmourType.CHEST);
        RemoveEntityEquipmentEvent.call(entity, EnumArmourType.LEGS);
        RemoveEntityEquipmentEvent.call(entity, EnumArmourType.SKIRT);
        RemoveEntityEquipmentEvent.call(entity, EnumArmourType.FEET);
    }
}
