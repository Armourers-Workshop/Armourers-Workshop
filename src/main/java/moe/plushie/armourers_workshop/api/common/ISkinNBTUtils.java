package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface ISkinNBTUtils {
    
    // Item stack.
    public void setSkinDescriptor(ItemStack itemStack, ISkinDescriptor skinDescriptor);
    
    public ISkinDescriptor getSkinDescriptor(ItemStack itemStack);
    
    public void removeSkinDescriptor(ItemStack itemStack);
    
    public boolean hasSkinDescriptor(ItemStack itemStack);
    
    // Tag compound.
    public void setSkinDescriptor(NBTTagCompound compound, ISkinDescriptor skinDescriptor);
    
    public ISkinDescriptor getSkinDescriptor(NBTTagCompound compound);
    
    public void removeSkinDescriptor(NBTTagCompound compound);
    
    public boolean hasSkinDescriptor(NBTTagCompound compound);
}
