package com.apple.library.impl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WeakDispatcherImpl<V> {

    private ArrayList<Entry> accessing = null;
    private ArrayList<Entry> entries = new ArrayList<>();

    public <T> void add(T target, BiConsumer<T, V> listener) {
        writeQueue().add(new Entry(target, listener));
    }

    public <T> void remove(T target) {
        writeQueue().removeIf(e -> e.target.get() == target);
    }

    public void send(V value) {
        accessing = entries;
        entries.forEach(e -> e.consumer.accept(value));
        accessing = null;
    }

    private ArrayList<Entry> writeQueue() {
        if (accessing != entries) {
            return entries;
        }
        ArrayList<Entry> entries = new ArrayList<>(this.entries);
        this.entries = entries;
        return entries;
    }

    public class Entry {

        final WeakReference<?> target;
        final Consumer<V> consumer;

        <T> Entry(T target, BiConsumer<T, V> listener) {
            WeakReference<T> ref = new WeakReference<>(target);
            this.target = ref;
            this.consumer = value -> {
                T target1 = ref.get();
                if (target1 != null) {
                    listener.accept(target1, value);
                }
            };
        }
    }
}
