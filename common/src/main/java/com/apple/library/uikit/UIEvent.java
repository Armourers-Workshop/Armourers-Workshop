package com.apple.library.uikit;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.impl.InvokerResult;
import com.apple.library.impl.event.InputKeyEvent;
import com.apple.library.impl.event.InputMouseEvent;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class UIEvent {

    private InvokerResult result = InvokerResult.PASS;
    private boolean isCancelled = false;

    protected final long timestamp;
    protected final CGPoint delta;
    protected final CGPoint location;
    protected final Type type;

    protected final InputKeyEvent key;
    protected final InputMouseEvent mouse;

    public UIEvent(Type type, CGPoint location, CGPoint delta, @Nullable InputKeyEvent key, @Nullable InputMouseEvent mouse) {
        this.type = type;
        this.location = location;
        this.timestamp = System.currentTimeMillis();
        this.delta = delta;
        this.key = key;
        this.mouse = mouse;
    }

    public Type type() {
        return type;
    }

    public CGPoint delta() {
        return delta;
    }

    public CGPoint locationInWindow() {
        return location;
    }

    public CGPoint locationInView(UIView view) {
        return view.convertPointFromView(location, null);
    }

    public void cancel(InvokerResult result) {
        setCancelled(true);
        setResult(result);
    }

    public void setResult(InvokerResult result) {
        this.result = result;
    }

    public InvokerResult result() {
        return result;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public InputKeyEvent key() {
        return key;
    }

    public InputMouseEvent mouse() {
        return mouse;
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
