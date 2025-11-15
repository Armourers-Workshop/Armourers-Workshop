package com.apple.library.impl.event;


public interface InputKeyEvent {

    int code();

    int modifiers();

    default boolean is(int code) {
        return code == code();
    }
}
