package com.apple.library.impl;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
public class ScreenImpl {

    public static CGRect bounds() {
        var w = Minecraft.getInstance().getWindow();
        return new CGRect(w.getX(), w.getY(), w.getGuiScaledWidth(), w.getGuiScaledHeight());
    }

    public static CGRect nativeBounds() {
        var w = Minecraft.getInstance().getWindow();
        return new CGRect(w.getX(), w.getY(), w.getWidth(), w.getHeight());
    }

    public static float nativeScale() {
        return (float) Minecraft.getInstance().getWindow().getGuiScale();
    }

    public static CGPoint nativeMousePos() {
        var x = Minecraft.getInstance().mouseHandler.xpos();
        var y = Minecraft.getInstance().mouseHandler.ypos();
        return new CGPoint((int) x, (int) y);
    }
}

