package com.apple.library.uikit;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.HighlightedDisplayable;
import com.apple.library.impl.WeakDispatcherImpl;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class UIControl extends UIView implements HighlightedDisplayable {

    private boolean isEnabled = true;
    private boolean isSelected = false;
    private boolean isHighlighted = false;

    private final HashMap<Event, WeakDispatcherImpl<UIControl>> dispatchers = new HashMap<>();

    public UIControl(CGRect frame) {
        super(frame);
    }


    public <T> void addTarget(T value, Event event, Consumer<T> consumer) {
        addTarget(value, event, (self, sender) -> consumer.accept(self));
    }

    public <T> void addTarget(T value, Event event, BiConsumer<T, UIControl> consumer) {
        getDispatcher(event).add(value, consumer);
    }

    public <T> void removeTarget(T target, Event event) {
        getDispatcher(event).remove(target);
    }

    @Override
    public void mouseUp(UIEvent event) {
        this.sendEvent(Event.of(event));
    }

    @Override
    public void mouseDown(UIEvent event) {
        this.sendEvent(Event.of(event));
    }

    @Override
    public void mouseDragged(UIEvent event) {
        this.sendEvent(Event.MOUSE_DRAGGED);
    }

    @Override
    public void mouseEntered(UIEvent event) {
        CGPoint point = event.locationInView(this);
        this.setHighlighted(shouldBeHighlight(point, event));
        this.sendEvent(Event.MOUSE_ENTERED);
    }

    @Override
    public void mouseMoved(UIEvent event) {
        this.sendEvent(Event.MOUSE_MOVED);
    }

    @Override
    public void mouseExited(UIEvent event) {
        this.setHighlighted(false);
        this.sendEvent(Event.MOUSE_EXITED);
    }

    public void sendEvent(Event event) {
        getDispatcher(event).send(this);
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        this.updateStateIfNeeded();
    }

    public boolean isHighlighted() {
        return this.isHighlighted;
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        this.isHighlighted = highlighted;
        this.applyHighlightState(this, highlighted);
        this.updateStateIfNeeded();
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
        this.updateStateIfNeeded();
    }

    protected boolean shouldBeHighlight(CGPoint point, UIEvent event) {
        return pointInside(point, event);
    }

    protected boolean shouldPassHighlighted() {
        return true;
    }

    protected void updateStateIfNeeded() {
    }

    private WeakDispatcherImpl<UIControl> getDispatcher(Event event) {
        return dispatchers.computeIfAbsent(event, k -> new WeakDispatcherImpl<>());
    }

    private void applyHighlightState(UIView view, boolean isHighlighted) {
        if (!shouldPassHighlighted()) {
            return;
        }
        for (UIView subview : view.subviews()) {
            if (subview instanceof HighlightedDisplayable) {
                ((HighlightedDisplayable) subview).setHighlighted(isHighlighted);
            }
            if (!(subview instanceof UIControl)) {
                applyHighlightState(subview, isHighlighted);
            }
        }
    }

    @Override
    public boolean _ignoresTouchEvents(UIView view) {
        if (!isEnabled()) {
            return true;
        }
        return super._ignoresTouchEvents(view);
    }

    public enum Event {
        KEY_UP(UIEvent.Type.KEY_UP),
        KEY_DOWN(UIEvent.Type.KEY_DOWN),
        CHAR_TYPED(UIEvent.Type.CHAR_TYPED),
        MOUSE_LEFT_UP(UIEvent.Type.MOUSE_LEFT_UP),
        MOUSE_LEFT_DOWN(UIEvent.Type.MOUSE_LEFT_DOWN),
        MOUSE_MIDDLE_UP(UIEvent.Type.MOUSE_MIDDLE_UP),
        MOUSE_MIDDLE_DOWN(UIEvent.Type.MOUSE_MIDDLE_DOWN),
        MOUSE_RIGHT_UP(UIEvent.Type.MOUSE_RIGHT_UP),
        MOUSE_RIGHT_DOWN(UIEvent.Type.MOUSE_RIGHT_DOWN),
        MOUSE_WHEEL(UIEvent.Type.MOUSE_WHEEL),
        MOUSE_DRAGGED,
        MOUSE_MOVED,
        MOUSE_ENTERED,
        MOUSE_EXITED,
        EDITING_DID_END,
        EDITING_DID_BEGIN,
        VALUE_CHANGED;

        private final UIEvent.Type type;

        Event() {
            this(null);
        }

        Event(UIEvent.Type type) {
            this.type = type;
        }

        public static Event of(UIEvent event) {
            UIEvent.Type type = event.type();
            for (Event event1 : values()) {
                if (event1.type == type) {
                    return event1;
                }
            }
            return null;
        }
    }

    public static class State {
        public static final int NORMAL = 0x00;
        public static final int HIGHLIGHTED = 0x01;
        public static final int SELECTED = 0x02;
        public static final int DISABLED = 0x04;
        public static final int ALL = 0x07;
    }
}

