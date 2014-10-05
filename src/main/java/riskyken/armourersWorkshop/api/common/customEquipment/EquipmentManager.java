package riskyken.armourersWorkshop.api.common.customEquipment;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourType;
import riskyken.armourersWorkshop.api.common.event.AddEntityEquipmentEvent;
import riskyken.armourersWorkshop.api.common.event.GetEquipmentTypeEvent;
import riskyken.armourersWorkshop.api.common.event.RemoveEntityEquipmentEvent;

public class EquipmentManager {
    
    /**
     * Adds custom equipment data to an entity.
     * Note: addCustomEquipmentToEntity(Entity, EnumArmourType, int) is more performance friendly.
     * @param entity Entity to add the custom equipment to.
     * @param equipmentStack ItemStack that has the equipment id.
     */
    public static void addCustomEquipmentToEntity(Entity entity, ItemStack equipmentStack) {
        int equipmentId = EquipmentNBTHelper.getEquipmentIdFromStack(equipmentStack);
        EnumArmourType armourType = GetEquipmentTypeEvent.call(equipmentId);
        if (armourType != EnumArmourType.NONE) {
            addCustomEquipmentToEntity(entity, armourType, equipmentId);
        }
    }
    
    /**
     * Adds custom equipment data to an entity.
     * @param entity Entity to add the custom equipment to.
     * @param armourType Armour type to add.
     * @param equipmentId Equipment id to add.
     */
    public static void addCustomEquipmentToEntity(Entity entity, EnumArmourType armourType, int equipmentId) {
        AddEntityEquipmentEvent.call(entity, armourType, equipmentId);
    }
    
    /**
     * Removes custom equipment data from an entity.
     * @param entity Entity to remove the custom equipment from.
     * @param armourType The type of equipment to remove.
     */
    public static void removeCustomEquipmentFromEntity(Entity entity, EnumArmourType armourType) {
        RemoveEntityEquipmentEvent.call(entity, armourType);
    }
    
    /**
     * Removes all custom equipment data from an entity.
     * @param entity Entity to remove the custom equipment from.
     */
    public static void removeAllCustomEquipmentFromEntity(Entity entity) {
        RemoveEntityEquipmentEvent.call(entity, EnumArmourType.HEAD);
        RemoveEntityEquipmentEvent.call(entity, EnumArmourType.CHEST);
        RemoveEntityEquipmentEvent.call(entity, EnumArmourType.LEGS);
        RemoveEntityEquipmentEvent.call(entity, EnumArmourType.SKIRT);
        RemoveEntityEquipmentEvent.call(entity, EnumArmourType.FEET);
    }
}
