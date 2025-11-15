package com.apple.library.impl;

public class PropertyImpl<T> {

    private T value;

    public PropertyImpl(T value) {
        this.value = value;
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
