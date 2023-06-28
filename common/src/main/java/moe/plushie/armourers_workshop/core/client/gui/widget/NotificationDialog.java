package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIFont;
import moe.plushie.armourers_workshop.utils.MathUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class NotificationDialog extends ConfirmDialog {

    public NotificationDialog() {
        super();
        this.setup();
    }

    private void setup() {
        CGRect rect = bounds();
        int w = 100;
        int sp = (rect.width - w) / 2;
        int bottom = rect.height - 30;

        confirmButton.setFrame(new CGRect(sp, bottom, w, 20));
        confirmButton.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleTopMargin);

        // we not need a cancel button.
        cancelButton.removeFromSuperview();
    }

    public void sizeToFit() {
        CGRect bounding = message().boundingRectWithFont(UIFont.system());
        int minWidth = MathUtils.clamp(bounding.width, 120, 280) + 40;
        CGRect bounds = bounds();
        bounds = new CGRect(0, 0, minWidth, bounds.height);
        setBounds(bounds);
    }
}
