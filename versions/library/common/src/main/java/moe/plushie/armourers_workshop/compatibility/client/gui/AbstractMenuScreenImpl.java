package moe.plushie.armourers_workshop.compatibility.client.gui;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Available("[1.18, 1.19.4)")
@Environment(value = EnvType.CLIENT)
public abstract class AbstractMenuScreenImpl<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public AbstractMenuScreenImpl(T menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }
}
