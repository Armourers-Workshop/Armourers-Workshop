package moe.plushie.armourers_workshop.compat.core.menu;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Available("[16, )")
public abstract class AbstractContainerMenu extends AbstractContainerMenuImpl {

    public AbstractContainerMenu(IMenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    protected void abi$broadcastChanges() {
        super.broadcastChanges();
    }

    protected void abi$removed(Player player) {
        super.removed(player);
    }

    protected void abi$clearContainer(Player player, Container container) {
        super.clearContainer(player, container);
    }

    protected abstract ItemStack abi$quickMoveStack(Player player, int i);

    protected abstract boolean abi$stillValid(Player player);

    @Override
    public final void broadcastChanges() {
        abi$broadcastChanges();
    }

    @Override
    public final void removed(Player player) {
        abi$removed(player);
    }

    @Override
    public final void clearContainer(Player player, Container container) {
        abi$clearContainer(player, container);
    }

    @Override
    public final boolean stillValid(Player player) {
        return abi$stillValid(player);
    }

    @Override
    public final ItemStack quickMoveStack(Player player, int i) {
        return abi$quickMoveStack(player, i);
    }
}
