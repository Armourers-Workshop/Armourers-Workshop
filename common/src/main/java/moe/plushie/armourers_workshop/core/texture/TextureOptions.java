package moe.plushie.armourers_workshop.core.texture;

import com.google.common.base.Objects;
import moe.plushie.armourers_workshop.api.common.ITextureOptions;

public class TextureOptions implements ITextureOptions {

    private long value = 0;
    private int rotation = 0;

    public TextureOptions() {
    }

    public TextureOptions(long value) {
        this.value = value;
        this.rotation = opt2rot((int) value & 0x0f);
    }

    public void setRotation(int rotation) {
        this.value &= ~0x0f;
        this.value |= rot2opt(rotation);
        this.rotation = rotation;
    }

    @Override
    public int getRotation() {
        return this.rotation;
    }

    public long asLong() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextureOptions that)) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return String.format("[rotation=%d]", rotation);
    }

    private int opt2rot(int opt) {
        return switch (opt) {
            case 0x01 -> 90;
            case 0x02 -> 180;
            case 0x03 -> 270;
            default -> 0;
        };
    }

    private int rot2opt(int rot) {
        return switch (rot) {
            case 90 -> 0x01;
            case 180 -> 0x02;
            case 270 -> 0x03;
            default -> 0;
        };
    }
}
