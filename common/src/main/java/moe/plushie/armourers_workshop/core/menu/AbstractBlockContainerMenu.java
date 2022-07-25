package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.api.common.IHasInventory;
import moe.plushie.armourers_workshop.api.registry.IRegistryObject;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBlockContainerMenu extends AbstractContainerMenu {

    protected final Block block;
    protected final ContainerLevelAccess access;

    public <C extends net.minecraft.world.inventory.AbstractContainerMenu> AbstractBlockContainerMenu(int containerId, IRegistryObject<MenuType<C>> containerType, IRegistryObject<Block> block, ContainerLevelAccess access) {
        super(containerType.get(), containerId);
        this.access = access;
        this.block = block.get();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, this.block);
    }

    @Nullable
    public <T extends BlockEntity> T getTileEntity(Class<T> clazz) {
        BlockEntity[] tileEntities = {null};
        access.execute((world, pos) -> tileEntities[0] = world.getBlockEntity(pos));
        if (clazz.isInstance(tileEntities[0])) {
            return ObjectUtils.unsafeCast(tileEntities[0]);
        }
        return null;
    }

    @Nullable
    public Container getTileInventory() {
        BlockEntity tileEntity = getTileEntity(BlockEntity.class);
        if (tileEntity instanceof IHasInventory) {
            return ((IHasInventory) tileEntity).getInventory();
        }
        return null;
    }
}