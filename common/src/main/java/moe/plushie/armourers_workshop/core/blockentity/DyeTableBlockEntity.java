package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.init.ModBlockEntities;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class DyeTableBlockEntity extends AbstractContainerBlockEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(10, ItemStack.EMPTY);

    public DyeTableBlockEntity() {
        super(ModBlockEntities.DYE_TABLE.get());
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        DataSerializers.loadAllItems(nbt, items);
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        DataSerializers.saveAllItems(nbt, items);
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
