package com.apple.library.uikit;

import com.apple.library.impl.FontImpl;
import com.apple.library.impl.ObjectImpl;

@SuppressWarnings("unused")
public class UIFont extends FontImpl {

    private static final UIFont DEFAULT = new UIFont(FontImpl.defaultFont(), 9);

    private final float fontSize;

    private UIFont(Object impl, float size) {
        super(impl, size);
        this.fontSize = size;
    }

    public UIFont(UIFont font, float size) {
        this(font.impl(), size);
    }

    public static UIFont systemFont() {
        return DEFAULT;
    }

    public static UIFont systemFont(float size) {
        return new UIFont(DEFAULT, size);
    }

    public float fontSize() {
        return fontSize;
    }

    @Override
    public String toString() {
        return ObjectImpl.makeDescription(this, "fontSize", fontSize, "lineHeight", lineHeight());
    }
}
