package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.core.client.gui.ServerAlertWindow;
import moe.plushie.armourers_workshop.core.network.ExecuteAlertPacket;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class Toast {

    // show the message in to current screen.
    public static void showAlertFromServer(ExecuteAlertPacket alertPacket) {
        RenderSystem.recordRenderCall(() -> {
            ServerAlertWindow window = new ServerAlertWindow(alertPacket);
            window.showInScreen();
        });
    }

    public static void show(String message, UIView view) {
        showAlert("Info", message, null, view);
    }

    public static void show(Exception exception, UIView view) {
        exception.printStackTrace();
        showAlert("Error", exception.getMessage(), UIColor.RED, view);
    }

    private static void showAlert(String title, String message, UIColor messageColor, UIView view) {
        Impl alert = new Impl();
        alert.setTitle(new NSString(title));
        alert.setMessage(new NSString(message));
        if (messageColor != null) {
            alert.setMessageColor(messageColor);
        }
        alert.showInView(view, () -> {
        });
    }

    private static class Impl extends BaseDialog {

        protected final UILabel messageLabel = new UILabel(new CGRect(0, 8, 240, 20));
        protected final UIButton confirmButton = buildButton(0, 0, 100, 20, Impl::confirmAction);

        private NSString confirmText;

        public Impl() {
            super();
            this.messageLabel.setNumberOfLines(0);
            this.addSubview(messageLabel);
            this.setConfirmText(new NSString(TranslateUtils.title("inventory.armourers_workshop.common.button.ok")));
            this.setup();
        }

        private void setup() {
            CGRect rect = bounds();
            int w = 100;
            int sp = (rect.width - w) / 2;
            int bottom = rect.height - 30;

            messageLabel.setFrame(new CGRect(10, 30, rect.width - 20, 20));
            messageLabel.setAutoresizingMask(AutoresizingMask.flexibleWidth);

            confirmButton.setFrame(new CGRect(sp, bottom, w, 20));
            confirmButton.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleTopMargin);
        }

        protected UIButton buildButton(int x, int y, int width, int height, BiConsumer<Impl, UIControl> event) {
            UIButton button = new UIButton(new CGRect(x, y, width, height));
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
