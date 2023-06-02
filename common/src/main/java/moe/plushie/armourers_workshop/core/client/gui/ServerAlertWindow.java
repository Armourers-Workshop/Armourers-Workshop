package moe.plushie.armourers_workshop.core.client.gui;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.core.client.gui.widget.ClientMenuScreen;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.NotificationDialog;
import moe.plushie.armourers_workshop.core.menu.AbstractContainerMenu;
import moe.plushie.armourers_workshop.core.network.ExecuteAlertPacket;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(value = EnvType.CLIENT)
public class ServerAlertWindow extends MenuWindow<AbstractContainerMenu> {

    private final ExecuteAlertPacket alertPacket;

    public ServerAlertWindow(ExecuteAlertPacket alertPacket) {
        super(ClientMenuScreen.getEmptyMenu(), ClientMenuScreen.getEmptyInventory(), new NSString(alertPacket.getTitle()));
        this.titleView.removeFromSuperview();
        this.inventoryView.removeFromSuperview();
        this.alertPacket = alertPacket;
    }

    @Override
    public void init() {
        NotificationDialog dialog = new NotificationDialog();
        dialog.setTitle(new NSString(alertPacket.getTitle()));
        dialog.setMessage(new NSString(alertPacket.getMessage()));
        if (alertPacket.getType() == 1) {
            dialog.setMessageColor(new UIColor(0xff5555));
        }
        dialog.setConfirmText(new NSString(alertPacket.getConfirm()));
        dialog.sizeToFit();
        dialog.showInView(this, this::dismiss);
    }

    public void showInScreen() {
        Minecraft minecraft = Minecraft.getInstance();
        WrappedScreen screen = new WrappedScreen(this, alertPacket.getTitle());
        screen.setTarget(minecraft.screen);
        // we must the screen before set, otherwise it will cause the previous screen to close.
        minecraft.screen = null;
        minecraft.setScreen(screen);
    }

    public void dismiss() {
        Minecraft minecraft = Minecraft.getInstance();
        WrappedScreen screen = ObjectUtils.safeCast(minecraft.screen, WrappedScreen.class);
        if (screen == null) {
            return;
        }
        // we need to switch back to the original screen again.
        minecraft.setScreen(screen.getTarget());
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
                renderBackground(context, targetScreen, Integer.MIN_VALUE, Integer.MIN_VALUE, partialTicks);
            }
            context.saveGraphicsState();
            context.translateCTM(0, 0, 500);
            super.render(context, mouseX, mouseY, partialTicks);
            context.restoreGraphicsState();
        }

        public void setTarget(Screen screen) {
            this.targetScreen = screen;
        }

        public Screen getTarget() {
            return targetScreen;
        }
    }
}
