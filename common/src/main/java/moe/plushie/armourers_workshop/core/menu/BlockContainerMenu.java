package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

public abstract class BlockContainerMenu extends ContainerMenu {

    protected final Block block;
    protected final IGlobalPos access;

    public BlockContainerMenu(IMenuType<?> menuType, Block block, int containerId, IGlobalPos access) {
        super(menuType, containerId);
        this.access = access;
        this.block = block;
    }

    @Override
    protected boolean abi$stillValid(Player player) {
        return stillValid(access::evaluate, player, block);
    }
}
