package moe.plushie.armourers_workshop.core.utils;

import java.lang.reflect.Field;

public class FieldAccessor<T> {

    private final Object owner;
    private final Field field;

    private FieldAccessor(Object owner, Field field) {
        this.owner = owner;
        this.field = field;
    }

    public static <T> FieldAccessor<T> create(String name) {
        try {
            var index = name.lastIndexOf(".");
            var clazz = Class.forName(name.substring(0, index));
            var field = clazz.getDeclaredField(name.substring(index + 1));
            return new FieldAccessor<>(null, field);
        } catch (Exception e) {
            // noinspection unchecked
            return (FieldAccessor<T>) Placeholder.PLACEHOLDER;
        }
    }

    public T get() {
        try {
            // noinspection unchecked
            return (T) field.get(owner);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void set(T value) {
        try {
            field.set(owner, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class Placeholder<T> extends FieldAccessor<T> {

        private static final Placeholder<?> PLACEHOLDER = new Placeholder<>();

        private Placeholder() {
            super(null, null);
        }

        @Override
        public T get() {
            return null;
        }

        @Override
        public void set(T value) {
            // nop.
        }
    }
}
