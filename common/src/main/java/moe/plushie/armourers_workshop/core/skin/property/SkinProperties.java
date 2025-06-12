package moe.plushie.armourers_workshop.core.skin.property;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenProperties;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SkinProperties extends OpenProperties implements ISkinProperties {

    public static final SkinProperties EMPTY = new SkinProperties();

    public static final IDataCodec<SkinProperties> CODEC = IDataCodec.COMPOUND_TAG.xmap(SkinProperties::new, SkinProperties::serializeNBT);

    public SkinProperties() {
        super();
    }

    public SkinProperties(CompoundTag tag) {
        this();
        this.readFromNBT(tag);
    }

    protected SkinProperties(LinkedHashMap<String, Object> properties) {
        super(properties);
    }

    @Override
    public <T> T get(ISkinProperty<T> property) {
        var value = getOrDefault(property.key(), property.defaultValue());
        return Objects.unsafeCast(value);
    }

    @Override
    public <T> void put(ISkinProperty<T> property, T value) {
        if (shouldRemoveDefaultValues() && Objects.equals(value, property.defaultValue())) {
            remove(property.key());
        } else {
            put(property.key(), value);
        }
    }

    @Override
    public <T> void remove(ISkinProperty<T> property) {
        remove(property.key());
    }

    @Override
    public <T> boolean containsKey(ISkinProperty<T> property) {
        return containsKey(property.key());
    }

    @Override
    public <T> boolean containsValue(ISkinProperty<T> property) {
        return containsValue(property.key());
    }

    public boolean shouldRemoveDefaultValues() {
        return true;
    }

    public ArrayList<String> getPropertiesList() {
        var list = new ArrayList<String>();
        for (int i = 0; i < properties.size(); i++) {
            var key = (String) properties.keySet().toArray()[i];
            list.add(key + ":" + properties.get(key));
        }
        return list;
    }

    @Override
    public OpenProperties empty() {
        return new SkinProperties();
    }

    @Override
    public SkinProperties copy() {
        return new SkinProperties(new LinkedHashMap<>(properties));
    }

    public SkinProperties slice(String index) {
        return new SkinProperties.Stub(this, index);
    }

    public static class Stub extends SkinProperties {
        private final String index;

        public Stub(SkinProperties parent, String index) {
            super(parent.properties);
            this.index = index;
        }

        @Override
        public <T> void put(ISkinProperty<T> property, T value) {
            put(resolveKey(property), value);
        }

        @Override
        public <T> void remove(ISkinProperty<T> property) {
            remove(resolveKey(property));
        }

        @Override
        public <T> T get(ISkinProperty<T> property) {
            var value = getOrDefault(resolveKey(property), property.defaultValue());
            return Objects.unsafeCast(value);
        }

        @Override
        public <T> boolean containsKey(ISkinProperty<T> property) {
            return containsKey(resolveKey(property));
        }

        @Override
        public <T> boolean containsValue(ISkinProperty<T> property) {
            return containsValue(resolveKey(property));
        }

        private <T> String resolveKey(ISkinProperty<T> property) {
            return property.key() + index;
        }
    }

    public static class Increment extends SkinProperties {

        @Override
        public boolean shouldRemoveDefaultValues() {
            return false; // increments require record all changes.
        }
    }
}
