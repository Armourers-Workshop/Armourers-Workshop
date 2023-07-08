package com.apple.library.uikit;

import com.apple.library.coregraphics.CGAffineTransform;
import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.impl.InvokerResult;
import com.apple.library.impl.LBSIterator;
import com.apple.library.impl.WeakDispatcherImpl;
import com.apple.library.impl.WindowDispatcherImpl;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public class UIWindow extends UIView {

    private int level = 0;

    private UIView firstResponder;
    private UIView hoveredResponder;
    private UIView firstInputResponder;
    private UIView focusedResponder;

    private final HashMap<UIControl.Event, WeakDispatcherImpl<UIEvent>> dispatchers = new HashMap<>();

    private WeakReference<UIWindowManager> windowManager;

    public UIWindow(CGRect frame) {
        super(frame);
    }

    public void init() {
    }

    public void deinit() {
        if (firstInputResponder != null) {
            firstInputResponder.resignFirstResponder();
            firstInputResponder = null;
        }
        _removeAllSubviews(this);
    }

    @Override
    public void mouseDown(UIEvent event) {
        event.cancel(InvokerResult.FAIL);
    }

    @Override
    public void mouseUp(UIEvent event) {
        event.cancel(InvokerResult.FAIL);
    }

    @Override
    public void mouseDragged(UIEvent event) {
        event.cancel(InvokerResult.FAIL);
    }

    @Override
    public void mouseMoved(UIEvent event) {
        event.cancel(InvokerResult.FAIL);
    }

    @Override
    public void mouseExited(UIEvent event) {
        event.cancel(InvokerResult.FAIL);
    }

    @Override
    public void mouseEntered(UIEvent event) {
        event.cancel(InvokerResult.FAIL);
    }

    @Override
    public void keyUp(UIEvent event) {
        event.cancel(InvokerResult.FAIL);
    }

    @Override
    public void keyDown(UIEvent event) {
        event.cancel(InvokerResult.FAIL);
    }

    public void screenWillTick() {
    }

    public void screenWillResize(CGSize size) {
        setCenter(new CGPoint(size.width / 2, size.height / 2));
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
        _setFocusedResponder(view);
    }


    public UIWindowManager getWindowManager() {
        if (windowManager != null) {
            return windowManager.get();
        }
        return null;
    }

    protected void setWindowManager(UIWindowManager manager) {
        this.windowManager = new WeakReference<>(manager);
    }


    protected boolean shouldPassEventToNextWindow(UIEvent event) {
        return true;
    }

    protected void _didRemoveFromWindow(UIView view) {
        if (firstResponder == view) {
            firstResponder = null;
        }
        if (focusedResponder == view) {
            _setFocusedResponder(null);
        }
        if (firstInputResponder == view) {
            firstInputResponder = null;
        }
        if (hoveredResponder == view) {
            _setHoveredResponder(null, Dispatcher.NULL_EVENT);
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

    private void _setFocusedResponder(UIView view) {
        if (focusedResponder == view) {
            return;
        }
        if (focusedResponder != null) {
            Dispatcher.applyFocusedView(focusedResponder, false);
        }
        focusedResponder = view;
        if (focusedResponder != null) {
            Dispatcher.applyFocusedView(focusedResponder, true);
        }
    }

    private void _setInputResponder(UIView view) {
        if (firstInputResponder == view) {
            return;
        }
        if (firstInputResponder != null) {
            firstInputResponder.resignFirstResponder();
        }
        firstInputResponder = view;
        if (firstInputResponder != null) {
            firstInputResponder.becomeFirstResponder();
        }
    }

    private void _setHoveredResponder(UIView view, UIEvent event) {
        if (hoveredResponder == view) {
            return;
        }
        if (hoveredResponder != null) {
            Dispatcher.applyMouseHovered(hoveredResponder, false, event);
        }
        hoveredResponder = view;
        if (hoveredResponder != null) {
            Dispatcher.applyMouseHovered(hoveredResponder, true, event);
        }
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
        public void layout(CGSize size) {
            window.screenWillResize(size);
            window.layoutIfNeeded();
        }

        @Override
        public void render(CGGraphicsContext context) {
            int level = window.level();
            if (level != 0) {
                context.saveGraphicsState();
                context.translateCTM(0, 0, level);
            }
            float mouseX = context.state().mousePos().getX();
            float mouseY = context.state().mousePos().getY();
            applyRender(mouseX, mouseY, 0, window, context);
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
            return applyKeyEvent(event, UIResponder::keyUp);
        }

        @Override
        public InvokerResult keyDown(int key, int keyModifier, int j) {
            UIEvent event = makeKeyEvent(key, keyModifier, UIEvent.Type.KEY_DOWN);
            if (!window._sendGlobalEvent(UIControl.Event.of(event), event)) {
                return checkEvent(event);
            }
            return applyKeyEvent(event, UIResponder::keyDown);
        }

        @Override
        public InvokerResult charTyped(int ch, int keyModifier, int j) {
            UIEvent event = makeKeyEvent(ch, keyModifier, UIEvent.Type.CHAR_TYPED);
            if (!window._sendGlobalEvent(UIControl.Event.of(event), event)) {
                return checkEvent(event);
            }
            return applyKeyEvent(event, UIResponder::charTyped);
        }

        @Override
        public InvokerResult mouseDown(double mouseX, double mouseY, int button) {
            UIEvent event = makeMouseEvent(mouseX, mouseY, button, 0, MOUSE_BUTTONS[(button % 3)]);
            if (!window._sendGlobalEvent(UIControl.Event.of(event), event)) {
                return checkEvent(event);
            }
            window.firstResponder = findFirstResponder((float) mouseX, (float) mouseY, event, window);
            // auto resign the input first responder if needed.
            if (window.focusedResponder != window.firstResponder) {
                window._setFocusedResponder(null);
            }
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
            updateHoveredResponder((float) mouseX, (float) mouseY, event, true);
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
            updateHoveredResponder((float) mouseX, (float) mouseY, event, false);
            if (window.hoveredResponder != null) {
                window.hoveredResponder.mouseWheel(event);
                if (!event.result().isDecided()) {
                    mouseMoved(mouseX, mouseY, 0);
                }
                return checkEvent(event);
            }
            window.mouseWheel(event);
            return checkEvent(event);
        }

        @Override
        public InvokerResult mouseIsInside(double mouseX, double mouseY, int button) {
            UIEvent event = makeMouseEvent(mouseX, mouseY, button, 0, MOUSE_BUTTONS[(button % 3)]);
            CGRect frame = window.frame();
            UIView view = window.hitTest(new CGPoint((float) mouseX - frame.x, (float) mouseY - frame.y), event);
            if (view != null && view != window) {
                return InvokerResult.SUCCESS;
            }
            return InvokerResult.PASS;
        }

        @Override
        public InvokerResult changeKeyView(boolean bl) {
            UIView view = window;
            UIView targetView = null;
            if (window.focusedResponder != null) {
                UIView superview = window.focusedResponder.superview();
                if (superview != null) {
                    view = superview;
                    targetView = window.focusedResponder;
                }
            }
            LBSIterator<UIView> iterator = new LBSIterator<>(bl);
            UIView responder = findFocusedResponder(view, targetView, iterator, false, targetView != null);
            if (responder != null) {
                window._setInputResponder(responder);
                window._setFocusedResponder(responder);
                return InvokerResult.SUCCESS;
            }
            return InvokerResult.PASS;
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

        private void updateHoveredResponder(float mouseX, float mouseY, UIEvent event, boolean force) {
            window._setHoveredResponder(findFirstResponder(mouseX, mouseY, event, window), event);
        }

        private UIEvent makeMouseEvent(double mouseX, double mouseY, int key, double delta, UIEvent.Type type) {
            return makeEvent(mouseX, mouseY, key, 0, delta, type);
        }

        private UIEvent makeKeyEvent(int key, int keyModifier, UIEvent.Type type) {
            return makeEvent(0, 0, key, keyModifier, 0, type);
        }

        private UIEvent makeEvent(double mouseX, double mouseY, int key, int keyModifier, double delta, UIEvent.Type type) {
            CGRect frame = window.frame();
            CGPoint location = new CGPoint((float) mouseX - frame.x, (float) mouseY - frame.y);
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
            return event.result();
        }

        private InvokerResult applyKeyEvent(UIEvent event, BiConsumer<UIResponder, UIEvent> consumer) {
            UIResponder[] responders = {window.firstInputResponder, window.focusedResponder, window.hoveredResponder, window};
            for (UIResponder responder : responders) {
                if (responder != null) {
                    consumer.accept(responder, event);
                    break;
                }
            }
            return checkEvent(event);
        }

        private static void applyRender(float mouseX, float mouseY, int depth, UIView view, CGGraphicsContext context) {
            if (view.isHidden()) {
                return;
            }
            CGRect frame = view.frame();
            CGRect bounds = view.bounds();
            float ix = mouseX - frame.x;
            float iy = mouseY - frame.y;
            boolean needClips = view.isClipBounds();
            if (needClips) {
                context.addClipRect(UIScreen.convertRectFromView(bounds, view));
            }
            context.saveGraphicsState();
            context.translateCTM(frame.x - bounds.x, frame.y - bounds.y, view.zIndex());
            CGAffineTransform transform = view.transform();
            if (!transform.isIdentity()) {
                context.concatenateCTM(transform);
            }
            context.strokeDebugRect(depth, bounds);
            view.layerWillDraw(context);
            UIColor backgroundColor = view.backgroundColor();
            if (backgroundColor != null) {
                context.fillRect(backgroundColor, bounds);
            }
            boolean isOpaque = view.isOpaque();
            if (!isOpaque) {
                context.enableBlend();
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

        private static UIView findFirstResponder(float mouseX, float mouseY, UIEvent event, UIView view) {
            CGRect frame = view.frame();
            CGPoint point = new CGPoint(mouseX - frame.x, mouseY - frame.y);
            return view.hitTest(point, event);
        }

        private static UIView findFocusedResponder(UIView view, @Nullable UIView currentView, LBSIterator<UIView> iterator, boolean ignoreSelf, boolean backToTop) {
            List<UIView> subviews = view.subviews();
            for (UIView subview : iterator.remaining(subviews, currentView)) {
                if (ignoreSelf && subview == currentView) {
                    continue;
                }
                if (subview != currentView && subview.canBecomeFocused()) {
                    return subview;
                }
                UIView focusedResponder = findFocusedResponder(subview, null, iterator, false, false);
                if (focusedResponder != null) {
                    return focusedResponder;
                }
            }
            if (backToTop) {
                UIView superView = view.superview();
                if (superView != null) {
                    UIView focusedResponder = findFocusedResponder(superView, view, iterator, true, true);
                    if (focusedResponder != null) {
                        return focusedResponder;
                    }
                }
            }
            for (UIView subview : iterator.skipping(subviews, currentView)) {
                if (subview != currentView && subview.canBecomeFocused()) {
                    return subview;
                }
                UIView focusedResponder = findFocusedResponder(subview, null, iterator, false, false);
                if (focusedResponder != null) {
                    return focusedResponder;
                }
            }
            return null;
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

        private static void applyFocusedView(UIView view, boolean isFocused) {
            if (view._flags.isFocused != isFocused) {
                view._flags.isFocused = isFocused;
                view.focusesDidChange();
            }
        }
    }
}

