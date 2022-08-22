package com.apple.library.uikit;

import net.minecraft.client.gui.Font;

public class UIFont {

    private final int lineHeight;
    private final Font font;

    public UIFont(Font font) {
        this.font = font;
        this.lineHeight = font.lineHeight;
    }

    public int lineHeight() {
        return lineHeight;
    }

    public Font font() {
        return font;
    }
}
