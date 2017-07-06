package riskyken.armourersWorkshop.api.common.skin;

import java.io.InputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;

public interface ISkinDataHandler {
        
    /** Set a skin in the players wardrobe.
     * 
     * @param player Target player.
     * @param stack Stack with skin.
     * @param index Index for this skin type.
     */
    public void setSkinOnPlayer(EntityPlayer player, ItemStack stack, int index);
    
    /**
     * Set a skin in the players wardrobe.
     * 
     * @Deprecated Use the {@link #setSkinOnPlayer(EntityPlayer, ItemStack, Int)} method.
     * @return Deprecated always returns false.
     */
    @Deprecated
    public boolean setSkinOnPlayer(EntityPlayer player, ItemStack stack);
    
    public ItemStack getSkinFormPlayer(EntityPlayer player, ISkinType skinType, int index);
    
    @Deprecated
    public ItemStack getSkinFormPlayer(EntityPlayer player, ISkinType skinType);
    
    public void removeSkinFromPlayer(EntityPlayer player, ISkinType skinType, int index);
    
    @Deprecated
    public void removeSkinFromPlayer(EntityPlayer player, ISkinType skinType);
    
    public boolean isValidEquipmentSkin(ItemStack stack);
    
    public boolean stackHasSkinPointer(ItemStack stack);
    
    public ISkinPointer getSkinPointerFromStack(ItemStack stack);
    
    public void saveSkinPointerOnStack(ISkinPointer skinPointer, ItemStack stack);
    
    public boolean compoundHasSkinPointer(NBTTagCompound compound);
    
    public ISkinPointer readSkinPointerFromCompound(NBTTagCompound compound);
    
    public void writeSkinPointerToCompound(ISkinPointer skinPointer, NBTTagCompound compound);
    
    public ISkinPointer addSkinToCache(InputStream inputStream);
    
    /**
     * Checks if the armour render has been overridden for this slot.
     * @param player
     * @param slotId
     * @return
     */
    public boolean isArmourRenderOverridden(EntityPlayer player, int slotId);
    
    public void setItemAsSkinnable(Item item);
}
