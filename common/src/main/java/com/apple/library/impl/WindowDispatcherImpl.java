package com.apple.library.impl;

import com.apple.library.coregraphics.CGGraphicsContext;
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

    public void layout(int width, int height) {
    }

    public void render(CGGraphicsContext context) {
    }

    public InvokerResult keyUp(int key, int i, int j) {
        return InvokerResult.PASS;
    }

    public InvokerResult keyDown(int key, int i, int j) {
        return InvokerResult.PASS;
    }

    public InvokerResult charTyped(int key, int i, int j) {
        return InvokerResult.PASS;
    }

    public InvokerResult mouseDown(double mouseX, double mouseY, int button) {
        return InvokerResult.PASS;
    }

    public InvokerResult mouseMoved(double mouseX, double mouseY, int button) {
        return InvokerResult.PASS;
    }

    public InvokerResult mouseWheel(double mouseX, double mouseY, double delta) {
        return InvokerResult.PASS;
    }

    public InvokerResult mouseUp(double mouseX, double mouseY, int button) {
        return InvokerResult.PASS;
    }

    public boolean mouseIsInside(double mouseX, double mouseY, int button) {
        return false;
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
