package moe.plushie.armourers_workshop.core.skin.molang.impl;

import java.util.Objects;

/**
 * A key path for a.b.c style
 */
public final class KeyPath {

    private final String name;
    private final KeyPath child;

    public KeyPath(String name) {
        this.name = name;
        this.child = null;
    }

    public KeyPath(String name, KeyPath child) {
        this.name = name;
        this.child = child;
    }

    public static KeyPath of(String name) {
        return new KeyPath(name);
    }

    public static KeyPath parse(String name) {
        var keys = name.split("\\.");
        var index = keys.length;
        if (index <= 1) {
            return of(name);
        }
        var key = new KeyPath(keys[--index]);
        while (index > 0) {
            key = new KeyPath(keys[--index], key);
        }
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyPath that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(child, that.child);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, child);
    }

    @Override
    public String toString() {
        if (child != null) {
            return name + "." + child;
        }
        return name;
    }

    public String getName() {
        return name;
    }

    public KeyPath getChild() {
        return child;
    }
}
