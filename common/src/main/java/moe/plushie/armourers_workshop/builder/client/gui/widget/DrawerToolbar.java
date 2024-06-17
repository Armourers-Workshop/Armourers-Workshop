package moe.plushie.armourers_workshop.builder.client.gui.widget;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.uikit.UIBarItem;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEdgeInsets;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIScrollView;
import com.apple.library.uikit.UIScrollViewDelegate;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.init.ModTextures;

import java.util.ArrayList;

// /------\
// |[-|---|
// |  |---|
// \------/
public class DrawerToolbar extends UIView implements UIScrollViewDelegate {

    protected final UIScrollView sidebarView = new UIScrollView(CGRect.ZERO);
    protected final UIScrollView containerView = new UIScrollView(CGRect.ZERO);

    protected final UIImage badgeBackgroundImage;

    private UIView displayView;
    private final ArrayList<UIButton> entities = new ArrayList<>();

    public DrawerToolbar(CGRect frame) {
        super(frame);
        this.badgeBackgroundImage = ModTextures.buttonImage(ModTextures.ADVANCED_SKIN_BUILDER, 0, 48, 24, 24);
        this.sidebarView.setFrame(new CGRect(0, 0, getBarSize(), frame.height));
        this.sidebarView.setAutoresizingMask(AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleHeight);
        this.sidebarView.setShowsVerticalScrollIndicator(false);
        this.sidebarView.setDelegate(this);
        this.addSubview(sidebarView);
        this.containerView.setFrame(new CGRect(getBarSize(), 0, frame.width - getBarSize(), frame.height));
        this.containerView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        this.containerView.setContents(UIImage.of(ModTextures.ADVANCED_SKIN_BUILDER).uv(52, 24).fixed(20, 24).clip(4, 0, 4, 4).build());
        this.containerView.setShowsVerticalScrollIndicator(false);
        this.containerView.setDelegate(this);
        this.addSubview(containerView);
    }

    public void addPage(UIView contentView, UIBarItem barItem) {
        var edg = barItem.imageInsets();
        var badgeView = new UIButton(new CGRect(0, 0, getBarSize(), getBarSize()));
        badgeView.setImage(barItem.getImage(), UIControl.State.NORMAL);
        badgeView.setImageEdgeInsets(new UIEdgeInsets(edg.top + 2, edg.left + 2, edg.bottom + 2, edg.right + 0));
        badgeView.setBackgroundImage(badgeBackgroundImage, UIControl.State.ALL);
        badgeView.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, (self, sender) -> self.switchToPage(sender, contentView));
        entities.add(badgeView);
        sidebarView.addSubview(badgeView);
        // select first page
        if (entities.size() == 1) {
            switchToPage(entities.get(0), contentView);
        }
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        float y = 0;
        for (var entity : entities) {
            if (y != 0) {
                y += 5;
            }
            CGRect rect = entity.frame().copy();
            rect.y = y;
            entity.setFrame(rect);
            y += rect.height;
        }
        sidebarView.setContentSize(new CGSize(0, y));
    }

    protected float getBarSize() {
        return 24;
    }

    protected void switchToPage(UIControl sender, UIView contentView) {
        for (var entity : entities) {
            entity.setSelected(false);
        }
        sender.setSelected(true);
        if (displayView != null) {
            displayView.removeFromSuperview();
        }
        displayView = contentView;
        if (displayView != null) {
            containerView.addSubview(displayView);
            var bounds = containerView.bounds();
            var size = displayView.sizeThatFits(bounds.size());
            displayView.setFrame(new CGRect(0, 0, bounds.width, Math.max(bounds.height, size.height)));
            containerView.setContentSize(new CGSize(0, size.height));
            containerView.setContentOffset(new CGPoint(0, 0));
        }
    }
}
