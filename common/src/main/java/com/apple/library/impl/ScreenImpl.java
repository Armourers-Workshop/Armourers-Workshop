package com.apple.library.impl;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

import manifold.ext.rt.api.auto;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class ScreenImpl {

    public static CGRect nativeBounds() {
        auto w = Minecraft.getInstance().getWindow();
        return new CGRect(w.getX(), w.getY(), w.getWidth(), w.getHeight());
    }

    public static float nativeScale() {
        return (float) Minecraft.getInstance().getWindow().getGuiScale();
    }

    public static CGPoint nativeMousePos() {
        double x = Minecraft.getInstance().mouseHandler.xpos();
        double y = Minecraft.getInstance().mouseHandler.ypos();
        return new CGPoint((int) x, (int) y);
    }
}

