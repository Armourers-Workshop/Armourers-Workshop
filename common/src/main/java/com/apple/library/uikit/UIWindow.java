package com.apple.library.uikit;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.impl.InvokerResult;
import com.apple.library.impl.WeakDispatcherImpl;
import com.apple.library.impl.WindowDispatcherImpl;
import moe.plushie.armourers_workshop.utils.RenderSystem;

import java.util.HashMap;
import java.util.function.BiConsumer;

public class UIWindow extends UIView {

    private int level = 0;

    private UIView firstResponder;
    private UIView hoveredResponder;
    private UIView firstInputResponder;

    private final HashMap<UIControl.Event, WeakDispatcherImpl<UIEvent>> dispatchers = new HashMap<>();

    public UIWindow(CGRect frame) {
        super(frame);
    }

    public void init() {
    }

    public void deinit() {
        if (firstInputResponder != null) {
            firstInputResponder.resignFirstResponder();
        }
        _removeAllSubviews(this);
    }

    @Override
    public void mouseDown(UIEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void mouseUp(UIEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void mouseDragged(UIEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void mouseMoved(UIEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void mouseExited(UIEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void mouseEntered(UIEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void keyUp(UIEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void keyDown(UIEvent event) {
        event.setCancelled(true);
    }

    public void screenWillTick() {
    }

    public void screenWillResize(CGSize size) {
        setOrigin(new CGPoint(size.width / 2, size.height / 2));
    }

    public int level() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public <T> void addGlobalTarget(T target, UIControl.Event event, BiConsumer<T, UIEvent> consumer) {
        _dispatcher(event).add(target, consumer);
    }

    public <T> void removeGlobalTarget(T target, UIControl.Event event) {
        _dispatcher(event).remove(target);
    }

    public UIView firstResponder() {
        return firstResponder;
    }

    public UIView firstInputResponder() {
        return firstInputResponder;
    }

    public UIView firstTooltipResponder() {
        UIView view = hoveredResponder;
        while (view != null) {
            if (view.tooltip() != null) {
                return view;
            }
            view = view.superview();
        }
        return null;
    }

    protected void setFirstInputResponder(UIView view) {
        firstInputResponder = view;
    }

    protected boolean shouldPassEventToNextWindow(UIEvent event) {
        return true;
    }

    protected void _didRemoveFromWindow(UIView view) {
        if (firstResponder == view) {
            firstResponder = null;
        }
        if (firstInputResponder == view) {
            firstInputResponder = null;
        }
        if (hoveredResponder == view) {
            Dispatcher.applyMouseHovered(hoveredResponder, false, Dispatcher.NULL_EVENT);
            hoveredResponder = null;
        }
    }

    private void _removeAllSubviews(UIView view) {
        while (!view.subviews().isEmpty()) {
            UIView subview = view.subviews().get(0);
            subview.removeFromSuperview();
            _removeAllSubviews(subview);
        }
    }

    private boolean _sendGlobalEvent(UIControl.Event event, UIEvent event1) {
        _dispatcher(event).send(event1);
        return !event1.isCancelled();
    }

    private WeakDispatcherImpl<UIEvent> _dispatcher(UIControl.Event event) {
        return dispatchers.computeIfAbsent(event, k -> new WeakDispatcherImpl<>());
    }

    public static class Dispatcher extends WindowDispatcherImpl {

        // left: 0, right: 1, middle: 2
        private static final UIEvent.Type[] MOUSE_BUTTONS = {UIEvent.Type.MOUSE_LEFT_DOWN, UIEvent.Type.MOUSE_RIGHT_DOWN, UIEvent.Type.MOUSE_MIDDLE_DOWN, UIEvent.Type.MOUSE_LEFT_UP, UIEvent.Type.MOUSE_RIGHT_UP, UIEvent.Type.MOUSE_MIDDLE_UP,};

        public static final UIEvent NULL_EVENT = new UIEvent(UIEvent.Type.MOUSE_MOVED, 0, 0, 0, CGPoint.ZERO);


        public final UIWindow window;

        public Dispatcher(UIWindow window) {
            this.window = window;
        }

        @Override
        public void init() {
            window.init();
        }

        @Override
        public void deinit() {
            window.deinit();
        }

        @Override
        public void tick() {
            if (window._flags.isDirty) {
                window.layoutIfNeeded();
            }
        }

        @Override
        public void layout(int width, int height) {
            window.screenWillResize(new CGSize(width, height));
            window.layoutIfNeeded();
        }

        @Override
        public void render(CGGraphicsContext context) {
            int level = window.level();
            if (level != 0) {
                context.saveGraphicsState();
                context.translateCTM(0, 0, level);
            }
            applyRender(context.mouseX, context.mouseY, 0, window, context);
            if (level != 0) {
                context.restoreGraphicsState();
            }
        }

        @Override
        public InvokerResult keyUp(int key, int keyModifier, int j) {
            UIEvent event = makeKeyEvent(key, keyModifier, UIEvent.Type.KEY_UP);
            if (!window._sendGlobalEvent(UIControl.Event.of(event), event)) {
                return checkEvent(event);
            }
            if (window.firstInputResponder != null) {
                window.firstInputResponder.keyUp(event);
                return checkEvent(event);
            }
            if (window.hoveredResponder != null) {
                window.hoveredResponder.keyUp(event);
                return checkEvent(event);
            }
            window.keyUp(event);
            return checkEvent(event);
        }

        @Override
        public InvokerResult keyDown(int key, int keyModifier, int j) {
            UIEvent event = makeKeyEvent(key, keyModifier, UIEvent.Type.KEY_DOWN);
            if (!window._sendGlobalEvent(UIControl.Event.of(event), event)) {
                return checkEvent(event);
            }
            if (window.firstInputResponder != null) {
                window.firstInputResponder.keyDown(event);
                return checkEvent(event);
            }
            if (window.hoveredResponder != null) {
                window.hoveredResponder.keyDown(event);
                return checkEvent(event);
            }
            window.keyDown(event);
            return checkEvent(event);
        }

        @Override
        public InvokerResult charTyped(int ch, int keyModifier, int j) {
            UIEvent event = makeKeyEvent(ch, keyModifier, UIEvent.Type.CHAR_TYPED);
            if (!window._sendGlobalEvent(UIControl.Event.of(event), event)) {
                return checkEvent(event);
            }
            if (window.firstInputResponder != null) {
                window.firstInputResponder.charTyped(event);
                return checkEvent(event);
            }
            if (window.hoveredResponder != null) {
                window.hoveredResponder.charTyped(event);
                return checkEvent(event);
            }
            window.charTyped(event);
            return checkEvent(event);
        }

        @Override
        public InvokerResult mouseDown(double mouseX, double mouseY, int button) {
            UIEvent event = makeMouseEvent(mouseX, mouseY, button, 0, MOUSE_BUTTONS[(button % 3)]);
            if (!window._sendGlobalEvent(UIControl.Event.of(event), event)) {
                return checkEvent(event);
            }
            window.firstResponder = findFirstResponder((int) mouseX, (int) mouseY, event, window);
            // auto resign the input first responder if needed.
            if (window.firstInputResponder != window.firstResponder) {
                if (window.firstInputResponder != null) {
                    window.firstInputResponder.resignFirstResponder();
                }
                window.firstInputResponder = window.firstResponder;
            }
            if (window.firstResponder != null) {
                window.firstResponder.mouseDown(event);
                return checkEvent(event);
            }
            window.mouseDown(event);
            return checkEvent(event);
        }

        @Override
        public InvokerResult mouseMoved(double mouseX, double mouseY, int button) {
            UIEvent event = makeMouseEvent(mouseX, mouseY, button, 0, UIEvent.Type.MOUSE_MOVED);
            if (!window._sendGlobalEvent(UIControl.Event.MOUSE_MOVED, event)) {
                return checkEvent(event);
            }
            updateHoveredResponder((int) mouseX, (int) mouseY, event, true);
            if (window.firstResponder != null) {
                window.firstResponder.mouseDragged(event);
            }
            if (window.hoveredResponder != null) {
                window.hoveredResponder.mouseMoved(event);
            }
            window.mouseMoved(event);
            return checkEvent(event);
        }

        @Override
        public InvokerResult mouseUp(double mouseX, double mouseY, int button) {
            UIEvent event = makeMouseEvent(mouseX, mouseY, button, 0, MOUSE_BUTTONS[(button % 3) + 3]);
            if (!window._sendGlobalEvent(UIControl.Event.of(event), event)) {
                return checkEvent(event);
            }
            if (window.firstResponder != null) {
                window.firstResponder.mouseUp(event);
                window.firstResponder = null;
                return checkEvent(event);
            }
            window.mouseUp(event);
            return checkEvent(event);
        }

        @Override
        public InvokerResult mouseWheel(double mouseX, double mouseY, double delta) {
            UIEvent event = makeMouseEvent(mouseX, mouseY, 0, delta, UIEvent.Type.MOUSE_WHEEL);
            if (!window._sendGlobalEvent(UIControl.Event.of(event), event)) {
                return checkEvent(event);
            }
            updateHoveredResponder((int) mouseX, (int) mouseY, event, false);
            if (window.hoveredResponder != null) {
                window.hoveredResponder.mouseWheel(event);
                if (!event.isCancelled()) {
                    mouseMoved(mouseX, mouseY, 0);
                }
                return checkEvent(event);
            }
            window.mouseWheel(event);
            return checkEvent(event);
        }

        @Override
        public boolean mouseIsInside(double mouseX, double mouseY, int button) {
            UIEvent event = makeMouseEvent(mouseX, mouseY, button, 0, MOUSE_BUTTONS[(button % 3)]);
            CGRect frame = window.frame();
            UIView view = window.hitTest(new CGPoint((int) mouseX - frame.x, (int) mouseY - frame.y), event);
            return view != null && view != window;
        }

        @Override
        public UIView firstInputResponder() {
            return window.firstInputResponder();
        }

        @Override
        public UIView firstTooltipResponder() {
            return window.firstTooltipResponder();
        }

        @Override
        public int level() {
            return window.level();
        }

        private void updateHoveredResponder(int mouseX, int mouseY, UIEvent event, boolean force) {
            UIView newHoveredResponder = findFirstResponder(mouseX, mouseY, event, window);
            if (window.hoveredResponder != newHoveredResponder) {
                if (window.hoveredResponder != null) {
                    applyMouseHovered(window.hoveredResponder, false, event);
                }
                window.hoveredResponder = newHoveredResponder;
                if (window.hoveredResponder != null) {
                    applyMouseHovered(window.hoveredResponder, true, event);
                }
            }
        }

        private UIEvent makeMouseEvent(double mouseX, double mouseY, int key, double delta, UIEvent.Type type) {
            return makeEvent(mouseX, mouseY, key, 0, delta, type);
        }

        private UIEvent makeKeyEvent(int key, int keyModifier, UIEvent.Type type) {
            return makeEvent(0, 0, key, keyModifier, 0, type);
        }

        private UIEvent makeEvent(double mouseX, double mouseY, int key, int keyModifier, double delta, UIEvent.Type type) {
            CGRect frame = window.frame();
            CGPoint location = new CGPoint((int) mouseX - frame.x, (int) mouseY - frame.y);
            return new UIEvent(type, key, keyModifier, delta, location);
        }

        private InvokerResult checkEvent(UIEvent event) {
            // if the event is not cancelled, indicates the event has been successfully handled,
            // we needs return success to interrupt the event chain.
            if (!event.isCancelled()) {
                return InvokerResult.SUCCESS;
            }
            // in normal case we need to pass events to the next window,
            // but some special cases the window need exclusive the event handler.
            if (window.shouldPassEventToNextWindow(event)) {
                return InvokerResult.PASS;
            }
            return InvokerResult.FAIL;
        }

        private static void applyRender(int mouseX, int mouseY, int depth, UIView view, CGGraphicsContext context) {
            if (view.isHidden()) {
                return;
            }
            CGRect frame = view.frame();
            CGRect bounds = view.bounds();
            int ix = mouseX - frame.x;
            int iy = mouseY - frame.y;
            boolean needClips = view.isClipBounds();
            if (needClips) {
                context.addClipRect(view.convertRectToView(bounds, null));
            }
            context.saveGraphicsState();
            context.translateCTM(frame.x - bounds.x, frame.y - bounds.y, view.zIndex());
            context.strokeDebugRect(depth, bounds);
            view.layerWillDraw(context);
            UIColor backgroundColor = view.backgroundColor();
            if (backgroundColor != null) {
                context.fillRect(backgroundColor, bounds);
            }
            boolean isOpaque = view.isOpaque();
            if (!isOpaque) {
                RenderSystem.enableAlphaTest();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
            }
            view.render(new CGPoint(ix, iy), context);
            for (UIView subview : view.subviews()) {
                // when the clip is enabled, we will not render the views of the out of size.
                if (needClips && !bounds.intersects(subview.frame())) {
                    continue;
                }
                applyRender(ix, iy, depth + 1, subview, context);
            }
            view.layerDidDraw(context);
            context.restoreGraphicsState();
            if (needClips) {
                context.removeClipRect();
            }
        }

        private static UIView findFirstResponder(int mouseX, int mouseY, UIEvent event, UIView view) {
            CGRect frame = view.frame();
            CGPoint point = new CGPoint(mouseX - frame.x, mouseY - frame.y);
            return view.hitTest(point, event);
        }

        private static void applyMouseHovered(UIView view, boolean isHovered, UIEvent event) {
            if (view._flags.isHovered != isHovered) {
                view._flags.isHovered = isHovered;
                if (isHovered) {
                    view.mouseEntered(event);
                } else {
                    view.mouseExited(event);
                }
            }
        }
    }
}
