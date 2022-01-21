package moe.plushie.armourers_workshop.core.wardrobe;

import moe.plushie.armourers_workshop.core.utils.SkinCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SkinWardrobeProvider implements ICapabilityProvider, INBTSerializable<CompoundNBT> {

    public static final ResourceLocation WARDROBE_ID = new ResourceLocation(SkinCore.getModId(), "entity-skin-provider");

    @CapabilityInject(SkinWardrobe.class)
    public static Capability<SkinWardrobe> WARDROBE_KEY = null;

    public final SkinWardrobe wardrobe;

    public SkinWardrobeProvider(Entity entity) {
        this.wardrobe = new SkinWardrobe(entity);
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
