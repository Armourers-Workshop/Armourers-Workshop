package moe.plushie.armourers_workshop.core.skin.texture;

import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenVector2i;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;

import java.util.HashMap;

public class SkinPaintData {

    public static final int TEXTURE_OLD_WIDTH = 64;
    public static final int TEXTURE_OLD_HEIGHT = 32;
    public static final int TEXTURE_OLD_SIZE = TEXTURE_OLD_WIDTH * TEXTURE_OLD_HEIGHT;

    public static final int TEXTURE_WIDTH = 64;
    public static final int TEXTURE_HEIGHT = 64;
    public static final int TEXTURE_SIZE = TEXTURE_WIDTH * TEXTURE_HEIGHT;

    private final int width;
    private final int height;

    private final int[] bytes;

    public SkinPaintData(int width, int height) {
        this(width, height, new int[width * height]);
    }

    public SkinPaintData(int width, int height, int[] bytes) {
        this.width = width;
        this.height = height;
        this.bytes = bytes;
    }

    public static SkinPaintData v1() {
        return new SkinPaintData(TEXTURE_OLD_WIDTH, TEXTURE_OLD_HEIGHT);
    }

    public static SkinPaintData v2(boolean slim) {
        var paintData = new SkinPaintData(TEXTURE_WIDTH, TEXTURE_HEIGHT);
        paintData.setFlag(0, slim);
        return paintData;
    }

    public SkinPaintData copy() {
        var paintData = new SkinPaintData(width, height);
        System.arraycopy(bytes, 0, paintData.bytes, 0, bytes.length);
        return paintData;
    }

    public void copyFrom(SkinPaintData other) {
        // in future version the width maybe has some diff, but we don't need to support for now.
        if (width != other.width()) {
            return;
        }
        // when the version is same, directly memory copy of the data.
        if (height == other.height() && slim() == other.slim()) {
            System.arraycopy(other.bytes(), 0, bytes, 0, bytes.length);
            return;
        }
        // manual copy all part boxes.
        var source = PlayerSkinModel.from(other.width(), other.height(), other.slim());
        var destination = PlayerSkinModel.from(width(), height(), slim());
        source.forEach((partType, sourceBox) -> {
            var destinationBox = destination.get(partType);
            if (destinationBox != null) {
                other.copyTo(sourceBox, this, destinationBox, false);
            }
        });
    }

    public void copyTo(PlayerSkinModel.Box srcBox, SkinPaintData destData, PlayerSkinModel.Box destBox, boolean isMirrorX) {
        var srcWidth = srcBox.width() - 1;
        var srcHeight = srcBox.height() - 1;
        var srcDepth = srcBox.depth() - 1;
        var destWidth = destBox.width() - 1;
        var destHeight = destBox.height() - 1;
        var destDepth = destBox.depth() - 1;
        var colors = new HashMap<OpenVector2i, Integer>();
        srcBox.forEach((texturePos, x, y, z, dir) -> {
            // src => progress => dest
            var px = (float) x / srcWidth;
            var py = (float) y / srcHeight;
            var pz = (float) z / srcDepth;
            // apply mirror
            if (isMirrorX) {
                px = 1 - px;
                // we're just mirroring the x-axis when if it needs.
                if (dir.axis() == OpenDirection.Axis.X) {
                    dir = dir.opposite();
                }
            }
            var ix = OpenMath.roundi(px * destWidth);
            var iy = OpenMath.roundi(py * destHeight);
            var iz = OpenMath.roundi(pz * destDepth);
            var newTexturePos = destBox.get(ix, iy, iz, dir);
            if (newTexturePos == null) {
                return;
            }
            var color = getColor(texturePos);
            if (SkinPaintColor.isOpaque(color)) {
                // a special case is to use the mirror to swap the part texture,
                // we will copy the color to the map and then applying it when read finish.
                colors.put(newTexturePos, color);
            }
        });
        colors.forEach(destData::setColor);
    }

    public int getColor(OpenVector2i point) {
        return getColor(point.x(), point.y());
    }

    public void setColor(OpenVector2i point, int color) {
        setColor(point.x(), point.y(), color);
    }

    public int getColor(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return 0;
        }
        return bytes[x + y * width];
    }

    public void setColor(int x, int y, SkinPaintColor paintColor) {
        setColor(x, y, paintColor.rawValue());
    }

    public void setColor(int x, int y, int color) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return;
        }
        bytes[x + y * width] = color;
    }

    public int version() {
        if (width == TEXTURE_OLD_WIDTH && height == TEXTURE_OLD_HEIGHT) {
            return 1;
        }
        if (width == TEXTURE_WIDTH && height == TEXTURE_HEIGHT) {
            return 2;
        }
        return 0;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int[] bytes() {
        return bytes;
    }

    public boolean slim() {
        return getFlag(0);
    }

    private void setFlag(int bit, boolean value) {
        if (value) {
            bytes[0] |= 1 << bit;
        } else {
            bytes[0] &= ~(1 << bit);
        }
    }

    private boolean getFlag(int bit) {
        return (bytes[0] & (1 << bit)) != 0;
    }

}
