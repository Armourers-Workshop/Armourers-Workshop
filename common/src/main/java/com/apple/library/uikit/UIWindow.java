package com.apple.library.uikit;

import com.apple.library.coregraphics.CGAffineTransform;
import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.impl.InvokerResult;
import com.apple.library.impl.LBSIterator;
import com.apple.library.impl.ObjectUtilsImpl;
import com.apple.library.impl.WeakDispatcherImpl;
import com.apple.library.impl.WindowDispatcherImpl;
import com.apple.library.impl.event.InputKeyEvent;
import com.apple.library.impl.event.InputMouseEvent;
import com.apple.library.quartzcore.CATransaction;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public class UIWindow extends UIView {

    private int level = 0;

    private UIView firstResponder;
    private UIView hoveredResponder;
    private UIView hoveredTooltipResponder;
    private UIView firstInputResponder;
    private UIView focusedResponder;

    private final ArrayList<UIMenuItem> menuItems = new ArrayList<>();
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
        event.setResult(InvokerResult.FAIL);
    }

    @Override
    public void mouseUp(UIEvent event) {
        event.setResult(InvokerResult.FAIL);
    }

    @Override
    public void mouseDragged(UIEvent event) {
        event.setResult(InvokerResult.FAIL);
    }

    @Override
    public void mouseMoved(UIEvent event) {
        event.setResult(InvokerResult.FAIL);
    }

    @Override
    public void mouseExited(UIEvent event) {
        event.setResult(InvokerResult.FAIL);
    }

    @Override
    public void mouseEntered(UIEvent event) {
        event.setResult(InvokerResult.FAIL);
    }

    @Override
    public void keyUp(UIEvent event) {
        if (!_sendMenuEvent(event)) {
            event.setResult(InvokerResult.FAIL);
        }
    }

    @Override
    public void keyDown(UIEvent event) {
        if (!_sendMenuEvent(event)) {
            event.setResult(InvokerResult.FAIL);
        }
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

    public void addMenuItem(UIMenuItem menuItem) {
        this.menuItems.add(menuItem);
    }

    public void removeMenuItem(UIMenuItem menuItem) {
        this.menuItems.remove(menuItem);
    }

    public <T> void addGlobalTarget(T target, UIControl.Event event, BiConsumer<T, UIEvent> consumer) {
        _dispatcher(event).add(target, consumer);
    }

    public <T> void removeGlobalTarget(T target, UIControl.Event event) {
        _dispatcher(event).remove(target);
        // when remove mouse move event, the first responder maybe changes.
        if (event == UIControl.Event.MOUSE_MOVED) {
            var windowManager = windowManager();
            if (windowManager != null) {
                windowManager._setNeedsUpdateFocus();
            }
        }
    }

    public UIView firstResponder() {
        return firstResponder;
    }

    public UIView firstInputResponder() {
        return firstInputResponder;
    }

    public UIView firstTooltipResponder() {
        var view = hoveredTooltipResponder;
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


    public UIWindowManager windowManager() {
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
            _setHoveredTooltipRender(null);
        }
    }

    private void _removeAllSubviews(UIView view) {
        while (!view.subviews().isEmpty()) {
            var subview = view.subviews().get(0);
            subview.removeFromSuperview();
            _removeAllSubviews(subview);
        }
    }

    private boolean _sendGlobalEvent(UIControl.Event event, UIEvent event1) {
        _dispatcher(event).send(event1);
        return !event1.isCancelled();
    }

    private boolean _sendMenuEvent(UIEvent event) {
        var hitCount = 0;
        for (var menuItem : menuItems) {
            if (menuItem.test(event)) {
                menuItem.perform(event);
                hitCount += 1;
            }
        }
        return hitCount != 0;
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

    private void _setHoveredTooltipRender(UIView view) {
        hoveredTooltipResponder = view;
    }

    public static class Dispatcher extends WindowDispatcherImpl {

        // left: 0, right: 1, middle: 2
        private static final UIEvent.Type[] MOUSE_BUTTONS = {UIEvent.Type.MOUSE_LEFT_DOWN, UIEvent.Type.MOUSE_RIGHT_DOWN, UIEvent.Type.MOUSE_MIDDLE_DOWN, UIEvent.Type.MOUSE_LEFT_UP, UIEvent.Type.MOUSE_RIGHT_UP, UIEvent.Type.MOUSE_MIDDLE_UP,};

        public static final UIEvent NULL_EVENT = new UIEvent(UIEvent.Type.MOUSE_MOVED, CGPoint.ZERO, CGPoint.ZERO, null, null);

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
            var level = window.level();
            if (level != 0) {
                context.saveGraphicsState();
                context.translateCTM(0, 0, level);
            }
            applyAnimationPre();
            applyRender(window, context.param().mousePos(), 0, context);
            if (level != 0) {
                context.restoreGraphicsState();
            }
        }

        @Override
        public InvokerResult keyUp(InputKeyEvent e) {
            var event = makeKeyEvent(e, UIEvent.Type.KEY_UP);
            if (!window._sendGlobalEvent(UIControl.Event.of(event), event)) {
                return checkEvent(event);
            }
            return applyKeyEvent(event, UIResponder::keyUp);
        }

        @Override
        public InvokerResult keyDown(InputKeyEvent e) {
            var event = makeKeyEvent(e, UIEvent.Type.KEY_DOWN);
            if (!window._sendGlobalEvent(UIControl.Event.of(event), event)) {
                return checkEvent(event);
            }
            return applyKeyEvent(event, UIResponder::keyDown);
        }

        @Override
        public InvokerResult charTyped(InputKeyEvent e) {
            var event = makeKeyEvent(e, UIEvent.Type.CHAR_TYPED);
            if (!window._sendGlobalEvent(UIControl.Event.of(event), event)) {
                return checkEvent(event);
            }
            return applyKeyEvent(event, UIResponder::charTyped);
        }

        @Override
        public InvokerResult mouseDown(InputMouseEvent e, boolean bl) {
            var event = makeMouseEvent(e, MOUSE_BUTTONS[(e.button() % 3)]);
            if (!window._sendGlobalEvent(UIControl.Event.of(event), event)) {
                return checkEvent(event);
            }
            window.firstResponder = findFirstResponder(e.location(), event, window);
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
        public InvokerResult mouseUp(InputMouseEvent e) {
            var event = makeMouseEvent(e, MOUSE_BUTTONS[(e.button() % 3) + 3]);
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
        public InvokerResult mouseMoved(InputMouseEvent e) {
            var event = makeMouseEvent(e, UIEvent.Type.MOUSE_MOVED);
            if (!window._sendGlobalEvent(UIControl.Event.MOUSE_MOVED, event)) {
                return checkEvent(event);
            }
            updateHoveredResponder(e.location(), event, true);
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
        public InvokerResult mouseWheel(InputMouseEvent e) {
            var event = makeMouseEvent(e, UIEvent.Type.MOUSE_WHEEL);
            if (!window._sendGlobalEvent(UIControl.Event.of(event), event)) {
                return checkEvent(event);
            }
            updateHoveredResponder(e.location(), event, false);
            if (window.hoveredResponder != null) {
                window.hoveredResponder.mouseWheel(event);
                if (!event.isCancelled()) {
                    mouseMoved(e);
                }
                return checkEvent(event);
            }
            window.mouseWheel(event);
            return checkEvent(event);
        }

        @Override
        public InvokerResult mouseIsInside(InputMouseEvent e) {
            var event = makeMouseEvent(e, MOUSE_BUTTONS[(e.button() % 3)]);
            var frame = window.frame();
            var view = window.hitTest(e.location().offset(-frame.x, -frame.y), event);
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
                var superview = window.focusedResponder.superview();
                if (superview != null) {
                    view = superview;
                    targetView = window.focusedResponder;
                }
            }
            var iterator = new LBSIterator<UIView>(bl);
            var responder = findFocusedResponder(view, targetView, iterator, false, targetView != null);
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

        private void updateHoveredResponder(CGPoint mousePos, UIEvent event, boolean force) {
            window._setHoveredResponder(findFirstResponder(mousePos, event, window), event);
            window._setHoveredTooltipRender(findTooltipResponder(window, window.hoveredResponder, mousePos, event));
        }

        private UIEvent makeMouseEvent(InputMouseEvent e, UIEvent.Type type) {
            var frame = window.frame();
            var location = e.location().offset(-frame.x, -frame.y);
            var delta = e.delta();
            return new UIEvent(type, location, delta, null, e);
        }

        private UIEvent makeKeyEvent(InputKeyEvent e, UIEvent.Type type) {
            return new UIEvent(type, CGPoint.ZERO, CGPoint.ZERO, e, null);
        }

        private InvokerResult checkEvent(UIEvent event) {
            // when the user manually cancels the event, we will take the results as the final criterion.
            if (event.isCancelled()) {
                return event.result();
            }
            switch (event.result()) {
                case PASS: {
                    // pass indicates the event has been successfully handled,
                    // we needs return success to interrupt the event chain.
                    return InvokerResult.SUCCESS;
                }
                case FAIL: {
                    // in normal case we need to pass events to the next window,
                    // but some special cases the window need exclusive the event handler.
                    if (window.shouldPassEventToNextWindow(event)) {
                        event.setResult(InvokerResult.PASS);
                        return InvokerResult.PASS;
                    }
                }
            }
            return event.result();
        }

        private InvokerResult applyKeyEvent(UIEvent event, BiConsumer<UIResponder, UIEvent> consumer) {
            UIResponder[] responders = {window.firstInputResponder, window.focusedResponder, window.hoveredResponder, window};
            for (var responder : responders) {
                if (responder != null) {
                    consumer.accept(responder, event);
                    break;
                }
            }
            return checkEvent(event);
        }

        private static void applyRender(UIView view, CGPoint mousePos, int depth, CGGraphicsContext context) {
            if (view.isHidden()) {
                return;
            }
            var layer = view.layer();
            var presentation = view._presentation;
            var center = presentation.center();
            var bounds = presentation.bounds();
            var transform = presentation.transform();
            var x = center.x;
            var y = center.y;
            var width = bounds.width;
            var height = bounds.height;
            if (transform != CGAffineTransform.IDENTITY) { // fast check
                var size = new CGSize(width, height);
                size.apply(transform);
                width = size.width;
                height = size.height;
            }
            x -= width * 0.5f;
            y -= height * 0.5f;
            mousePos = new CGPoint(mousePos.x - x, mousePos.y - y);
            var isOpaque = view.isOpaque();
            var needClips = view.isClipBounds();
            if (needClips) {
                var clipBox = UIScreen.convertRectFromView(bounds, view);
                var cornerRadius = layer.cornerRadius();
                if (cornerRadius != 0) {
                    var cornerBox = UIScreen.convertRectFromView(new CGRect(0, 0, cornerRadius, cornerRadius), view);
                    context.addClipPath(clipBox, cornerBox.width());
                } else {
                    context.addClipPath(clipBox);
                }
            }
            context.saveGraphicsState();
            context.translateCTM(x - bounds.x, y - bounds.y, view.zIndex());
            if (!transform.isIdentity()) {
                context.concatenateCTM(transform);
            }
            context.strokeDebugRect(bounds, depth);
            if (!isOpaque) {
                context.beginTransparencyLayer();
            }
            view.layerWillDraw(context);
            var backgroundColor = view.backgroundColor();
            if (backgroundColor != null) {
                context.fillRect(bounds, backgroundColor);
            }
            if (layer.borderWidth() != 0) {
                context.strokeRect(bounds, layer.borderWidth(), layer.borderColor());
            }
            view.render(mousePos, context);
            for (var subview : view.subviews()) {
                // when the clipLayer is enabled, we will not render the views of the out of size.
                if (needClips && !bounds.intersects(subview.frame())) {
                    continue;
                }
                applyRender(subview, mousePos, depth + 1, context);
            }
            view.layerDidDraw(context);
            if (!isOpaque) {
                context.endTransparencyLayer();
            }
            context.restoreGraphicsState();
            if (needClips) {
                context.removeClipPath();
            }
        }

        private static void applyAnimationPre() {
            CATransaction._updateAnimations(ObjectUtilsImpl.currentMediaTime());
        }

        private static UIView findFirstResponder(CGPoint mousePos, UIEvent event, UIView view) {
            var frame = view.frame();
            var point = mousePos.offset(-frame.x, -frame.y);
            return view.hitTest(point, event);
        }

        private static UIView findFocusedResponder(UIView view, @Nullable UIView currentView, LBSIterator<UIView> iterator, boolean ignoreSelf, boolean backToTop) {
            var subviews = view.subviews();
            for (var subview : iterator.remaining(subviews, currentView)) {
                if (ignoreSelf && subview == currentView) {
                    continue;
                }
                if (subview != currentView && subview.canBecomeFocused()) {
                    return subview;
                }
                var focusedResponder = findFocusedResponder(subview, null, iterator, false, false);
                if (focusedResponder != null && subview.shouldBecomeFocused(focusedResponder)) {
                    return focusedResponder;
                }
            }
            if (backToTop) {
                var superView = view.superview();
                if (superView != null) {
                    var focusedResponder = findFocusedResponder(superView, view, iterator, true, true);
                    if (focusedResponder != null) {
                        return focusedResponder;
                    }
                }
            }
            for (var subview : iterator.skipping(subviews, currentView)) {
                if (subview != currentView && subview.canBecomeFocused()) {
                    return subview;
                }
                var focusedResponder = findFocusedResponder(subview, null, iterator, false, false);
                if (focusedResponder != null) {
                    return focusedResponder;
                }
            }
            return null;
        }

        protected static UIView findTooltipResponder(UIView view, @Nullable UIView currentView, CGPoint mousePos, UIEvent event) {
            if (currentView == null) {
                return null;
            }
            var frame = view.frame();
            var point = mousePos.offset(-frame.x, -frame.y);
            for (var subview : currentView._invertedSubviews()) {
                if (subview.tooltip() != null && !subview.isHidden()) {
                    if (subview.pointInside(view.convertPointToView(point, subview), event)) {
                        return subview;
                    }
                }
            }
            return currentView;
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

