package moe.plushie.armourers_workshop.core.client.gui;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.core.client.gui.widget.ClientMenuScreen;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.NotificationDialog;
import moe.plushie.armourers_workshop.core.menu.ContainerMenu;
import moe.plushie.armourers_workshop.core.network.ExecuteAlertPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ServerAlertWindow extends MenuWindow<ContainerMenu> {

    private final ExecuteAlertPacket alertPacket;

    public ServerAlertWindow(ExecuteAlertPacket alertPacket) {
        super(ClientMenuScreen.createEmptyMenu(), ClientMenuScreen.createEmptyInventory(), new NSString(alertPacket.title()));
        this.titleView.removeFromSuperview();
        this.inventoryView.removeFromSuperview();
        this.alertPacket = alertPacket;
    }

    @Override
    public void init() {
        var dialog = new NotificationDialog();
        dialog.setTitle(new NSString(alertPacket.title()));
        dialog.setMessage(new NSString(alertPacket.message()));
        if (alertPacket.type() == 1) {
            dialog.setMessageColor(UIColor.of(0xffff5555));
        }
        dialog.setConfirmText(new NSString(alertPacket.confirm()));
        dialog.sizeToFit();
        dialog.showInView(this, this::dismiss);
    }

    public void showInScreen() {
        var minecraft = Minecraft.getInstance();
        var screen = new WrappedScreen(this, alertPacket.title());
        screen.setTarget(minecraft.screen);
        // we must the screen before set, otherwise it will cause the previous screen to close.
        minecraft.screen = null;
        minecraft.setScreen(screen);
    }

    public void dismiss() {
        var minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof WrappedScreen screen) {
            // we need to switch back to the original screen again.
            minecraft.setScreen(screen.target());
        }
    }

    public static class WrappedScreen extends ClientMenuScreen {

        private Screen targetScreen;

        public WrappedScreen(ServerAlertWindow window, Component component) {
            super(window, component);
        }

        @Override
        public void resize(Minecraft minecraft, int i, int j) {
            super.resize(minecraft, i, j);
            if (targetScreen != null) {
                targetScreen.resize(minecraft, i, j);
            }
        }

        @Override
        public void render(CGGraphicsContext context, int mouseX, int mouseY, float partialTicks) {
            // we need reset mouse to impossible position to fool the original tooltip render.
            if (targetScreen != null) {
                targetScreen.render(context, Integer.MIN_VALUE, Integer.MIN_VALUE, partialTicks);
            }
            context.saveGraphicsState();
            context.translateCTM(0, 0, 500);
            super.render(context, mouseX, mouseY, partialTicks);
            context.restoreGraphicsState();
        }

        public void setTarget(Screen screen) {
            this.targetScreen = screen;
        }

        public Screen target() {
            return targetScreen;
        }
    }
}
