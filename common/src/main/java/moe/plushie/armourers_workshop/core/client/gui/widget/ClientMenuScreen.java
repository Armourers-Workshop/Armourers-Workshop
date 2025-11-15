package moe.plushie.armourers_workshop.core.client.gui.widget;

import moe.plushie.armourers_workshop.compat.client.gui.event.AbstractMouseButtonEvent;
import moe.plushie.armourers_workshop.compat.client.gui.event.AbstractMouseEvent;
import moe.plushie.armourers_workshop.core.menu.ContainerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class ClientMenuScreen extends ContainerMenuScreen<ContainerMenu, MenuWindow<ContainerMenu>> {

    public ClientMenuScreen(MenuWindow<ContainerMenu> window, Component title) {
        super(window, window.menu, window.inventory, title);
    }

    public static Inventory createEmptyInventory() {
        var player = Minecraft.getInstance().player;
        if (player != null) {
            return player.getInventory();
        }
        return null;
    }

    public static ContainerMenu createEmptyMenu() {
        return new ContainerMenu(null, 0) {
            @Override
            protected boolean abi$stillValid(Player player) {
                return false;
            }
        };
    }


    @Override
    protected boolean _mouseClicked(AbstractMouseButtonEvent event, boolean bl) {
        return false;
    }

    @Override
    protected boolean _mouseMoved(AbstractMouseEvent event) {
        return false;
    }

    @Override
    protected boolean _mouseReleased(AbstractMouseButtonEvent event) {
        return false;
    }
}
