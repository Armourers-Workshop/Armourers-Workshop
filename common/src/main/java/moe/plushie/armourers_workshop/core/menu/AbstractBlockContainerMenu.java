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
    public <T extends BlockEntity> T getTileEntity(Class<T> clazz) {
        BlockEntity[] tileEntities = {null};
        access.execute((level, pos) -> tileEntities[0] = level.getBlockEntity(pos));
        if (clazz.isInstance(tileEntities[0])) {
            return ObjectUtils.unsafeCast(tileEntities[0]);
        }
        return null;
    }

    public <T extends BlockEntity> T getTileEntity() {
        BlockEntity[] tileEntities = {null};
        access.execute((level, pos) -> tileEntities[0] = level.getBlockEntity(pos));
        return ObjectUtils.unsafeCast(tileEntities[0]);
    }

    public Container getTileInventory() {
        return ((IHasInventory) getTileEntity()).getInventory();
    }
}
