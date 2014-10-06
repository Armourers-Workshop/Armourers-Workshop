package riskyken.armourersWorkshop.api.common.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public interface IEquipmentDataHandler {
    
    /**
     * Adds custom equipment data to an entity.
     * @param entity Entity to add the custom equipment to.
     * @param armourType Armour type to add.
     * @param equipmentId Equipment id to add.
     */
    public void setCustomEquipmentOnEntity(Entity entity, IEntityEquipment equipmentData);
    
    /**
     * 
     * @param entity
     * @return
     */
    public IEntityEquipment getCustomEquipmentForEntity(Entity entity);
    
    /**
     * Removes custom equipment data from an entity.
     * @param entity Entity to remove the custom equipment from.
     * @param armourType The type of equipment to remove.
     */
    public void removeCustomEquipmentFromEntity(Entity entity);
    
    public void setCustomEquipmentOnPlayer(EntityPlayer player, IEntityEquipment equipmentData);
    
    public IEntityEquipment getCustomEquipmentForPlayer(EntityPlayer player);
    
    public void removeCustomEquipmentFromPlayer(EntityPlayer player);
}
