package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.api.common.IHasInventory;
import moe.plushie.armourers_workshop.core.data.SimpleContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class UpdatableContainerBlockEntity extends UpdatableBlockEntity implements Container, IHasInventory {

    public UpdatableContainerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public boolean isEmpty() {
        return getContainer().isEmpty();
    }

    @Override
    public ItemStack getItem(int i) {
        return getContainer().getItem(i);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        var itemStack = getContainer().removeItem(i, j);
        if (!itemStack.isEmpty()) {
            setContainerChanged();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return getContainer().removeItemNoUpdate(i);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        getContainer().setItem(i, itemStack);
        setContainerChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        var level = getLevel();
        if (level == null) {
            return false;
        }
        var pos = getBlockPos();
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity != this) {
            return false;
        }
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        getContainer().clearContent();
        setContainerChanged();
    }

    @Override
    public int getContainerSize() {
        return getContainer().getContainerSize();
    }

    protected abstract SimpleContainer getContainer();

    protected void setContainerChanged() {
        setChanged();
    }

    @Override
    public Container getInventory() {
        return this;
    }
}
