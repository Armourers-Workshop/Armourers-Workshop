package moe.plushie.armourers_workshop.common.skin.data;

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
    
    public T getValue(SkinProperties properties) {
        return (T) properties.getProperty(key, defaultValue);
    }
    
    public void setValue(SkinProperties properties, T value) {
        properties.setProperty(key, value);
    }
    
    public void clearValue(SkinProperties properties) {
        properties.removeProperty(key);
    }
    
    public T getValue(SkinProperties properties, int index) {
        return (T) properties.getProperty(key + String.valueOf(index), defaultValue);
    }
    
    public void setValue(SkinProperties properties, T value, int index) {
        properties.setProperty(key + String.valueOf(index), value);
    }
    
    public void clearValue(SkinProperties properties, int index) {
        properties.removeProperty(key + String.valueOf(index));
    }
}
