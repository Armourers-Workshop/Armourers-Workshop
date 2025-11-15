package com.apple.library.coregraphics;

import java.util.HashMap;
import java.util.Map;

public class CGGraphicsParameters {

    private final float width;
    private final float height;

    private final float mouseX;
    private final float mouseY;

    private final float partialTicks;

    private final Object context;

    private Map<String, Object> extra;

    public CGGraphicsParameters(float width, float height, float mouseX, float mouseY, float partialTicks, Object context) {
        this.width = width;
        this.height = height;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTicks = partialTicks;
        this.context = context;
    }

    public void set(String key, Object value) {
        if (extra == null) {
            extra = new HashMap<>();
        }
        extra.put(key, value);
    }

    public Object get(String key) {
        if (extra != null) {
            return extra.get(key);
        }
        return null;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    public float mouseX() {
        return mouseX;
    }

    public float mouseY() {
        return mouseY;
    }

    public CGPoint mousePos() {
        return new CGPoint(mouseX, mouseY);
    }

    public float partialTicks() {
        return partialTicks;
    }

    public Object context() {
        return context;
    }
}
