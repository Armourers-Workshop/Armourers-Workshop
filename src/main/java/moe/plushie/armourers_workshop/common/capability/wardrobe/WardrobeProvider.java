package moe.plushie.armourers_workshop.common.capability.wardrobe;

import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class WardrobeProvider implements ICapabilitySerializable<NBTTagCompound> {

    private final WardrobeCap wardrobeCapability;
    
    public WardrobeProvider(Entity entity, ISkinnableEntity skinnableEntity) {
        this.wardrobeCapability = new WardrobeCap(entity, skinnableEntity);
        if (entity instanceof EntityPlayer) {
            
        } else {
            
        }
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability != null && capability == WardrobeCap.WARDROBE_CAP;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (hasCapability(capability, facing)) {
            return WardrobeCap.WARDROBE_CAP.cast(wardrobeCapability);
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound) WardrobeCap.WARDROBE_CAP.getStorage().writeNBT(WardrobeCap.WARDROBE_CAP, wardrobeCapability, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        WardrobeCap.WARDROBE_CAP.getStorage().readNBT(WardrobeCap.WARDROBE_CAP, wardrobeCapability, null, nbt);
    }
}
