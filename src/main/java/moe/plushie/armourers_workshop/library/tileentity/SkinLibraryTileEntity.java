package moe.plushie.armourers_workshop.library.tileentity;

import moe.plushie.armourers_workshop.core.tileentity.AbstractContainerTileEntity;
import moe.plushie.armourers_workshop.init.common.ModTileEntities;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public class SkinLibraryTileEntity extends AbstractContainerTileEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    public SkinLibraryTileEntity() {
        super(ModTileEntities.SKIN_LIBRARY);
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
        return 2;
    }

}
