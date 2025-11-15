package com.apple.library.impl.event;

import com.apple.library.coregraphics.CGPoint;

public interface InputMouseEvent {

    CGPoint location();

    CGPoint delta();

    int button();

    int modifiers();
}
