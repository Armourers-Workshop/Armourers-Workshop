package com.apple.library.impl;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public class StringEncoderImpl {

    private int count = 0;

    private Style style = Style.EMPTY;
    private StringBuilder builder = new StringBuilder();

    private final TextComponent component = new TextComponent("");

    public boolean append(int index, Style newStyle, int ch) {
        if (style != newStyle && count != 0) {
            flush();
            style = newStyle;
            builder = new StringBuilder();
            count = 0;
        }
        count += 1;
        builder.appendCodePoint(ch);
        return true;
    }

    public Component encode() {
        if (count != 0) {
            flush();
        }
        return component;
    }

    private void flush() {
        String value = builder.toString();
        component.append(new TextComponent(value).setStyle(style));
    }
}
