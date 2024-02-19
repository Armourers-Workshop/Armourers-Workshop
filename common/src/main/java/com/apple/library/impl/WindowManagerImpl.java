package com.apple.library.impl;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGSize;
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
    private double lastMouseX;
    private double lastMouseY;
    private int lastMouseButton;
    private int lastFocusVersion = 0;
    private int lastFocusVersionOld = 0;

    protected final Queue<WindowDispatcherImpl> dispatchers = new Queue<>();

    public WindowManagerImpl() {
        this.dispatchers.add(WindowDispatcherImpl.BACKGROUND);
        this.dispatchers.add(WindowDispatcherImpl.FOREGROUND);
        this.dispatchers.add(WindowDispatcherImpl.OVERLAY);
    }

    public void init() {
        dispatchers.forEach(WindowDispatcherImpl::init);
        isCalledInit = true;
    }

    public void deinit() {
        dispatchers.forEach(WindowDispatcherImpl::deinit);
        dispatchers.removeAll();
    }

    public void addWindow(UIWindow window) {
        UIWindow.Dispatcher dispatcher = new UIWindow.Dispatcher(window);
        dispatchers.add(dispatcher);
        if (isCalledInit) {
            dispatcher.init();
        }
        if (lastLayoutSize != null) {
            dispatcher.layout(lastLayoutSize);
        }
        _setNeedsUpdateFocus();
    }

    public void removeWindow(UIWindow window) {
        dispatchers.removeIf(dispatcher -> {
            UIWindow.Dispatcher dispatcher1 = ObjectUtilsImpl.safeCast(dispatcher, UIWindow.Dispatcher.class);
            if (dispatcher1 != null && dispatcher1.window == window) {
                dispatcher1.deinit();
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
        dispatchers.forEach(WindowDispatcherImpl::tick);
    }

    public void layout(float width, float height) {
        CGSize size = new CGSize(width, height);
        dispatchers.forEach(dispatcher -> dispatcher.layout(size));
        lastLayoutSize = size;
    }

    public void render(CGGraphicsContext context, RenderInvoker foreground, RenderInvoker background, RenderInvoker overlay) {
        float partialTicks = context.state().partialTicks();
        int mouseX = (int) context.state().mousePos().getX();
        int mouseY = (int) context.state().mousePos().getY();
        // we need to display a custom tooltip, so must cancel the original tooltip render,
        // we need reset mouse to impossible position to fool the original tooltip render.
        UIView tooltipResponder = firstTooltipResponder();
        if (tooltipResponder != null) {
            mouseX = Integer.MIN_VALUE;
            mouseY = Integer.MIN_VALUE;
        }
        for (WindowDispatcherImpl dispatcher : dispatchers) {
            dispatcher.render(context);
            if (dispatcher == WindowDispatcherImpl.BACKGROUND) {
                background.invoke(mouseX, mouseY, partialTicks, context);
            }
            if (dispatcher == WindowDispatcherImpl.FOREGROUND) {
                foreground.invoke(mouseX, mouseY, partialTicks, context);
            }
            if (dispatcher == WindowDispatcherImpl.OVERLAY) {
                overlay.invoke(mouseX, mouseY, partialTicks, context);
                renderTooltip(tooltipResponder, context);
            }
        }
    }

    private void renderTooltip(UIView tooltipResponder, CGGraphicsContext context) {
        if (tooltipResponder == null) {
            return;
        }
        Object tooltip = tooltipResponder.tooltip();
        if (tooltip != null) {
            context.saveGraphicsState();
            context.translateCTM(0, 0, 400);
            context.drawTooltip(tooltip, tooltipResponder.bounds());
            context.restoreGraphicsState();
        }
    }

    private void updateLastFocus(double mouseX, double mouseY, int button) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        lastMouseButton = button;
        lastFocusVersionOld = lastFocusVersion;
    }

    private void updateLastFocusIfNeeded() {
        // send the move event again.
        if (lastFocusVersion != lastFocusVersionOld) {
            mouseMoved(lastMouseX, lastMouseY, lastMouseButton, (mouseX, mouseY, button) -> true);
        }
    }

    public boolean keyUp(int key, int i, int j, Invoker<Integer, Integer, Integer, Boolean> invoker) {
        return dispatchers.invoke(key, i, j, invoker, WindowDispatcherImpl::keyUp);
    }

    public boolean keyDown(int key, int i, int j, Invoker<Integer, Integer, Integer, Boolean> invoker) {
        return dispatchers.invoke(key, i, j, invoker, WindowDispatcherImpl::keyDown);
    }

    public boolean charTyped(int key, int i, int j, Invoker<Integer, Integer, Integer, Boolean> invoker) {
        return dispatchers.invoke(key, i, j, invoker, WindowDispatcherImpl::charTyped);
    }

    public boolean mouseDown(double mouseX, double mouseY, int button, Invoker<Double, Double, Integer, Boolean> invoker) {
        return dispatchers.invoke(mouseX, mouseY, button, invoker, WindowDispatcherImpl::mouseDown);
    }

    public boolean mouseUp(double mouseX, double mouseY, int button, Invoker<Double, Double, Integer, Boolean> invoker) {
        return dispatchers.invoke(mouseX, mouseY, button, invoker, WindowDispatcherImpl::mouseUp);
    }

    public boolean mouseMoved(double mouseX, double mouseY, int button, Invoker<Double, Double, Integer, Boolean> invoker) {
        updateLastFocus(mouseX, mouseY, button);
        return dispatchers.invoke(mouseX, mouseY, button, invoker, WindowDispatcherImpl::mouseMoved);
    }

    public boolean mouseWheel(double mouseX, double mouseY, CGPoint delta, Invoker<Double, Double, CGPoint, Boolean> invoker) {
        return dispatchers.invoke(mouseX, mouseY, delta, invoker, WindowDispatcherImpl::mouseWheel);
    }

    public boolean mouseIsInside(double mouseX, double mouseY, int button) {
        return dispatchers.test(dispatcher -> dispatcher.mouseIsInside(mouseX, mouseY, button));
    }

    public boolean changeKeyView(boolean bl) {
        return dispatchers.test(dispatcher -> dispatcher.changeKeyView(bl));
    }

    public UIView firstTooltipResponder() {
        return dispatchers.flatMap(WindowDispatcherImpl::firstTooltipResponder);
    }

    public UIView firstInputResponder() {
        return dispatchers.flatMap(WindowDispatcherImpl::firstInputResponder);
    }

    public boolean isTextEditing() {
        return firstInputResponder() instanceof TextInputTraits;
    }

    @FunctionalInterface
    public interface Invoker<A, B, C, U> {
        U invoke(A a, B b, C c);
    }

    @FunctionalInterface
    public interface Invoker4<A, B, C, D, U> {
        U invoke(A a, B b, C c, D d);
    }

    @FunctionalInterface
    public interface RenderInvoker {
        void invoke(int mouseX, int mouseY, float partialTicks, CGGraphicsContext context);
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
                InvokerResult result = provider.apply(value);
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

        public <A, B, C> boolean invoke(A a, B b, C c, Invoker<A, B, C, Boolean> invoker, Invoker4<T, A, B, C, InvokerResult> provider) {
            for (T value : descendingEnum()) {
                InvokerResult ret = provider.invoke(value, a, b, c);
                if (ret.isDecided()) {
                    return ret.conclusion();
                }
            }
            return invoker.invoke(a, b, c);
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
