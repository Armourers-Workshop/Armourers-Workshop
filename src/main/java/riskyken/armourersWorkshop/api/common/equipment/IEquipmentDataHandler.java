package riskyken.armourersWorkshop.api.common.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.equipment.armour.EnumArmourType;

public interface IEquipmentDataHandler {
    
    /**
     * Sets the IEntityEquipment data for an entity.
     * @param entity Entity to add the custom equipment to.
     * @param armourType Armour type to add.
     * @param equipmentId Equipment id to add.
     */
    public void setCustomEquipmentOnEntity(Entity entity, IEntityEquipment equipmentData);
    
    /**
     * Get the IEntityEquipment data for an entity.
     * @param entity
     * @return
     */
    public IEntityEquipment getCustomEquipmentForEntity(Entity entity);
    
    /**
     * Removes all custom equipment data from an entity.
     * @param entity Entity to remove the custom equipment from.
     */
    public void removeAllCustomEquipmentFromEntity(Entity entity);
    
    /**
     * Removes custom equipment data from an entity.
     * @param entity Entity to remove the custom equipment from.
     * @param armourType The type of equipment to remove.
     */
    public void removeCustomEquipmentFromEntity(Entity entity, EnumArmourType armourType);
    
    /**
     * Get the EnumArmourType for an equipment id.
     * @param equipmentId
     * @return The EnumArmourType for this equipment id.
     * Returns EnumArmourType.NONE if no armour exists for this id.
     */
    public EnumArmourType getEquipmentType(int equipmentId);
    
    /**
     * Get an item stack for the equipment id.
     * @param equipmentId
     * @return 
     */
    public ItemStack getCustomEquipmentItemStack(int equipmentId);
    
    /**
     * Get the IInventory that players custom equipment items are stored in.
     * Slot 0 = Head
     * Slot 1 = Chest
     * Slot 2 = Legs
     * Slot 3 = Skirt
     * Slot 4 = Feet
     * 
     * @param player Player to get the IInventory for.
     * @return IInventory of the player. Returns null if not found.
     */
    public IInventory getPlayersEquipmentInventory(EntityPlayer player);
}
