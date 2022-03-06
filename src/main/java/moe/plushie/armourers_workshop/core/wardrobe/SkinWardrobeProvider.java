package moe.plushie.armourers_workshop.core.wardrobe;

import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SkinWardrobeProvider implements ICapabilityProvider, INBTSerializable<CompoundNBT> {

    public static final ResourceLocation WARDROBE_ID = new ResourceLocation(AWCore.getModId(), "entity-skin-provider");

    @CapabilityInject(SkinWardrobe.class)
    public static Capability<SkinWardrobe> WARDROBE_KEY = null;

    public final SkinWardrobe wardrobe;

    public SkinWardrobeProvider(Entity entity, EntityProfile profile) {
        this.wardrobe = new SkinWardrobe(entity, profile);
    }

    @Nullable
    public static SkinWardrobe by(PlayerEntity player, PacketBuffer buffer) {
        return SkinWardrobe.of(player.level.getEntity(buffer.readInt()));
    }

    public static void to(SkinWardrobe wardrobe, PacketBuffer buffer) {
        buffer.writeInt(wardrobe.getId());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return WARDROBE_KEY.orEmpty(cap, LazyOptional.of(() -> wardrobe));
    }

    @Override
    public CompoundNBT serializeNBT() {
        return wardrobe.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        wardrobe.deserializeNBT(nbt);
    }
}
