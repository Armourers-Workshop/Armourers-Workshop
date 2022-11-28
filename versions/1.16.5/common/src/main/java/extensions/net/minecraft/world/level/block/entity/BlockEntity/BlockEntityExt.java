package extensions.net.minecraft.world.level.block.entity.BlockEntity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

@Extension
public class BlockEntityExt {

    public static CompoundTag saveWithFullMetadata(@This BlockEntity blockEntity) {
        return blockEntity.save(new CompoundTag());
    }

    public static void load(@This BlockEntity blockEntity, CompoundTag tag) {
        blockEntity.load(blockEntity.getBlockState(), tag);
    }
}
