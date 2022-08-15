package moe.plushie.armourers_workshop.builder.blockentity;

import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import moe.plushie.armourers_workshop.core.blockentity.AbstractBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AdvancedSkinBuilderBlockEntity extends AbstractBlockEntity implements IBlockEntityHandler {

    public AdvancedSkinBuilderBlockEntity(BlockEntityType<?> entityType) {
        super(entityType);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
    }
}
