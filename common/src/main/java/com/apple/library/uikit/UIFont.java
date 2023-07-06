package com.apple.library.uikit;

import com.apple.library.impl.FontImpl;
import net.minecraft.client.gui.Font;

public class UIFont implements FontImpl {

    private final float lineHeight;
    private final Font font;
    private final float fontSize;

    public UIFont(Font font, float size) {
        this.font = font;
        this.fontSize = size;
        this.lineHeight = font.lineHeight;
    }

    public UIFont(UIFont font, float size) {
        this(font.font, size);
    }

    public static UIFont systemFont() {
        return SYSTEM_FONT;
    }

    public float lineHeight() {
        return lineHeight;
    }

    public float fontSize() {
        return fontSize;
    }

    public Font font() {
        return font;
    }
}
