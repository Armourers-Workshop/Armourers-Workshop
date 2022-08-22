package com.apple.library.impl;

import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class DelegateImpl<T> {

    private final T def;
    private WeakReference<T> ref;

    private DelegateImpl(T def) {
        this.def = def;
    }

    public static <T> DelegateImpl<T> of(T defaultValue) {
        return new DelegateImpl<>(defaultValue);
    }

    public T invoker() {
        T value = get();
        if (value != null) {
            return value;
        }
        return def;
    }

    @Nullable
    public T get() {
        if (ref != null) {
            return ref.get();
        }
        return null;
    }

    public void set(T value) {
        if (value != null) {
            ref = new WeakReference<>(value);
        } else {
            ref = null;
        }
    }
}
