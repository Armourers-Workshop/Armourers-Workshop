package moe.plushie.armourers_workshop.api.skin.property;

import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;

public interface ISkinProperties {

    <T> void put(SkinProperty<T> property, T value);

    <T> void remove(SkinProperty<T> property);

    <T> boolean containsKey(SkinProperty<T> property);

    <T> boolean containsValue(SkinProperty<T> property);

    <T> T get(SkinProperty<T> property);
}


