package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIPopoverView;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public abstract class BaseDialog extends UIView {

    private Runnable completeHandler;
    protected final UILabel titleLabel = new UILabel(new CGRect(0, 8, 240, 9));

    public BaseDialog() {
        this(new CGRect(0, 0, 240, 120));
    }

    public BaseDialog(CGRect frame) {
        super(frame);
        this.setContents(ModTextures.defaultWindowImage());
        this.titleLabel.setTextHorizontalAlignment(NSTextAlignment.Horizontal.CENTER);
        this.titleLabel.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        this.addSubview(titleLabel);
    }

    @Override
    public void keyDown(UIEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            dismiss();
            return;
        }
        super.keyDown(event);
    }

    public void dismiss() {
        var window = window();
        if (window instanceof UIPopoverView popoverView) {
            window.removeGlobalTarget(this, UIControl.Event.KEY_DOWN);
            popoverView.dismiss();
        }
        if (completeHandler != null) {
            completeHandler.run();
            completeHandler = null;
        }
    }

    public void showInView(UIView view) {
        var popoverView = makePopoverView();
        popoverView.showInView(view);
//        popoverView.addGlobalTarget(this, UIControl.Event.KEY_DOWN, (self, event) -> {
//            if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
//                event.cancel(InvokerResult.SUCCESS);
//                self.dismiss();
//            }
//        });
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
        var popoverView = new UIPopoverView();
        popoverView.setContentView(this);
        return popoverView;
    }
}
