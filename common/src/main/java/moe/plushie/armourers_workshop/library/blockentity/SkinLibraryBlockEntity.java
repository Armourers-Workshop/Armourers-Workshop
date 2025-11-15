package moe.plushie.armourers_workshop.library.blockentity;

import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.core.blockentity.UpdatableContainerBlockEntity;
import moe.plushie.armourers_workshop.core.data.SimpleContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SkinLibraryBlockEntity extends UpdatableContainerBlockEntity {

    private final SimpleContainer container = new SimpleContainer(2);

    public SkinLibraryBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    protected void abi$readAdditionalData(IDataSerializer serializer) {
        container.deserialize(serializer);
    }

    @Override
    protected void abi$writeAdditionalData(IDataSerializer serializer) {
        container.serialize(serializer);
    }

    @Override
    protected SimpleContainer getContainer() {
        return container;
    }
}
