package moe.plushie.armourers_workshop.api.skin.property;


public interface ISkinProperties {

    <T> void put(ISkinProperty<T> property, T value);

    <T> void remove(ISkinProperty<T> property);

    <T> boolean containsKey(ISkinProperty<T> property);

    <T> boolean containsValue(ISkinProperty<T> property);

    <T> T get(ISkinProperty<T> property);
}


