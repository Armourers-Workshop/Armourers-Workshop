package moe.plushie.armourers_workshop.utils;

import java.awt.*;

public class SkinPaintData {

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

    @Override
    public SkinPaintData clone() {
        SkinPaintData paintData = new SkinPaintData(width, height);
        System.arraycopy(data, 0, paintData.data, 0, data.length);
        return paintData;
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
