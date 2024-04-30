package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.world.level.block.entity.BlockEntity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.21, )")
public class StorageAPI {

    public static CompoundTag saveFullData(@This BlockEntity blockEntity, RegistryAccess registryAccess) {
        return blockEntity.saveWithFullMetadata(registryAccess);
    }

    public static void loadFullData(@This BlockEntity blockEntity, CompoundTag tag, RegistryAccess registryAccess) {
        blockEntity.loadWithComponents(tag, registryAccess);
    }
}
