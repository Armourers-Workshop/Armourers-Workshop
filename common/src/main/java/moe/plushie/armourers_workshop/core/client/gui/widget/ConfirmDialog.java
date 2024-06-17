package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class ConfirmDialog extends BaseDialog {

    protected final UILabel messageLabel = new UILabel(new CGRect(0, 8, 240, 20));
    protected final UIButton confirmButton = buildButton(0, 0, 100, 20, ConfirmDialog::confirmAction);
    protected final UIButton cancelButton = buildButton(0, 0, 100, 20, ConfirmDialog::cancelAction);

    private int selectedIndex = 0;

    private NSString cancelText;
    private NSString confirmText;

    public ConfirmDialog() {
        super();
        this.messageLabel.setNumberOfLines(0);
        this.addSubview(messageLabel);
        this.setConfirmText(NSString.localizedString("common.button.ok"));
        this.setCancelText(NSString.localizedString("common.button.cancel"));
        this.setup();
    }

    private void setup() {
        var rect = bounds();
        float w = 100;
        float sp = (rect.width - w * 2) / 3;
        float bottom = rect.height - 30;

        messageLabel.setFrame(new CGRect(10, 30, rect.width - 20, 20));
        messageLabel.setAutoresizingMask(AutoresizingMask.flexibleWidth);

        confirmButton.setFrame(new CGRect(sp, bottom, w, 20));
        confirmButton.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleTopMargin);

        cancelButton.setFrame(new CGRect(rect.width - w - sp, bottom, w, 20));
        cancelButton.setAutoresizingMask(AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleTopMargin);
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

    public NSString cancelText() {
        return cancelText;
    }

    public void setCancelText(NSString text) {
        cancelText = text;
        cancelButton.setTitle(text, UIControl.State.NORMAL);
    }

    public void confirmAction(UIControl sender) {
        this.selectedIndex = 1;
        this.dismiss();
    }

    public void cancelAction(UIControl sender) {
        this.selectedIndex = 0;
        this.dismiss();
    }

    public boolean isCancelled() {
        return selectedIndex == 0;
    }

    protected UIButton buildButton(int x, int y, int width, int height, BiConsumer<ConfirmDialog, UIControl> event) {
        var button = new UIButton(new CGRect(x, y, width, height));
        button.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
        button.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, event);
        addSubview(button);
        return button;
    }
}
