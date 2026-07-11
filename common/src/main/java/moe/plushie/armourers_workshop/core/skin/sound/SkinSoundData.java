package moe.plushie.armourers_workshop.core.skin.sound;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.skin.sound.ISkinSoundData;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;
import moe.plushie.armourers_workshop.core.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;

public class SkinSoundData implements ISkinSoundData {

    public static final SkinSoundData EMPTY = new SkinSoundData(null, SkinSoundProperties.EMPTY);

    private final int id = OpenRandomSource.nextInt(SkinSoundData.class);

    private final String name;
    private final SkinSoundProperties properties;

    private byte[] bytes = new byte[0];

    public SkinSoundData(String name, SkinSoundProperties properties) {
        this.name = name;
        this.properties = properties;
    }

    public void load(ByteBuf buf) {
        bytes = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), bytes);
    }

    public void load(InputStream inputStream) throws IOException {
        bytes = StreamUtils.readStreamToByteArray(inputStream);
    }

    public int id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public SkinSoundProperties properties() {
        return properties;
    }

    @Override
    public byte[] bytes() {
        return bytes;
    }

    public String extension() {
        return "ogg";
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
