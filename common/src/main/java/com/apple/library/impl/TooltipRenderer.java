package com.apple.library.impl;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGRect;

public interface TooltipRenderer {

    void render(CGRect rect, CGGraphicsContext context);
}
