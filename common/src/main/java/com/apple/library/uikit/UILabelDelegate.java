package com.apple.library.uikit;

import java.util.Map;

@SuppressWarnings("unused")
public interface UILabelDelegate {

    default void labelWillClickAttributes(UILabel label, Map<String, ?> attributes) {
    }
}
