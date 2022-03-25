package moe.plushie.armourers_workshop.api.common;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public interface ISkinInventoryContainer {

    // TODO: Refactor
//    public void writeToNBT(NBTTagCompound compound);
//
//    public void readFromNBT(NBTTagCompound compound);
    
    public void dropItems(World world, Vector3d pos);

    public void clear();
}
