package riskyken.armourersWorkshop.common.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class WardrobeStorage implements IStorage<IWardrobeCapability> {

    @Override
    public NBTBase writeNBT(Capability<IWardrobeCapability> capability, IWardrobeCapability instance, EnumFacing side) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void readNBT(Capability<IWardrobeCapability> capability, IWardrobeCapability instance, EnumFacing side,
            NBTBase nbt) {
        // TODO Auto-generated method stub
        
    }

}
