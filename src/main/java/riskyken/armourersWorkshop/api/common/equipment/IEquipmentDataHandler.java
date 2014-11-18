package riskyken.armourersWorkshop.api.common.equipment;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IEquipmentDataHandler {
    
    /**
     * Sets the equipment stack for a player.
     * @param player Entity to add the custom equipment to.
     * @param stack Armour type to add.
     */
    public void setCustomEquipmentOnPlayer(EntityPlayer player, ItemStack stack);
    
    /**
     * Get the equipment stacks for a player.
     * @param player
     * @return IEntityEquipment
     */
    public ItemStack[] getAllCustomEquipmentForPlayer(EntityPlayer player);
    
    /**
     * Get the IEntityEquipment data for a player.
     * @param entity
     * @return IEntityEquipment
     */
    public ItemStack getCustomEquipmentForPlayer(EntityPlayer player, EnumEquipmentType equipmentType);
    
    /**
     * Removes all custom equipment data from a player.
     * @param entity Entity to remove the custom equipment from.
     */
    public void clearAllCustomEquipmentFromPlayer(EntityPlayer player);
    
    /**
     * Removes custom equipment data from a player.
     * @param entity Entity to remove the custom equipment from.
     * @param armourType The type of equipment to remove.
     */
    public void clearCustomEquipmentFromPlayer(EntityPlayer player, EnumEquipmentType equipmentType);
    
    /** 
     * Get the EnumArmourType for an item stack.
     * @param stack
     * @return The EnumArmourType for this equipment id.
     * Returns EnumArmourType.NONE if no armour exists for this item stack.
     */
    public EnumEquipmentType getEquipmentTypeFromStack(ItemStack stack);
    
    /**
     * Checks if an item stack has equipment data.
     * @param stack
     * @return True/False Has the item stack got Equipment data?
     */
    public boolean hasItemStackGotEquipmentData(ItemStack stack);
    
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
     * 
     * @param player Player to get the IInventory for.
     * @return IInventory of the player. Returns null if not found.
     */
    public IInventory getPlayersEquipmentInventory(EntityPlayer player);
    
    /**
     * Checks if the armour render has been overridden for this slot.
     * @param player
     * @param slotId
     * @return
     */
    public boolean isArmourRenderOverridden(EntityPlayer player, int slotId);
}
