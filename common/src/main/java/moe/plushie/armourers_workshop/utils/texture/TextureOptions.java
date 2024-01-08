package moe.plushie.armourers_workshop.utils.texture;

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
        if (o == null || getClass() != o.getClass()) return false;
        TextureOptions that = (TextureOptions) o;
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
        switch (opt) {
            case 0x01:
                return 90;
            case 0x02:
                return 180;
            case 0x03:
                return 270;
        }
        return 0;
    }

    private int rot2opt(int rot) {
        switch (rot) {
            case 90:
                return 0x01;
            case 180:
                return 0x02;
            case 270:
                return 0x03;
        }
        return 0;
    }
}
