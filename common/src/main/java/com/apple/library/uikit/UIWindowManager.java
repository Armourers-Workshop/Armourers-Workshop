package com.apple.library.uikit;

import com.apple.library.impl.WindowManagerImpl;

@SuppressWarnings("unused")
public class UIWindowManager extends WindowManagerImpl {

    @Override
    public void addWindow(UIWindow window) {
        super.addWindow(window);
        window.setWindowManager(this);
    }

    @Override
    public void removeWindow(UIWindow window) {
        super.removeWindow(window);
        window.setWindowManager(null);
    }
}
