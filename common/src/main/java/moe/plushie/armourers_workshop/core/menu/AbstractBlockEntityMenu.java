package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractBlockEntityMenu<T extends BlockEntity> extends AbstractBlockMenu {

    protected final T blockEntity;

    public AbstractBlockEntityMenu(MenuType menuType, Block block, int containerId, IGlobalPos access) {
        super(menuType, block, containerId, access);
        this.blockEntity = ObjectUtils.unsafeCast(access.evaluate(Level::getBlockEntity, null));
    }

    public T getBlockEntity() {
        return blockEntity;
    }

//    public Container getBlockInventory() {
//        return ((IHasInventory) getBlockEntity()).getInventory();
//    }
}
