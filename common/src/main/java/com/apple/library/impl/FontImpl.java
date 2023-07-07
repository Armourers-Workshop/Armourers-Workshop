package com.apple.library.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

@Environment(EnvType.CLIENT)
public abstract class FontImpl {

    private final Font font;
    private final float lineHeight;

    public FontImpl(Object impl) {
        this.font = (Font) impl;
        this.lineHeight = font.lineHeight;
    }

    protected static Font defaultFont() {
        return Minecraft.getInstance().font;
    }

    public float lineHeight() {
        return lineHeight;
    }

    public Font impl() {
        return font;
    }
}
