package com.apple.library.uikit;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.impl.InvokerResult;
import org.lwjgl.glfw.GLFW;

public class UIMenuPopoverView extends UIPopoverView {

    private UIView contentView;

    @Override
    public void mouseDown(UIEvent event) {
        super.mouseDown(event);
        dismiss();
    }

    @Override
    public void keyDown(UIEvent event) {
        //
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            event.cancel(InvokerResult.SUCCESS);
            event.setCancelled(true);
            dismiss();
            return;
        }
        super.keyDown(event);
    }

    @Override
    public UIView hitTest(CGPoint point, UIEvent event) {
        UIView hitView = super.hitTest(point, event);
        if (hitView != null) {
            return hitView;
        }
        return this;
    }

    @Override
    public UIView contentView() {
        return contentView;
    }

    @Override
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
