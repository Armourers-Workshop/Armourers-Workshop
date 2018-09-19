package moe.plushie.armourers_workshop.common.capability;

import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class EntitySkinProvider implements ICapabilitySerializable<NBTTagCompound> {
    
    private final EntitySkinCapability entitySkinCapability;
    
    public EntitySkinProvider(Entity entity, ISkinnableEntity skinnableEntity) {
        this.entitySkinCapability = new EntitySkinCapability(entity, skinnableEntity);
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability != null && capability == EntitySkinCapability.SKIN_CAP;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (hasCapability(capability, facing)) {
            return EntitySkinCapability.SKIN_CAP.cast(entitySkinCapability);
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound) EntitySkinCapability.SKIN_CAP.getStorage().writeNBT(EntitySkinCapability.SKIN_CAP, entitySkinCapability, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        EntitySkinCapability.SKIN_CAP.getStorage().readNBT(EntitySkinCapability.SKIN_CAP, entitySkinCapability, null, nbt);
    }
}
