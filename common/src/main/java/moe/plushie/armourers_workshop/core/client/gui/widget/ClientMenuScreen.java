package moe.plushie.armourers_workshop.core.client.gui.widget;

import moe.plushie.armourers_workshop.core.menu.AbstractContainerMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

@Environment(EnvType.CLIENT)
public class ClientMenuScreen extends ContainerMenuScreen<AbstractContainerMenu, MenuWindow<AbstractContainerMenu>> {

    public ClientMenuScreen(MenuWindow<AbstractContainerMenu> window, Component title) {
        super(window, window.menu, window.inventory, title);
    }

    public static Inventory getEmptyInventory() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            return player.getInventory();
        }
        return null;
    }

    public static AbstractContainerMenu getEmptyMenu() {
        return new AbstractContainerMenu(null, 0) {
            @Override
            public boolean stillValid(Player player) {
                return false;
            }
        };
    }


    @Override
    protected boolean _mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    protected boolean _mouseMoved(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    protected boolean _mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }
}
