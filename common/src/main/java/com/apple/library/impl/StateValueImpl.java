package com.apple.library.impl;

import com.apple.library.uikit.UIControl;

import java.util.HashMap;

public class StateValueImpl<T> {

    private T currentValue;
    private final HashMap<Integer, T> values = new HashMap<>();

    public T currentValue() {
        return currentValue;
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public void setCurrentState(int state) {
        this.currentValue = eval(state);
    }

    public T valueForState(int state) {
        return values.get(state);
    }

    public void setValueForState(T value, int state) {
        values.put(state, value);
    }

    private T eval(int state) {
        if (values.isEmpty()) {
            return null;
        }
        var value = values.get(state);
        if (value != null) {
            return value;
        }
        // gradually remove all flags when not found value.
        for (var i = 0; i < UIControl.State.ALL; ++i) {
            var resolvedState = state & ~i;
            if (resolvedState != state) {
                value = values.get(resolvedState);
            }
            if (value != null) {
                return value;
            }
        }
        value = values.get(UIControl.State.ALL);
        if (value != null) {
            return value;
        }
        return values.get(UIControl.State.NORMAL);
    }
}
