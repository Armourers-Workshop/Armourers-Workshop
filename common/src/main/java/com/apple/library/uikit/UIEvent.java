package com.apple.library.uikit;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.impl.InvokerResult;

public class UIEvent {

    private InvokerResult result = InvokerResult.PASS;

    protected final int key;
    protected final int keyModifier;

    protected final long timestamp;
    protected final double delta;
    protected final CGPoint location;
    protected final Type type;

    public UIEvent(Type type, int key, int keyModifier, double delta, CGPoint location) {
        this.type = type;
        this.location = location;
        this.timestamp = System.currentTimeMillis();
        this.delta = delta;
        this.key = key;
        this.keyModifier = keyModifier;
    }

    public int key() {
        return this.key;
    }

    public int keyModifier() {
        return keyModifier;
    }

    public Type type() {
        return this.type;
    }

    public double delta() {
        return delta;
    }

    public CGPoint locationInWindow() {
        return location;
    }

    public void cancel(InvokerResult result) {
        this.result = result;
    }

    public InvokerResult result() {
        return result;
    }

    public boolean isCancelled() {
        return result.isDecided();
    }

    public enum Type {
        KEY_UP,
        KEY_DOWN,
        CHAR_TYPED,
        MOUSE_LEFT_UP,
        MOUSE_LEFT_DOWN,
        MOUSE_MIDDLE_UP,
        MOUSE_MIDDLE_DOWN,
        MOUSE_RIGHT_UP,
        MOUSE_RIGHT_DOWN,
        MOUSE_MOVED,
        MOUSE_WHEEL,
    }
}
