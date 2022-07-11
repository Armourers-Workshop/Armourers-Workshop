package moe.plushie.armourers_workshop.utils.texture;

import java.awt.*;
import java.util.ArrayList;

public class SkinPaintData {

    public static final int TEXTURE_OLD_WIDTH = 64;
    public static final int TEXTURE_OLD_HEIGHT = 32;

    public static final int TEXTURE_WIDTH = 64;
    public static final int TEXTURE_HEIGHT = 64;

    private final int width;
    private final int height;

    private final int[] data;

    public SkinPaintData(int width, int height) {
        this.data = new int[width * height];
        this.width = width;
        this.height = height;
    }

    public SkinPaintData(int width, int height, int[] data) {
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public static SkinPaintData v1() {
        return new SkinPaintData(TEXTURE_OLD_WIDTH, TEXTURE_OLD_HEIGHT);
    }

    public static SkinPaintData v2() {
        return new SkinPaintData(TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public SkinPaintData clone() {
        SkinPaintData paintData = new SkinPaintData(width, height);
        System.arraycopy(data, 0, paintData.data, 0, data.length);
        return paintData;
    }

    public void copyFrom(SkinPaintData paintData) {
        // in future version the width maybe has some diff, but we don't need to support for now.
        if (width != paintData.getWidth()) {
            return;
        }
        // when the version is same, directly memory copy of the data.
        if (height == paintData.getHeight()) {
            System.arraycopy(paintData.getData(), 0, data, 0, data.length);
            return;
        }
        // when the height are different, we directly memory copy the same rect first.
        // and then copy all v2 version data if needed.
        System.arraycopy(paintData.getData(), 0, data, 0, width * Math.min(height, paintData.getHeight()));
        if (height <= TEXTURE_OLD_HEIGHT) {
            return;
        }
        PlayerTextureModel source = PlayerTextureModel.of(paintData.getWidth(), paintData.getHeight(), false);
        PlayerTextureModel destination = PlayerTextureModel.of(getWidth(), getHeight(), false);
        source.forEach((partType, sourceBox) -> {
            SkyBox destinationBox = destination.get(partType);
            if (sourceBox.equals(destinationBox) || destinationBox == null) {
                return;
            }
            ArrayList<Point> sourceTextures = new ArrayList<>(0);
            sourceBox.forEach((texture, x, y, z, dir) -> sourceTextures.add(texture));
            destinationBox.forEach((texture, x, y, z, dir) -> {
                if (!sourceTextures.isEmpty()) {
                    int color = paintData.getColor(sourceTextures.remove(0));
                    setColor(texture, color);
                }
            });
        });
    }

    public int getColor(Point point) {
        return getColor(point.x, point.y);
    }

    public void setColor(Point point, int color) {
        setColor(point.x, point.y, color);
    }

    public int getColor(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return 0;
        }
        return data[x + y * width];
    }

    public void setColor(int x, int y, int color) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return;
        }
        data[x + y * width] = color;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getData() {
        return data;
    }
}
