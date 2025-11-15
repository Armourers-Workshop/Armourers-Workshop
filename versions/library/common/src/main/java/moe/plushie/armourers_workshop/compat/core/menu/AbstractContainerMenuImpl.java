package moe.plushie.armourers_workshop.compat.core.menu;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Supplier;

@Available("[1.18, )")
public abstract class AbstractContainerMenuImpl extends AbstractContainerMenu {

    public AbstractContainerMenuImpl(IMenuType<?> menuType, int containerId) {
        super(Objects.flatMap(menuType, Supplier::get), containerId);
    }

    @Override
    protected void clearContainer(Player player, Container container) {
        super.clearContainer(player, container);
    }
}
