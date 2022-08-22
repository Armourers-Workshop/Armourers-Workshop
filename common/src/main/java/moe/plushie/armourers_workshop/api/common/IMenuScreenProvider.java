package moe.plushie.armourers_workshop.api.common;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface IMenuScreenProvider<T extends AbstractContainerMenu, S> {

    @NotNull
    S createMenuScreen(T var1, Inventory var2, Component var3);
}
