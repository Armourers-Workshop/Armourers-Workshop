package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;

public abstract class AbstractBlockMenu extends AbstractContainerMenu {

    protected final Block block;
    protected final IGlobalPos access;

    public AbstractBlockMenu(MenuType<?> menuType, Block block, int containerId, IGlobalPos access) {
        super(menuType, containerId);
        this.access = access;
        this.block = block;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access::evaluate, player, block);
    }
}
