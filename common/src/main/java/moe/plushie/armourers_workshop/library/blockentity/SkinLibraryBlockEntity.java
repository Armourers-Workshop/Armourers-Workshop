package moe.plushie.armourers_workshop.library.blockentity;

import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import moe.plushie.armourers_workshop.core.blockentity.UpdatableContainerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SkinLibraryBlockEntity extends UpdatableContainerBlockEntity {

    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    public SkinLibraryBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void readAdditionalData(IDataSerializer serializer) {
        serializer.readItemList(items);
    }

    @Override
    public void writeAdditionalData(IDataSerializer serializer) {
        serializer.writeItemList(items);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

}
