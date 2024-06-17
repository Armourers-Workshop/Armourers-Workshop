package moe.plushie.armourers_workshop.core.texture;

import moe.plushie.armourers_workshop.api.common.ITextureProperties;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;

public class TextureProperties implements ITextureProperties {

    public static final TextureProperties EMPTY = new TextureProperties();

    private int flags = 0;

    public void readFromStream(IInputStream stream) throws IOException {
        this.flags = stream.readInt();
    }

    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeInt(flags);
    }

    public void setEmissive(boolean isEmissive) {
        setFlag(0, isEmissive);
    }

    @Override
    public boolean isEmissive() {
        return getFlag(0);
    }

    public void setAdditive(boolean isAdditive) {
        setFlag(1, isAdditive);
    }

    public boolean isAdditive() {
        return getFlag(1);
    }

    public void setParticle(boolean isParticle) {
        setFlag(2, isParticle);
    }

    @Override
    public boolean isParticle() {
        return getFlag(2);
    }

    @Override
    public String toString() {
        if (this == EMPTY) {
            return "[]";
        }
        return String.format("[emissive=%s, particle=%s, additive=%s]", isEmissive(), isParticle(), isAdditive());
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
