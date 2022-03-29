package moe.plushie.armourers_workshop.core.tileentity;

import moe.plushie.armourers_workshop.init.common.ModTileEntities;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

@SuppressWarnings("NullableProblems")
public class DyeTableTileEntity extends AbstractContainerTileEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(10, ItemStack.EMPTY);

    public DyeTableTileEntity() {
        super(ModTileEntities.DYE_TABLE);
    }

    @Override
    public void readFromNBT(CompoundNBT nbt) {
        ItemStackHelper.loadAllItems(nbt, items);
    }

    @Override
    public void writeToNBT(CompoundNBT nbt) {
        ItemStackHelper.saveAllItems(nbt, items);
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
