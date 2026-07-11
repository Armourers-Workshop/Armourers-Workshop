package moe.plushie.armourers_workshop.core.skin.texture;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.skin.texture.ISkinTextureData;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;
import moe.plushie.armourers_workshop.core.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SkinTextureData implements ISkinTextureData {

    public static final SkinTextureData EMPTY = new SkinTextureData("", 256, 256);

    private final int id = OpenRandomSource.nextInt(SkinTextureData.class);
    private final String name;

    private final float width;
    private final float height;

    private SkinTextureAnimation animation;
    private SkinTextureProperties properties;

    private byte[] bytes = new byte[0];
    private List<SkinTextureData> variants = Collections.emptyList();

    public SkinTextureData(String name, float width, float height) {
        this(name, width, height, SkinTextureAnimation.EMPTY, SkinTextureProperties.EMPTY);
    }

    public SkinTextureData(String name, float width, float height, SkinTextureAnimation animation, SkinTextureProperties properties) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.animation = animation;
        this.properties = properties;
    }

    public void load(ByteBuf buf) {
        bytes = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), bytes);
    }

    public void load(InputStream inputStream) throws IOException {
        bytes = StreamUtils.readStreamToByteArray(inputStream);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public float width() {
        return width;
    }

    @Override
    public float height() {
        return height;
    }

    public void setAnimation(SkinTextureAnimation animation) {
        this.animation = animation;
    }

    @Override
    public SkinTextureAnimation animation() {
        return animation;
    }

    public void setProperties(SkinTextureProperties properties) {
        this.properties = properties;
    }

    @Override
    public SkinTextureProperties properties() {
        return properties;
    }

    @Override
    public byte[] bytes() {
        return bytes;
    }

    public void setVariants(List<SkinTextureData> variants) {
        this.variants = variants;
    }

    @Override
    public List<SkinTextureData> variants() {
        return variants;
    }

    public String extension() {
        return "png";
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", name, "width", width, "height", height, "animation", animation, "properties", properties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinTextureData that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
