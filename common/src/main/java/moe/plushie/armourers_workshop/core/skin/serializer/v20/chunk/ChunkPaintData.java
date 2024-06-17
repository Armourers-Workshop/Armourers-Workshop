package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.texture.SkinPaintData;

import java.io.IOException;

public class ChunkPaintData {

    private final ChunkPaletteData palette;

    public ChunkPaintData(ChunkPaletteData palette) {
        this.palette = palette;
    }

    public SkinPaintData readFromStream(ChunkInputStream stream) throws IOException {
        int flags = stream.readVarInt();
        int totalWidth = stream.readVarInt();
        int totalHeight = stream.readVarInt();
        SkinPaintData paintData = _paintData(flags, totalWidth, totalHeight);
        if (paintData == null) {
            return null; // we can't support it.
        }
        while (true) {
            int width = stream.readVarInt();
            if (width == 0) {
                break;
            }
            int height = stream.readVarInt();
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    paintData.setColor(x, y, palette.readColor(stream));
                }
            }
        }
        return paintData;
    }

    public void writeToStream(SkinPaintData paintData, ChunkOutputStream stream) throws IOException {
        int flags = _flags(paintData);
        stream.writeVarInt(flags);
        stream.writeVarInt(paintData.getWidth());
        stream.writeVarInt(paintData.getHeight());
        // TODO: we need to support writing only part of the data.
        int width = paintData.getWidth();
        int height = paintData.getHeight();
        stream.writeVarInt(width);
        stream.writeVarInt(height);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                stream.writeVariable(palette.writeColor(paintData.getColor(x, y)));
            }
        }
        stream.writeVarInt(0);
        stream.writeVarInt(0);
    }

    private int _flags(SkinPaintData paintData) {
        // 0x80 slim
        return 0;
    }

    private SkinPaintData _paintData(int flags, int width, int height) {
        // v1 skin texture.
        if (width == SkinPaintData.TEXTURE_OLD_WIDTH && height == SkinPaintData.TEXTURE_OLD_HEIGHT) {
            return SkinPaintData.v1();
        }
        // v2 skin texture.
        if (width == SkinPaintData.TEXTURE_WIDTH && height == SkinPaintData.TEXTURE_HEIGHT) {
            return SkinPaintData.v2();
        }
        // v3 custom texture.
        return null;
    }
}
