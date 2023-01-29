package moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;

import java.io.IOException;

public class ChunkPaintData {

    private final ChunkPalette palette;

    public ChunkPaintData(ChunkPalette palette) {
        this.palette = palette;
    }

    public SkinPaintData readFromStream(ChunkInputStream stream) throws IOException {
        int version = stream.readByte();
        int slim = stream.readByte();
        SkinPaintData paintData;
        if (version == 1) {
            paintData = SkinPaintData.v1();
        } else if (version == 2) {
            paintData = SkinPaintData.v2();
        } else {
            return null; // unknown version
        }
        int size = stream.readInt();
        for (int i = 0; i < size; ++i) {
            int x = i % paintData.getWidth();
            int y = i / paintData.getWidth();
            IPaintColor paintColor = palette.readColor(stream);
            paintData.setColor(x, y, paintColor.getRawValue());
        }
        return paintData;
    }

    public void writeToStream(SkinPaintData paintData, ChunkOutputStream stream) throws IOException {
        // version
        int width = paintData.getWidth();
        int height = paintData.getHeight();
        if (paintData.getHeight() == SkinPaintData.TEXTURE_OLD_HEIGHT) {
            stream.writeByte(1);
        } else {
            stream.writeByte(2);
        }
        stream.writeByte(0);
        stream.writeInt(width * height);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                palette.writeColor(paintData.getColor(x, y), stream);
            }
        }
    }
}
