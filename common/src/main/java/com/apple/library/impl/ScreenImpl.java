package com.apple.library.impl;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.mojang.blaze3d.platform.Window;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class ScreenImpl {

    public static CGRect nativeBounds() {
        Window w = Minecraft.getInstance().getWindow();
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

