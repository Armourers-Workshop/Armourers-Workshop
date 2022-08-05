package moe.plushie.armourers_workshop.library.blockentity;

import moe.plushie.armourers_workshop.core.blockentity.AbstractBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class GlobalSkinLibraryBlockEntity extends AbstractBlockEntity {

    public GlobalSkinLibraryBlockEntity(BlockEntityType<?> entityType) {
        super(entityType);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
    }
}
