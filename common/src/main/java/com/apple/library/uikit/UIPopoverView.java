package com.apple.library.uikit;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class UIPopoverView extends UIWindow {

    private UIView contentView;

    public UIPopoverView() {
        super(CGRect.ZERO);
        this.setLevel(400);
        this.setBackgroundColor(new UIColor(0x3f000000, true));
    }

    public void dismiss() {
        UIWindowManager.sharedManager().removeWindow(this);
    }

    public void showInView(UIView view) {
        UIWindowManager.sharedManager().addWindow(this);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        CGRect rect = bounds();
        if (contentView != null) {
            contentView.setOrigin(new CGPoint(rect.width / 2, rect.height / 2));
        }
    }

    @Override
    public void screenWillResize(CGSize size) {
        setFrame(new CGRect(0, 0, size.width, size.height));
    }

    @Override
    protected boolean shouldPassEventToNextWindow(UIEvent event) {
        return false;
    }

    @Override
    public UIView hitTest(CGPoint point, UIEvent event) {
        UIView view = super.hitTest(point, event);
        if (view != null) {
            return view;
        }
        return contentView;
    }

    @Override
    public UIView firstTooltipResponder() {
        UIView view = super.firstTooltipResponder();
        if (view != null) {
            return view;
        }
        return this;
    }

    public UIView contentView() {
        return contentView;
    }

    public void setContentView(UIView contentView) {
        if (this.contentView == contentView) {
            return;
        }
        if (this.contentView != null) {
            this.contentView.removeFromSuperview();
        }
        this.contentView = contentView;
        if (this.contentView != null) {
            this.addSubview(this.contentView);
            this.setNeedsLayout();
        }
    }
}
