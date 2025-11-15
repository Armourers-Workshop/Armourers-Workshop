package com.apple.library.impl;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.impl.event.InputKeyEvent;
import com.apple.library.impl.event.InputMouseEvent;
import com.apple.library.uikit.UIView;

public class WindowDispatcherImpl {

    public static WindowDispatcherImpl BACKGROUND = new WindowDispatcherImpl(-200);
    public static WindowDispatcherImpl FOREGROUND = new WindowDispatcherImpl(200);
    public static WindowDispatcherImpl OVERLAY = new WindowDispatcherImpl(800);

    private int level;

    public WindowDispatcherImpl() {
    }

    public WindowDispatcherImpl(int level) {
        this.level = level;
    }

    public void init() {
    }

    public void deinit() {
    }

    public void tick() {
    }

    public void layout(CGSize size) {
    }

    public void render(CGGraphicsContext context) {
    }

    public InvokerResult keyUp(InputKeyEvent event) {
        return InvokerResult.PASS;
    }

    public InvokerResult keyDown(InputKeyEvent event) {
        return InvokerResult.PASS;
    }

    public InvokerResult charTyped(InputKeyEvent event) {
        return InvokerResult.PASS;
    }

    public InvokerResult mouseDown(InputMouseEvent event, boolean bl) {
        return InvokerResult.PASS;
    }

    public InvokerResult mouseUp(InputMouseEvent event) {
        return InvokerResult.PASS;
    }

    public InvokerResult mouseMoved(InputMouseEvent event) {
        return InvokerResult.PASS;
    }

    public InvokerResult mouseWheel(InputMouseEvent event) {
        return InvokerResult.PASS;
    }

    public InvokerResult changeKeyView(boolean bl) {
        return InvokerResult.PASS;
    }

    public InvokerResult mouseIsInside(InputMouseEvent event) {
        return InvokerResult.PASS;
    }

    public UIView firstInputResponder() {
        return null;
    }

    public UIView firstTooltipResponder() {
        return null;
    }

    public int level() {
        return level;
    }
}
