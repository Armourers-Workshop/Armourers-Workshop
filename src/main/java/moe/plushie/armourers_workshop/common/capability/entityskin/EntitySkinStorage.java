package moe.plushie.armourers_workshop.common.capability.entityskin;

import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class EntitySkinStorage implements IStorage<IEntitySkinCapability> {

    @Override
    public NBTBase writeNBT(Capability<IEntitySkinCapability> capability, IEntitySkinCapability instance, EnumFacing side) {
        NBTTagCompound compound = new NBTTagCompound();
        instance.getSkinInventoryContainer().writeToNBT(compound);
        return compound;
    }

    @Override
    public void readNBT(Capability<IEntitySkinCapability> capability, IEntitySkinCapability instance, EnumFacing side, NBTBase nbt) {
        NBTTagCompound compound = (NBTTagCompound) nbt;
        instance.getSkinInventoryContainer().readFromNBT(compound);
    }
}
