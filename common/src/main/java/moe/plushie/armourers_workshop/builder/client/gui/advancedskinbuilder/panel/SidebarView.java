package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.panel;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEdgeInsets;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIScrollView;
import com.apple.library.uikit.UIScrollViewDelegate;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.init.ModTextures;

// /------\
// |[-|---|
// |  |---|
// \------/
public class SidebarView extends UIView implements UIScrollViewDelegate {

    protected final UIScrollView sidebarView = new UIScrollView(CGRect.ZERO);
    protected final UIScrollView containerView = new UIScrollView(CGRect.ZERO);

    protected final UIImage iconImage;

    public SidebarView(CGRect frame) {
        super(frame);
        this.iconImage = ModTextures.buttonImage(ModTextures.ADVANCED_SKIN_BUILDER, 0, 48, 24, 24);
        this.sidebarView.setFrame(new CGRect(0, 0, 24, frame.height));
        this.sidebarView.setAutoresizingMask(AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleHeight);
        this.sidebarView.setShowsVerticalScrollIndicator(false);
        this.sidebarView.setDelegate(this);
        this.addSubview(sidebarView);
        this.containerView.setFrame(new CGRect(24, 0, frame.width - 24, frame.height));
        this.containerView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        this.containerView.setContents(UIImage.of(ModTextures.ADVANCED_SKIN_BUILDER).uv(48, 0).resizable(24, 24).build());
        this.containerView.setShowsVerticalScrollIndicator(false);
        this.containerView.setDelegate(this);
        this.addSubview(containerView);
    }

    public void reloadData() {
        int y = 0;
        for (int j = 0; j < 5; ++j) {
            if (j != 0) {
                y += 5;
            }
            for (int i = 0; i < 10; ++i) {
                UIButton iconView = new UIButton(new CGRect(0, y, 24, 24));
                iconView.setImage(UIImage.of(ModTextures.TAB_ICONS).uv(16, 16 * 8).fixed(16, 16).build(), UIControl.State.NORMAL);
                iconView.setImageEdgeInsets(new UIEdgeInsets(2, 2, 2, 0));
                iconView.setBackgroundImage(iconImage, UIControl.State.ALL);
                if (j == 0 && i == 0) {
                    iconView.setSelected(true);
                }
                iconView.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, (self, btn) -> {
                    for (UIView view : btn.superview().subviews()) {
                        if (view instanceof UIButton) {
                            ((UIButton) view).setSelected(false);
                        }
                    }
                    btn.setSelected(true);
                });
                sidebarView.addSubview(iconView);
                y += 24;
            }
        }
        sidebarView.setContentSize(new CGSize(0, y));

        float top = 4;
//        for (int j = 0; j < 100; ++j) {
//            UILabel lb = new UILabel(new CGRect(4, top, 180 - 8, 10));
//            lb.setText(new NSString("Hello World!!"));
//            containerView.addSubview(lb);
//            top += 10 + 2;
//        }
        UIView c = new UIView(new CGRect(8, 4, bounds().getWidth() - 16, 240));
        top += 240;
        top += 4;
        containerView.setContentSize(new CGSize(0, top));
    }

    public void addEntry(Entry entry) {

    }

    public static class Entry {


    }
}
