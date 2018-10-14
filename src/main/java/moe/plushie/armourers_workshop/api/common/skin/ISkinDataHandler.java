package moe.plushie.armourers_workshop.api.common.skin;

import java.io.InputStream;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface ISkinDataHandler {
        
    /** Set a skin in the entity wardrobe.
     * 
     * @param entityLivingBase Target entity.
     * @param stack Stack with skin.
     * @param index Index for this skin type.
     */
    public void setEntitySkin(EntityLivingBase entityLivingBase, ItemStack stack, int index);
    
    public ItemStack getEntitySkin(EntityLivingBase entityLivingBase, ISkinType skinType, int index);
    
    public void removeEntitySkin(EntityLivingBase entityLivingBase, ISkinType skinType, int index);
    
    public boolean isValidSkin(ItemStack stack);
    
    public boolean stackHasSkinDescriptor(ItemStack stack);
    
    public ISkinDescriptor getSkinDescriptorFromStack(ItemStack stack);
    
    public void saveSkinDescriptorOnStack(ISkinDescriptor skinDescriptor, ItemStack stack);
    
    public boolean compoundHasSkinDescriptor(NBTTagCompound compound);
    
    public ISkinDescriptor readSkinDescriptorFromCompound(NBTTagCompound compound);
    
    public void writeSkinDescriptorToCompound(ISkinDescriptor skinDescriptor, NBTTagCompound compound);
    
    public ISkinDescriptor addSkinToCache(InputStream inputStream);
    
    /**
     * Checks if the armour render has been overridden for this slot.
     * @param player
     * @param slotId
     * @return
     */
    public boolean isArmourRenderOverridden(EntityPlayer player, EntityEquipmentSlot equipmentSlot);
}
