package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.impl.InvokerResult;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIPopoverView;
import com.apple.library.uikit.UIView;
import com.apple.library.uikit.UIWindow;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;

@Environment(value = EnvType.CLIENT)
public abstract class BaseDialog extends UIView {

    private Runnable completeHandler;
    protected final UILabel titleLabel = new UILabel(new CGRect(0, 8, 240, 9));

    public BaseDialog() {
        super(new CGRect(0, 0, 240, 120));
        this.setContents(ModTextures.defaultWindowImage());
        this.titleLabel.setTextHorizontalAlignment(NSTextAlignment.Horizontal.CENTER);
        this.titleLabel.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        this.addSubview(titleLabel);
    }

    public void dismiss() {
        UIWindow window = window();
        if (window instanceof UIPopoverView) {
            window.removeGlobalTarget(this, UIControl.Event.KEY_DOWN);
            ((UIPopoverView) window).dismiss();
        }
        if (completeHandler != null) {
            completeHandler.run();
            completeHandler = null;
        }
    }

    public void showInView(UIView view) {
        UIPopoverView popoverView = makePopoverView();
        popoverView.showInView(view);
        popoverView.addGlobalTarget(this, UIControl.Event.KEY_DOWN, (self, event) -> {
            if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
                event.cancel(InvokerResult.SUCCESS);
                self.dismiss();
            }
        });
    }

    public void showInView(UIView view, Runnable completeHandler) {
        this.completeHandler = completeHandler;
        showInView(view);
    }

    public NSString title() {
        return titleLabel.text();
    }

    public void setTitle(NSString title) {
        titleLabel.setText(title);
    }

    protected UIPopoverView makePopoverView() {
        UIPopoverView popoverView = new UIPopoverView();
        popoverView.setContentView(this);
        return popoverView;
    }
}
