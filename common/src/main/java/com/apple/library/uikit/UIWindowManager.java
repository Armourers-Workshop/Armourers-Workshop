package com.apple.library.uikit;

import com.apple.library.impl.WindowManagerImpl;

public class UIWindowManager extends WindowManagerImpl {

    private static UIWindowManager EMPTY = new UIWindowManager();
    private static UIWindowManager INSTANCE;

    public static UIWindowManager sharedManager() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        return EMPTY;
    }

    @Override
    public void init() {
        INSTANCE = this;
        super.init();
    }

    @Override
    public void deinit() {
        super.deinit();
        INSTANCE = null;
    }
}
