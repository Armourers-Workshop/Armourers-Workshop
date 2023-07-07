package com.apple.library.uikit;

import com.apple.library.impl.FontImpl;

public class UIFont extends FontImpl {

    private static final UIFont DEFAULT = new UIFont(FontImpl.defaultFont(), 9);
    private final float fontSize;

    public UIFont(Object impl, float size) {
        super(impl);
        this.fontSize = size;
    }

    public UIFont(UIFont font, float size) {
        this(font.impl(), size);
    }

    public static UIFont systemFont() {
        return DEFAULT;
    }

    public float fontSize() {
        return fontSize;
    }
}
