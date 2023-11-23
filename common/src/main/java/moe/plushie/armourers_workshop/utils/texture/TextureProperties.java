package moe.plushie.armourers_workshop.utils.texture;

import moe.plushie.armourers_workshop.api.common.ITextureProperties;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.utils.ObjectUtils;

import java.io.IOException;

public class TextureProperties implements ITextureProperties {

    public static final TextureProperties NONE = new TextureProperties();


    public void readFromStream(IInputStream stream) throws IOException {
        int flags = stream.readInt();
        if (flags == 0) {
            // compatible older versions, it will remove after going release.
            flags = stream.readInt();
            setEmissive((flags & 0x01) != 0);
            setAdditive((flags & 0x02) != 0);
            return;
        }
        int orderType = (flags >> 16) & 0xffff;
        int type = (flags >> 0) & 0xffff;

        // 0xffff0000 order type,
        // 0x0000ffff type

        // ..

    }

    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeInt(0);
        stream.writeInt(0);
    }

    public void setEmissive(boolean isEmissive) {
    }

    @Override
    public boolean isEmissive() {
        return false;
    }

    public void setAdditive(boolean isAdditive) {
//        if (renderMode.equals("emissive")) {
//            options |= 0x1;
//        }
//        if (renderMode.equals("additive")) {
//            options |= 0x2;
//        }
    }

    public boolean isAdditive() {
        return false;
    }


    public void setAnimation(TextureAnimation animation) {

    }

    @Override
    public TextureAnimation getAnimation() {
        return null;
    }

    @Override
    public String toString() {
        return ObjectUtils.makeDescription(this);
    }
}
