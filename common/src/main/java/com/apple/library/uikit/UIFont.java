package com.apple.library.uikit;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class UIFont {

    private final int lineHeight;
    private final Font font;

    public UIFont(Font font) {
        this.font = font;
        this.lineHeight = font.lineHeight;
    }

    @Environment(value = EnvType.CLIENT)
    public static UIFont system() {
        return new UIFont(Minecraft.getInstance().font);
    }

    public int lineHeight() {
        return lineHeight;
    }

    public Font font() {
        return font;
    }
}
