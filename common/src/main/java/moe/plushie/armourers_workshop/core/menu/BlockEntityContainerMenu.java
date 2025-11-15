package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BlockEntityContainerMenu<T extends BlockEntity> extends BlockContainerMenu {

    protected final T blockEntity;

    public BlockEntityContainerMenu(IMenuType<?> menuType, Block block, int containerId, IGlobalPos access) {
        super(menuType, block, containerId, access);
        this.blockEntity = Objects.unsafeCast(access.evaluate(Level::getBlockEntity, null));
    }

    public T getBlockEntity() {
        return blockEntity;
    }
}
