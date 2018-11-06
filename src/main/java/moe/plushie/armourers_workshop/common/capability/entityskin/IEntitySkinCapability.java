package moe.plushie.armourers_workshop.common.capability.entityskin;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.inventory.SkinInventoryContainer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IEntitySkinCapability {
    
    /**
     * 
     * @return
     */
    public ISkinType[] getValidSkinTypes();

    /**
     * Checks if the entity can hold this skin type.
     * 
     * @param skinType
     * @return
     */
    public boolean canHoldSkinType(ISkinType skinType);
    
    /**
     * Gets the number of this skin that the entity can hold.
     * 
     * @return
     */
    public int getSlotCountForSkinType(ISkinType skinType);
    
    /**
     * Get the skin descriptor for the ISkinType and slot index.
     * 
     * @param skinType
     * @param slotIndex
     * @return
     */
    public ISkinDescriptor getSkinDescriptor(ISkinType skinType, int slotIndex);
    
    /**
     * Sets the skin descriptor for the ISkinType and slot.
     * @param skinType
     * @param slotIndex
     * @param skinDescriptor
     * @return ISkinDescriptor that was in the slot. Returns input ISkinDescriptor on fail.
     */
    public ISkinDescriptor setSkinDescriptor(ISkinType skinType, int slotIndex, ISkinDescriptor skinDescriptor);
    
    /**
     * 
     * @param skinType
     * @param slotIndex
     * @return
     */
    public ItemStack getSkinStack(ISkinType skinType, int slotIndex);
    
    /**
     * 
     * @param skinType
     * @param slotIndex
     * @param skinStack
     * @return
     */
    public ItemStack setSkinStack(ISkinType skinType, int slotIndex, ItemStack skinStack);
    
    public boolean setStackInNextFreeSlot(ItemStack stack);
    
    //TODO Change this to use CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
    public SkinInventoryContainer getSkinInventoryContainer();
    
    public IInventory getInventoryOutfits();
    
    public void clear();
    
    /**
     * Syncs capability data to a player.
     * 
     * @param entityPlayer Player to sync to.
     */
    public void syncToPlayer(EntityPlayerMP entityPlayer);
    
    /**
     * Syncs capability data to all players tracking the entity.
     */
    public void syncToAllTracking();
}
