package com.apple.library.impl;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.impl.event.InputKeyEvent;
import com.apple.library.impl.event.InputMouseEvent;
import com.apple.library.uikit.UIView;
import com.apple.library.uikit.UIWindow;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Predicate;

public class WindowManagerImpl {

    private boolean isCalledInit = false;

    private CGSize lastLayoutSize;
    private InputMouseEvent lastMouseMove;
    private int lastFocusVersion = 0;
    private int lastFocusVersionOld = 0;

    protected final Queue<WindowDispatcherImpl> windows = new Queue<>();

    public WindowManagerImpl() {
        this.windows.add(WindowDispatcherImpl.BACKGROUND);
        this.windows.add(WindowDispatcherImpl.FOREGROUND);
        this.windows.add(WindowDispatcherImpl.OVERLAY);
    }

    public void init() {
        windows.forEach(WindowDispatcherImpl::init);
        isCalledInit = true;
    }

    public void deinit() {
        windows.forEach(WindowDispatcherImpl::deinit);
        windows.removeAll();
    }

    public void addWindow(UIWindow window) {
        var dispatcher = new UIWindow.Dispatcher(window);
        windows.add(dispatcher);
        if (isCalledInit) {
            dispatcher.init();
        }
        if (lastLayoutSize != null) {
            dispatcher.layout(lastLayoutSize);
        }
        _setNeedsUpdateFocus();
    }

    public void removeWindow(UIWindow window) {
        windows.removeIf(dispatcher -> {
            if (dispatcher instanceof UIWindow.Dispatcher windowDispatcher && windowDispatcher.window == window) {
                windowDispatcher.deinit();
                return true;
            }
            return false;
        });
        _setNeedsUpdateFocus();
    }

    public void _setNeedsUpdateFocus() {
        // when remove a window, first tooltip responder maybe change, so we need to recalculate.
        lastFocusVersion += 1;
    }

    public void tick() {
        updateLastFocusIfNeeded();
        windows.forEach(WindowDispatcherImpl::tick);
    }

    public void layout(CGSize size) {
        windows.forEach(dispatcher -> dispatcher.layout(size));
        lastLayoutSize = size;
    }

    public void render(CGGraphicsContext context, RenderInvoker foreground, RenderInvoker background, RenderInvoker overlay) {
        var partialTick = context.param().partialTick();
        var mouseX = (int) context.param().mouseX();
        var mouseY = (int) context.param().mouseY();
        // we need to display a custom tooltip, so must cancel the original tooltip render,
        // we need reset mouse to impossible position to fool the original tooltip render.
        var tooltipResponder = firstTooltipResponder();
        if (tooltipResponder != null) {
            mouseX = Integer.MIN_VALUE;
            mouseY = Integer.MIN_VALUE;
        }
        for (var window : windows) {
            window.render(context);
            if (window == WindowDispatcherImpl.BACKGROUND) {
                background.invoke(mouseX, mouseY, partialTick, context);
            }
            if (window == WindowDispatcherImpl.FOREGROUND) {
                foreground.invoke(mouseX, mouseY, partialTick, context);
            }
            if (window == WindowDispatcherImpl.OVERLAY) {
                renderTooltip(tooltipResponder, context);
                overlay.invoke(mouseX, mouseY, partialTick, context);
            }
        }
    }

    private void renderTooltip(UIView tooltipResponder, CGGraphicsContext context) {
        if (tooltipResponder == null) {
            return;
        }
        var tooltip = tooltipResponder.tooltip();
        if (tooltip != null) {
            context.saveGraphicsState();
            context.translateCTM(0, 0, 400);
            context.drawTooltip(tooltip, tooltipResponder.bounds());
            context.restoreGraphicsState();
        }
    }

    private void updateLastFocus(InputMouseEvent event) {
        lastMouseMove = event;
        lastFocusVersionOld = lastFocusVersion;
    }

    private void updateLastFocusIfNeeded() {
        // send the move event again.
        if (lastMouseMove != null && lastFocusVersion != lastFocusVersionOld) {
            mouseMoved(lastMouseMove, event -> true);
        }
    }

    public <E extends InputKeyEvent> boolean keyUp(E event, Invoker<E, Boolean> invoker) {
        return windows.invoke(event, invoker, WindowDispatcherImpl::keyUp);
    }

