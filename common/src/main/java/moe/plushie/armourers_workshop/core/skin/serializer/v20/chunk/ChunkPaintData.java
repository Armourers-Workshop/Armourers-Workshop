package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintData;

import java.io.IOException;

public class ChunkPaintData {

    public SkinPaintData readFromStream(ChunkDataInputStream stream) throws IOException {
        int options = stream.readInt();
        int width = stream.readVarInt();
        int height = stream.readVarInt();
        var paintData = _createPaintData(options, width, height);
        if (paintData == null) {
            return null; // we can't support it.
        }
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                paintData.setColor(x, y, stream.readInt());
            }
        }
        return paintData;
    }

    public void writeToStream(SkinPaintData paintData, ChunkDataOutputStream stream) throws IOException {
        int options = 0;
        int width = paintData.width();
        int height = paintData.height();
        stream.writeInt(options);
        stream.writeVarInt(width);
        stream.writeVarInt(height);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                stream.writeInt(paintData.getColor(x, y));
            }
        }
    }

    private SkinPaintData _createPaintData(int options, int width, int height) {
        // v1 skin texture.
        if (width == SkinPaintData.TEXTURE_OLD_WIDTH && height == SkinPaintData.TEXTURE_OLD_HEIGHT) {
            return SkinPaintData.v1();
        }
        // v2 skin texture.
        if (width == SkinPaintData.TEXTURE_WIDTH && height == SkinPaintData.TEXTURE_HEIGHT) {
            return SkinPaintData.v2(false);
        }
        // v3 custom texture.
        return null;
    }
}
