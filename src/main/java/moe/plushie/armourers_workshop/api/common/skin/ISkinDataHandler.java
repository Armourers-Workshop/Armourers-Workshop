package moe.plushie.armourers_workshop.api.common.skin;

import java.io.InputStream;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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
     * @Deprecated Use the {@link #setSkinOnPlayer(EntityPlayer, ItemStack, int)} method.
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
    
    public ISkinDescriptor getSkinPointerFromStack(ItemStack stack);
    
    public void saveSkinPointerOnStack(ISkinDescriptor skinPointer, ItemStack stack);
    
    public boolean compoundHasSkinPointer(NBTTagCompound compound);
    
    public ISkinDescriptor readSkinPointerFromCompound(NBTTagCompound compound);
    
    public void writeSkinPointerToCompound(ISkinDescriptor skinPointer, NBTTagCompound compound);
    
    public ISkinDescriptor addSkinToCache(InputStream inputStream);
    
    /**
     * Checks if the armour render has been overridden for this slot.
     * @param player
     * @param slotId
     * @return
     */
    public boolean isArmourRenderOverridden(EntityPlayer player, int slotId);
    
    public void setItemAsSkinnable(Item item);
}
