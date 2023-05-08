package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.api.common.IContainerLevelAccess;
import moe.plushie.armourers_workshop.api.common.IHasInventory;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBlockContainerMenu extends AbstractContainerMenu {

    protected final Block block;
    protected final IContainerLevelAccess access;

    public AbstractBlockContainerMenu(MenuType<?> menuType, Block block, int containerId, IContainerLevelAccess access) {
        super(menuType, containerId);
        this.access = access;
        this.block = block;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, this.block);
    }

    @Nullable
    public <T extends BlockEntity> T getBlockEntity(Class<T> clazz) {
        BlockEntity[] blockEntities = {null};
        access.execute((level, pos) -> blockEntities[0] = level.getBlockEntity(pos));
        return ObjectUtils.safeCast(blockEntities[0], clazz);
    }

    public <T extends BlockEntity> T getBlockEntity() {
        BlockEntity[] blockEntities = {null};
        access.execute((level, pos) -> blockEntities[0] = level.getBlockEntity(pos));
        return ObjectUtils.unsafeCast(blockEntities[0]);
    }

    public Container getBlockInventory() {
        return ((IHasInventory) getBlockEntity()).getInventory();
    }
}