    public <E extends InputKeyEvent> boolean keyDown(E event, Invoker<E, Boolean> invoker) {
        return windows.invoke(event, invoker, WindowDispatcherImpl::keyDown);
    }

    public <E extends InputKeyEvent> boolean charTyped(E event, Invoker<E, Boolean> invoker) {
        return windows.invoke(event, invoker, WindowDispatcherImpl::charTyped);
    }

    public <E extends InputMouseEvent> boolean mouseDown(E event, boolean bl, Invoker2<E, Boolean, Boolean> invoker) {
        return windows.invoke(event, bl, invoker, WindowDispatcherImpl::mouseDown);
    }

    public <E extends InputMouseEvent> boolean mouseUp(E event, Invoker<E, Boolean> invoker) {
        return windows.invoke(event, invoker, WindowDispatcherImpl::mouseUp);
    }

    public <E extends InputMouseEvent> boolean mouseMoved(E event, Invoker<E, Boolean> invoker) {
        updateLastFocus(event);
        return windows.invoke(event, invoker, WindowDispatcherImpl::mouseMoved);
    }

    public <E extends InputMouseEvent> boolean mouseWheel(E event, Invoker<E, Boolean> invoker) {
        return windows.invoke(event, invoker, WindowDispatcherImpl::mouseWheel);
    }

    public <E extends InputMouseEvent> boolean mouseIsInside(E event) {
        return windows.test(dispatcher -> dispatcher.mouseIsInside(event));
    }

    public boolean changeKeyView(boolean bl) {
        return windows.test(dispatcher -> dispatcher.changeKeyView(bl));
    }

    public UIView firstTooltipResponder() {
        return windows.flatMap(WindowDispatcherImpl::firstTooltipResponder);
    }

    public UIView firstInputResponder() {
        return windows.flatMap(WindowDispatcherImpl::firstInputResponder);
    }

    public boolean isTextEditing() {
        return firstInputResponder() instanceof TextInputTraits;
    }

    @FunctionalInterface
    public interface Invoker<A, U> {
        U invoke(A a);
    }

    @FunctionalInterface
    public interface Invoker2<A, B, U> {
        U invoke(A a, B b);
    }

    @FunctionalInterface
    public interface Invoker3<A, B, C, U> {
        U invoke(A a, B b, C c);
    }

    @FunctionalInterface
    public interface RenderInvoker {
        void invoke(int mouseX, int mouseY, float partialTick, CGGraphicsContext context);
    }

    public static class Queue<T extends WindowDispatcherImpl> implements Iterable<T> {

        private final LinkedList<T> values = new LinkedList<>();
        private LinkedList<T> readValues = values;

        public void add(T val) {
            values.add(val);
            values.sort(Comparator.comparing(T::level));
            readValues = new LinkedList<>(values);
        }

        public void remove(T val) {
            values.remove(val);
            readValues = new LinkedList<>(values);
        }

        public void removeIf(Predicate<T> val) {
            values.removeIf(val);
            readValues = new LinkedList<>(values);
        }

        public void removeAll() {
            values.clear();
            readValues = new LinkedList<>(values);
        }

        public boolean test(Function<T, InvokerResult> provider) {
            for (T value : descendingEnum()) {
                var result = provider.apply(value);
                if (result.isDecided()) {
                    return result.conclusion();
                }
            }
            return false;
        }

        public <U> U flatMap(Function<T, U> provider) {
            for (T value : descendingEnum()) {
                U ret = provider.apply(value);
                if (ret != null) {
                    return ret;
                }
            }
            return null;
        }

        public <A> boolean invoke(A a, Invoker<A, Boolean> invoker, Invoker2<T, A, InvokerResult> provider) {
            for (T value : descendingEnum()) {
                var ret = provider.invoke(value, a);
                if (ret.isDecided()) {
                    return ret.conclusion();
                }
            }
            return invoker.invoke(a);
        }

        public <A, B> boolean invoke(A a, B b, Invoker2<A, B, Boolean> invoker, Invoker3<T, A, B, InvokerResult> provider) {
            for (T value : descendingEnum()) {
                var ret = provider.invoke(value, a, b);
                if (ret.isDecided()) {
                    return ret.conclusion();
                }
            }
            return invoker.invoke(a, b);
        }

        @Override
        public Iterator<T> iterator() {
            return readValues.iterator();
        }

        public Iterable<T> descendingEnum() {
            return readValues::descendingIterator;
        }
    }
}
