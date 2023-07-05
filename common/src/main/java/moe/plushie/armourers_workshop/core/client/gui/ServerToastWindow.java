package moe.plushie.armourers_workshop.core.client.gui;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.core.client.gui.widget.ContainerMenuToast;
import moe.plushie.armourers_workshop.core.client.gui.widget.ToastWindow;
import moe.plushie.armourers_workshop.core.network.ExecuteAlertPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class ServerToastWindow extends ToastWindow {

    public ServerToastWindow(ExecuteAlertPacket alertPacket) {
        super(new CGRect(0, 0, 160, 32));
        this.setTitle(new NSString(alertPacket.getTitle()));
        this.setMessage(new NSString(alertPacket.getMessage()));
        if (alertPacket.getType() == 0x80000001) {
            this.setTitleColor(new UIColor(0xff88ff));
        }
        this.setIcon(alertPacket.getIcon());
        if (this.getIcon() == null) {
            int width = frame().getWidth();
            this.titleLabel.setFrame(new CGRect(8, 7, width - 8 - 5, 9));
            this.messageLabel.setFrame(new CGRect(8, 18, width - 8 - 5, 9));
        }
    }

    public void showInScreen() {
        Minecraft.getInstance().getToasts().addToast(new ContainerMenuToast<>(this));
    }
}
