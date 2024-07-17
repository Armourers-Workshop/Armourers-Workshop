package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import moe.plushie.armourers_workshop.utils.NonNullItemList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DyeTableBlockEntity extends UpdatableContainerBlockEntity {

    private final NonNullItemList items = new NonNullItemList(10);

    public DyeTableBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void readAdditionalData(IDataSerializer serializer) {
        items.deserialize(serializer);
    }

    @Override
    public void writeAdditionalData(IDataSerializer serializer) {
        items.serialize(serializer);
    }

    @Override
    protected NonNullItemList getItems() {
        return items;
    }

    @Override
    public int getContainerSize() {
        return 10;
    }
}
