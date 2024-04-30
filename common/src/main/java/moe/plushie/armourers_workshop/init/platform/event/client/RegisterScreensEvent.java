package moe.plushie.armourers_workshop.init.platform.event.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public interface RegisterScreensEvent {

    <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> arg, Factory<M, U> arg2);

    interface Factory<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {

        U create(T abstractContainerMenu, Inventory inventory, Component component);
    }
}
