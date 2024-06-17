package moe.plushie.armourers_workshop.core.client.gui.notification;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.core.client.gui.ServerAlertWindow;
import moe.plushie.armourers_workshop.core.client.gui.ServerToastWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.BaseDialog;
import moe.plushie.armourers_workshop.core.client.gui.widget.ContainerMenuToast;
import moe.plushie.armourers_workshop.core.client.gui.widget.ToastWindow;
import moe.plushie.armourers_workshop.core.network.ExecuteAlertPacket;
import moe.plushie.armourers_workshop.core.skin.exception.TranslatableException;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class UserNotificationCenter {

    // show the message in to current screen.
    public static void showAlertFromServer(ExecuteAlertPacket alertPacket) {
        RenderSystem.recordRenderCall(() -> {
            if ((alertPacket.getType() & 0x80000000) != 0) {
                var window = new ServerToastWindow(alertPacket);
                window.showInScreen();
            } else {
                ServerAlertWindow window = new ServerAlertWindow(alertPacket);
                window.showInScreen();
            }
        });
    }

    public static void showAlert(NSString title, NSString message, UIColor messageColor, UIView view) {
        var alert = new Impl();
        alert.setTitle(title);
        alert.setMessage(message);
        if (messageColor != null) {
            alert.setMessageColor(messageColor);
        }
        alert.showInView(view, () -> {
        });
    }

    public static void showToast(NSString message, @Nullable UIColor messageColor, NSString title, Object icon) {
        var window = new ToastWindow(new CGRect(0, 0, 160, 32));
        window.setTitle(title);
        window.setTitleColor(new UIColor(0xff88ff));
        window.setMessage(message);
        window.setMessageColor(messageColor);
        window.setIcon(icon);
        Minecraft.getInstance().getToasts().addToast(new ContainerMenuToast<>(window));
    }

    public static void showToast(NSString message, NSString title, Object icon) {
        showToast(message, UIColor.WHITE, title, icon);
    }

    public static void showToast(NSString message, String title, Object icon) {
        showToast(message, UIColor.WHITE, new NSString(title), icon);
    }

    public static void showToast(String message, String title, Object icon) {
        showToast(new NSString(message), UIColor.WHITE, new NSString(title), icon);
    }

    public static void showToast(Exception exception, String title, Object icon) {
        NSString message;
        if (exception instanceof TranslatableException) {
            message = new NSString(((TranslatableException) exception).getComponent());
        } else {
            message = new NSString(exception.getMessage());
        }
        exception.printStackTrace();
        showToast(message, UIColor.RED, new NSString(title), icon);
    }

    private static class Impl extends BaseDialog {

        protected final UILabel messageLabel = new UILabel(new CGRect(0, 8, 240, 20));
        protected final UIButton confirmButton = buildButton(0, 0, 100, 20, Impl::confirmAction);

        private NSString confirmText;

        public Impl() {
            super();
            this.messageLabel.setNumberOfLines(0);
            this.addSubview(messageLabel);
            this.setConfirmText(NSString .localizedTableString("commands", "notify.confirm"));
            this.setup();
        }

        private void setup() {
            var rect = bounds();
            var w = 100f;
            var sp = (rect.width - w) / 2;
            var bottom = rect.height - 30;

            messageLabel.setFrame(new CGRect(10, 30, rect.width - 20, 20));
            messageLabel.setAutoresizingMask(AutoresizingMask.flexibleWidth);

            confirmButton.setFrame(new CGRect(sp, bottom, w, 20));
            confirmButton.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleTopMargin);
        }

        protected UIButton buildButton(int x, int y, int width, int height, BiConsumer<Impl, UIControl> event) {
            var button = new UIButton(new CGRect(x, y, width, height));
            button.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
            button.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
            button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, event);
            addSubview(button);
            return button;
        }

        public NSString message() {
            return messageLabel.text();
        }

        public void setMessage(NSString message) {
            messageLabel.setText(message);
        }

        public UIColor messageColor() {
            return messageLabel.textColor();
        }

        public void setMessageColor(UIColor color) {
            messageLabel.setTextColor(color);
        }

        public NSString confirmText() {
            return confirmText;
        }

        public void setConfirmText(NSString text) {
            confirmText = text;
            confirmButton.setTitle(text, UIControl.State.NORMAL);
        }

        public void confirmAction(UIControl sender) {
            this.dismiss();
        }
    }
}
