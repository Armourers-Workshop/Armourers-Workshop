package moe.plushie.armourers_workshop.api.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface IMenuScreenProvider<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {

    @Environment(EnvType.CLIENT)
    U getMenuScreen(T var1, Inventory var2, Component var3);
}
