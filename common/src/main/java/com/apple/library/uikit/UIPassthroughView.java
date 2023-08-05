package com.apple.library.uikit;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;

@SuppressWarnings("unused")
public class UIPassthroughView extends UIView {

    private final UIView view;

    public UIPassthroughView(UIView view) {
        super(CGRect.ZERO);
        this.view = view;
    }

    @Override
    public boolean pointInside(CGPoint point, UIEvent event) {
        return view.pointInside(convertPointToView(point, view), event);
    }

    @Override
    public UIView hitTest(CGPoint point, UIEvent event) {
        return view.hitTest(convertPointToView(point, view), event);
    }

    public UIView view() {
        return view;
    }
}
