package moe.plushie.armourers_workshop.core.skin.sound;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.skin.sound.ISkinSoundProvider;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;


public class SkinSoundData implements ISkinSoundProvider {

    public static final SkinSoundData EMPTY = new SkinSoundData(null, Unpooled.EMPTY_BUFFER, SkinSoundProperties.EMPTY);

    private final int id = OpenRandomSource.nextInt(SkinSoundData.class);

    private final String name;
    private final ByteBuf buffer;

    private final SkinSoundProperties properties;

    public SkinSoundData(String name, ByteBuf buffer, SkinSoundProperties properties) {
        this.name = name;
        this.buffer = buffer;
        this.properties = properties;
    }

    public int id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public ByteBuf buffer() {
        return buffer;
    }

    @Override
    public SkinSoundProperties properties() {
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinSoundData that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", name, "properties", properties);
    }
}
