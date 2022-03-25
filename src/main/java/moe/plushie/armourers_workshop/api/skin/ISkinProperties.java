package moe.plushie.armourers_workshop.api.skin;

import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;

public interface ISkinProperties {

    //    public void removeProperty(String key);
//
//    public void setProperty(String key, Object value);
//
//    public String getPropertyString(String key, String defaultValue);
//
//    public int getPropertyInt(String key, int defaultValue);
//
//    public double getPropertyDouble(String key, double defaultValue);
//
//    public Boolean getPropertyBoolean(String key, Boolean defaultValue);
//
//    public Object getProperty(String key, Object defaultValue);
//
//    public boolean haveProperty(String key);
    <T> void put(SkinProperty<T> property, T value);

    <T> void remove(SkinProperty<T> property);

    <T> boolean containsKey(SkinProperty<T> property);

    <T> boolean containsValue(SkinProperty<T> property);

    <T> T get(SkinProperty<T> property);
}


