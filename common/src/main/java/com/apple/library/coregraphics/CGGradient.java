package com.apple.library.coregraphics;

import com.apple.library.uikit.UIColor;

@SuppressWarnings("unused")
public class CGGradient {

    public final UIColor startColor;
    public final UIColor endColor;

    public final CGPoint startPoint;
    public final CGPoint endPoint;

    public CGGradient(UIColor startColor, CGPoint startPoint, UIColor endColor, CGPoint endPoint) {
        this.startColor = startColor;
        this.startPoint = startPoint;
        this.endColor = endColor;
        this.endPoint = endPoint;
    }
}
