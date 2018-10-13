package moe.plushie.armourers_workshop.common.capability.wardrobe.player;

import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class PlayerWardrobeProvider implements ICapabilitySerializable<NBTTagCompound> {

    private final PlayerWardrobeCap wardrobeCapability;
    
    public PlayerWardrobeProvider(EntityPlayer entityPlayer, ISkinnableEntity skinnableEntity) {
        this.wardrobeCapability = new PlayerWardrobeCap(entityPlayer, skinnableEntity);
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability != null && capability == PlayerWardrobeCap.PLAYER_WARDROBE_CAP;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (hasCapability(capability, facing)) {
            return PlayerWardrobeCap.PLAYER_WARDROBE_CAP.cast(wardrobeCapability);
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound) PlayerWardrobeCap.PLAYER_WARDROBE_CAP.getStorage().writeNBT(PlayerWardrobeCap.PLAYER_WARDROBE_CAP, wardrobeCapability, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        PlayerWardrobeCap.PLAYER_WARDROBE_CAP.getStorage().readNBT(PlayerWardrobeCap.PLAYER_WARDROBE_CAP, wardrobeCapability, null, nbt);
    }
}
