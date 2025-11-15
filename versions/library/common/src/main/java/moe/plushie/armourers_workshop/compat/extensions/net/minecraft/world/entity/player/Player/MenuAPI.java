package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.player.Player;

import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.init.platform.MenuManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class MenuAPI {

    public static <T extends AbstractContainerMenu, V> OpenInteractionResult openMenu(@This Player player, IRegistryHolder<IMenuType<T>> menuType, V value) {
        return MenuManager.openMenu(menuType, player, value);
    }

    public static <T extends AbstractContainerMenu> OpenInteractionResult openMenu(@This Player player, IRegistryHolder<IMenuType<T>> menuType, Level level, BlockPos pos) {
        return MenuManager.openMenu(menuType, player, level, pos);
    }
}

