package moe.plushie.armourers_workshop.api.common;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface IMenuType<C extends AbstractContainerMenu> {

    <T> InteractionResult openMenu(Player player, T value);

    Component getTitle();

    ResourceLocation getRegistryName();
}
