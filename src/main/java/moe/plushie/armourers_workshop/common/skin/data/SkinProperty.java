package moe.plushie.armourers_workshop.common.skin.data;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperty;

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
    
    public T getValue(SkinProperties properties) {
        return (T) properties.getProperty(key, defaultValue);
    }
    
    public void setValue(SkinProperties properties, T value) {
        properties.setProperty(key, value);
    }
}
