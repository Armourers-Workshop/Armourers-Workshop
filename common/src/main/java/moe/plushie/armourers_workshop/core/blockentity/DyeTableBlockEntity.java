package moe.plushie.armourers_workshop.core.blockentity;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class DyeTableBlockEntity extends AbstractContainerBlockEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(10, ItemStack.EMPTY);

    public DyeTableBlockEntity(BlockEntityType<?> entityType) {
        super(entityType);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        ContainerHelper.loadAllItems(nbt, items);
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        ContainerHelper.saveAllItems(nbt, items);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return 10;
    }
}