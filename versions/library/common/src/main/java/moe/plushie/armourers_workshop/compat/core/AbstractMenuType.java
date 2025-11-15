package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class AbstractMenuType<C extends AbstractContainerMenu> implements IMenuType<C> {

    public abstract <T> OpenInteractionResult openMenu(ServerPlayer player, Component title, T value);
}
