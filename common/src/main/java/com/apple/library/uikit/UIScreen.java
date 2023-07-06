package com.apple.library.uikit;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.ScreenManagerImpl;

public class UIScreen {

    private static final UIScreen MAIN = new UIScreen();

    public static UIScreen mainScreen() {
        return MAIN;
    }

    public CGRect nativeBounds() {
        return ScreenManagerImpl.nativeBounds();
    }

    public float nativeScale() {
        return ScreenManagerImpl.nativeScale();
    }

}
