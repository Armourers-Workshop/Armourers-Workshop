package moe.plushie.armourers_workshop.common.skin.data;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperties;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperty;

public class SkinProperty<T> implements ISkinProperty<T> {

    private String key;
    private T defaultValue;

    public SkinProperty(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public String getKey() {
        return key;
    }

    public T getValue(ISkinProperties properties) {
        return (T) properties.getProperty(key, defaultValue);
    }

    public void setValue(ISkinProperties properties, T value) {
        properties.setProperty(key, value);
    }

    public void clearValue(SkinProperties properties) {
        properties.removeProperty(key);
    }

    public T getValue(ISkinProperties properties, int index) {
        if (properties.haveProperty(key + String.valueOf(index))) {
            return (T) properties.getProperty(key + String.valueOf(index), defaultValue);
        } else if (properties.haveProperty(key)) {
            return (T) properties.getProperty(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public void setValue(ISkinProperties properties, T value, int index) {
        properties.setProperty(key + String.valueOf(index), value);
    }

    public void clearValue(ISkinProperties properties, int index) {
        properties.removeProperty(key + String.valueOf(index));
    }
}
