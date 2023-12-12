package com.apple.library.uikit;

public interface UIMenuControllerDelegate {

    default void menuControllerDidShow(UIMenuController menuController) {
    }

    default void menuControllerDidDismiss(UIMenuController menuController) {
    }

    default boolean menuControllerShouldSelectItem(UIMenuController menuController, UIMenuItem menuItem) {
        return true;
    }

    default void menuControllerDidSelectItem(UIMenuController menuController, UIMenuItem menuItem) {
    }
}
