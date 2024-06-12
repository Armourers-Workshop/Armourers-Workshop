package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.registry.IRegistryEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface IMenuType<C extends AbstractContainerMenu> extends IRegistryEntry {

    <T> InteractionResult openMenu(Player player, T value);

    Component getTitle();
}
