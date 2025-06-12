package moe.plushie.armourers_workshop.core.skin.texture;

import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.api.skin.texture.ISkinTextureProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.io.IOException;

@SuppressWarnings("unused")
public class SkinTextureProperties implements ISkinTextureProperties {

    public static final SkinTextureProperties EMPTY = new SkinTextureProperties();

    private int flags = 0;
    private final SkinProperties storage = new SkinProperties();

    public SkinTextureProperties() {
    }

    public void readFromStream(IInputStream stream) throws IOException {
        flags = stream.readInt();
        if ((flags & 0x80000000) != 0) {
            storage.readFromStream(stream);
        }
    }

    public void writeToStream(IOutputStream stream) throws IOException {
        if (storage.isEmpty()) {
            stream.writeInt(flags);
        } else {
            stream.writeInt(flags | 0x80000000);
            storage.writeToStream(stream);
        }
    }

    public <T> void set(ISkinProperty<T> property, T value) {
        storage.put(property, value);
    }

    public <T> T get(ISkinProperty<T> property) {
        return storage.get(property);
    }

    public void setEmissive(boolean isEmissive) {
        setFlag(0, isEmissive);
    }

    @Override
    public boolean isEmissive() {
        return getFlag(0);
    }

    public void setTranslucent(boolean newValue) {
        setFlag(1, newValue);
    }

    @Override
    public boolean isTranslucent() {
        return getFlag(1);
    }

    public void setSpecular(boolean isSpecular) {
        setFlag(2, isSpecular);
    }

    @Override
    public boolean isSpecular() {
        return getFlag(2);
    }

    public void setNormal(boolean isNormal) {
        setFlag(3, isNormal);
    }

    @Override
    public boolean isNormal() {
        return getFlag(3);
    }

    public void setBlurFilter(boolean isBlurFilter) {
        setFlag(4, isBlurFilter);
    }

    @Override
    public boolean isBlurFilter() {
        return getFlag(4);
    }

    public void setClampToEdge(boolean isClampToEdge) {
        setFlag(5, isClampToEdge);
    }

    @Override
    public boolean isClampToEdge() {
        return getFlag(5);
    }

    public SkinTextureProperties copy() {
        var properties = new SkinTextureProperties();
        properties.flags = flags;
        properties.storage.putAll(storage);
        return properties;
    }

    @Override
    public String toString() {
        var properties = storage.copy();
        if (isEmissive()) {
            properties.put("isEmissive", true);
        }
        if (isTranslucent()) {
            properties.put("isTranslucent", true);
        }
        if (isNormal()) {
            properties.put("isNormal", true);
        }
        if (isSpecular()) {
            properties.put("isSpecular", true);
        }
        if (isBlurFilter()) {
            properties.put("isBlurFilter", true);
        }
        if (isClampToEdge()) {
            properties.put("isClampToEdge", true);
        }
        return properties.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinTextureProperties that)) return false;
        return flags == that.flags && storage.equals(that.storage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flags, storage);
    }

    private void setFlag(int bit, boolean value) {
        if (value) {
            flags |= 1 << bit;
        } else {
            flags &= ~(1 << bit);
        }
    }

    private boolean getFlag(int bit) {
        return (flags & (1 << bit)) != 0;
    }
}
