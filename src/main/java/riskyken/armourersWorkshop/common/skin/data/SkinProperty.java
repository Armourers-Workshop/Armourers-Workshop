package riskyken.armourersWorkshop.common.skin.data;

import riskyken.armourersWorkshop.api.common.skin.data.ISkinProperties;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinProperty;

public class SkinProperty<T> implements ISkinProperty<T> {

    private String key;
    private T defaultValue;
    private T property;

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

    public void clearValue(ISkinProperties properties) {
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
