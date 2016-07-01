package riskyken.armourersWorkshop.common.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class WardrobeProvider implements ICapabilitySerializable, IWardrobeCapability {

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NBTBase serializeNBT() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        // TODO Auto-generated method stub
        
    }

}
