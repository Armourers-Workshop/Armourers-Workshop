package moe.plushie.armourers_workshop.common.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class EntitySkinStorage implements IStorage<IEntitySkinCapability> {

    @Override
    public NBTBase writeNBT(Capability<IEntitySkinCapability> capability, IEntitySkinCapability instance, EnumFacing side) {
        // TODO Auto-generated method stub
        return new NBTTagCompound();
    }

    @Override
    public void readNBT(Capability<IEntitySkinCapability> capability, IEntitySkinCapability instance, EnumFacing side, NBTBase nbt) {
        // TODO Auto-generated method stub
        
    }
}
