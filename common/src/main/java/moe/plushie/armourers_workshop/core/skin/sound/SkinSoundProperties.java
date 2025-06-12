package moe.plushie.armourers_workshop.core.skin.sound;

import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.api.skin.sound.ISkinSoundProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.io.IOException;

@SuppressWarnings("unused")
public class SkinSoundProperties implements ISkinSoundProperties {

    public static final SkinSoundProperties EMPTY = new SkinSoundProperties();

    private static final SkinProperty<Float> VOLUME = SkinProperty.normal("Volume", 1.0f);
    private static final SkinProperty<Float> PITCH = SkinProperty.normal("Pitch", 1.0f);

    private int flags = 0;
    private final SkinProperties storage = new SkinProperties();

    public SkinSoundProperties() {
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


    public void setVolume(float volume) {
        storage.put(VOLUME, volume);
    }

    @Override
    public float volume() {
        return storage.get(VOLUME);
    }

    public void setPitch(float pitch) {
        storage.put(PITCH, pitch);
    }

    @Override
    public float pitch() {
        return storage.get(PITCH);
    }

    public SkinSoundProperties copy() {
        var properties = new SkinSoundProperties();
        properties.flags = flags;
        properties.storage.putAll(storage);
        return properties;
    }

    @Override
    public String toString() {
        var properties = storage.copy();
        return properties.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinSoundProperties that)) return false;
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
