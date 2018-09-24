package moe.plushie.armourers_workshop.common.capability.wardrobe;

import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class WardrobeProvider implements ICapabilitySerializable<NBTTagCompound> {

    private final WardrobeCapability wardrobeCapability;
    
    public WardrobeProvider(EntityPlayer entityPlayer, ISkinnableEntity skinnableEntity) {
        this.wardrobeCapability = new WardrobeCapability(entityPlayer, skinnableEntity);
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability != null && capability == WardrobeCapability.WARDROBE_CAP;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (hasCapability(capability, facing)) {
            return WardrobeCapability.WARDROBE_CAP.cast(wardrobeCapability);
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound) WardrobeCapability.WARDROBE_CAP.getStorage().writeNBT(WardrobeCapability.WARDROBE_CAP, wardrobeCapability, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        WardrobeCapability.WARDROBE_CAP.getStorage().readNBT(WardrobeCapability.WARDROBE_CAP, wardrobeCapability, null, nbt);
    }
}
