package riskyken.armourersWorkshop.api.common.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IEquipmentDataHandler {
    
    /**
     * Sets the IEntityEquipment data for an entity.
     * Note: If the entity is a player this will not update their
     * IInventory holding their custom equipment item stacks
     * @param entity Entity to add the custom equipment to.
     * @param armourType Armour type to add.
     * @param equipmentId Equipment id to add.
     */
    public void setCustomEquipmentOnEntity(Entity entity, IEntityEquipment equipmentData);
    
    /**
     * Get the IEntityEquipment data for an entity.
     * @param entity
     * @return IEntityEquipment
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
    public void removeCustomEquipmentFromEntity(Entity entity, EnumEquipmentType armourType);
    
    /** 
     * Get the EnumArmourType for an equipment id.
     * @param equipmentId
     * @return The EnumArmourType for this equipment id.
     * Returns EnumArmourType.NONE if no armour exists for this id.
     */
    public EnumEquipmentType getEquipmentType(int equipmentId);
    
    /**
     * Get an item stack for the equipment id.
     * @param equipmentId
     * @return 
     */
    public ItemStack getCustomEquipmentItemStack(int equipmentId);
    
    /**
     * Checks if an item stack has equipment data.
     * @param stack
     * @return True/False Has the item stack got Equipment data?
     */
    public boolean hasItemStackGotEquipmentData(ItemStack stack);
    
    public ItemStack getEquipmentStackFromEntity(Entity entity, EnumEquipmentType armourType);
    
    public void setEquipmentStackOnEntity(Entity entity, EnumEquipmentType armourType, ItemStack stack);
    
    /**
     * Get the equipment id from an item stack.
     * @param stack
     * @return Equipment ID
     */
    public int getEquipmentIdFromItemStack(ItemStack stack);
    
    /**
     * Get the IInventory that players custom equipment items are stored in.</BR>
     * </BR>
     * Slot 0 = Head </BR>
     * Slot 1 = Chest</BR>
     * Slot 2 = Legs</BR>
     * Slot 3 = Skirt</BR>
     * Slot 4 = Feet</BR>
     * </BR>
     * Note: Changing this IInventory will also update the players IEntityEquipment data.
     * 
     * @param player Player to get the IInventory for.
     * @return IInventory of the player. Returns null if not found.
     */
    public IInventory getPlayersEquipmentInventory(EntityPlayer player);
}
