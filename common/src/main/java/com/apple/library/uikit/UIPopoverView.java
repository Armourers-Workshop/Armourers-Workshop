package com.apple.library.uikit;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;

@SuppressWarnings("unused")
public class UIPopoverView extends UIWindow {

    private UIView contentView;

    public UIPopoverView() {
        super(CGRect.ZERO);
        this.setLevel(400);
        this.setBackgroundColor(UIColor.of(0x3f000000));
    }

    public void dismiss() {
        var windowManager = getWindowManagerFromView(this);
        if (windowManager != null) {
            windowManager.removeWindow(this);
            windowManager._setNeedsUpdateFocus();
        }
    }

    public void showInView(UIView view) {
        var windowManager = getWindowManagerFromView(view);
        if (windowManager != null) {
            windowManager.addWindow(this);
        }
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        var rect = bounds();
        if (contentView != null) {
            contentView.setCenter(new CGPoint(rect.width / 2, rect.height / 2));
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
        var view = super.hitTest(point, event);
        if (view != null) {
            return view;
        }
        return contentView;
    }

    @Override
    public UIView firstTooltipResponder() {
        var view = super.firstTooltipResponder();
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

    private UIWindowManager getWindowManagerFromView(UIView view) {
        if (view instanceof UIWindow window) {
            return window.windowManager();
        }
        var window = view.window();
        if (window != null) {
            return window.windowManager();
        }
        return null;
    }
}
