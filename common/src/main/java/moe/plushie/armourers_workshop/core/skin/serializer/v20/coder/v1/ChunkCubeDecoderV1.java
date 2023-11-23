package moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.v1;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkCubeSection;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkCubeSelector;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkPaletteData;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.ChunkCubeDecoder;
import moe.plushie.armourers_workshop.utils.DirectionUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import net.minecraft.core.Direction;

public class ChunkCubeDecoderV1 extends ChunkCubeDecoder {

    private boolean isParsed = false;

    public ChunkCubeDecoderV1(int startIndex, int endIndex, ChunkCubeSelector selector, ChunkCubeSection.Immutable section) {
        super(startIndex, endIndex, selector, section);
    }

    public static int getStride(int options, ChunkPaletteData palette) {
        int faceCount = options & 0x0F;
        return calcStride(palette.getColorIndexBytes(), faceCount);
    }

    public static int calcStride(int usedBytes, int size) {
        // x/y/z (face + color ref) * faceCount
        return 3 + (1 + usedBytes) * size;
    }

    @Override
    public Vector3i getPosition() {
        pos.setX(getByte(0));
        pos.setY(getByte(1));
        pos.setZ(getByte(2));
        return pos;
    }

    @Override
    public IPaintColor getPaintColor(Direction dir) {
        parseColorsIfNeeded();
        return super.getPaintColor(dir);
    }

    protected void parseColorsIfNeeded() {
        if (isParsed) {
            return;
        }
        int usedBytes = palette.getColorIndexBytes();
        for (int i = 0; i < faceCount; ++i) {
            int face = getByte(calcStride(usedBytes, i));
            IPaintColor color = palette.readColorFromStream(bytes, readerIndex + calcStride(usedBytes, i) + 1);
            for (Direction dir : DirectionUtils.valuesFromSet(face)) {
                super.setPaintColor(dir, color);
            }
        }
        isParsed = true;
    }

    @Override
    protected void reset() {
        isParsed = false;
    }
}
