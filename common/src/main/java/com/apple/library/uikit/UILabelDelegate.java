package com.apple.library.uikit;

import java.util.Map;

public interface UILabelDelegate {

    default void labelWillClickAttributes(UILabel label, Map<String, ?> attributes) {
    }
}
