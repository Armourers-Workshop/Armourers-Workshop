package moe.plushie.armourers_workshop.core.client.gui;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.core.client.gui.widget.ContainerMenuToast;
import moe.plushie.armourers_workshop.core.client.gui.widget.ToastWindow;
import moe.plushie.armourers_workshop.core.network.ExecuteAlertPacket;

public class ServerToastWindow extends ToastWindow {

    public ServerToastWindow(ExecuteAlertPacket alertPacket) {
        super(new CGRect(0, 0, 160, 32));
        this.setTitle(new NSString(alertPacket.title()));
        this.setMessage(new NSString(alertPacket.message()));
        if (alertPacket.type() == 0x80000001) {
            this.setTitleColor(UIColor.of(0xffff88ff));
        }
        this.setIcon(alertPacket.icon());
        if (this.icon() == null) {
            float width = frame().width();
            this.titleLabel.setFrame(new CGRect(8, 7, width - 8 - 5, 9));
            this.messageLabel.setFrame(new CGRect(8, 18, width - 8 - 5, 9));
        }
    }

    public void showInScreen() {
        ContainerMenuToast.showToast(new ContainerMenuToast<>(this));
    }
}
