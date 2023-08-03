package com.apple.library.uikit;

@SuppressWarnings("unused")
public interface UIScrollViewDelegate {

    default void scrollViewDidScroll(UIScrollView scrollView) {
    }
}
