package moe.plushie.armourers_workshop.api.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface ISkinInventoryContainer {
    
    public void writeToNBT(NBTTagCompound compound);
    
    public void readFromNBT(NBTTagCompound compound);
    
    public void dropItems(EntityPlayer player);

    public void clear();
}
