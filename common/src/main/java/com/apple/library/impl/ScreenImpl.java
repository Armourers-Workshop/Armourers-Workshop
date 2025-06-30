package com.apple.library.impl;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class ScreenImpl {

    public static CGRect bounds() {
        var w = EnvironmentManager.getClient().getWindow();
        return new CGRect(w.getX(), w.getY(), w.getGuiScaledWidth(), w.getGuiScaledHeight());
    }

    public static CGRect nativeBounds() {
        var w = EnvironmentManager.getClient().getWindow();
        return new CGRect(w.getX(), w.getY(), w.getWidth(), w.getHeight());
    }

    public static float nativeScale() {
        return (float) EnvironmentManager.getClient().getWindow().getGuiScale();
    }

    public static CGPoint nativeMousePos() {
        var x = EnvironmentManager.getClient().mouseHandler.xpos();
        var y = EnvironmentManager.getClient().mouseHandler.ypos();
        return new CGPoint((int) x, (int) y);
    }
}

