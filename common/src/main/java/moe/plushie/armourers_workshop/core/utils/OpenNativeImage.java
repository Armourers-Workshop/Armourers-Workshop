package moe.plushie.armourers_workshop.core.utils;

public abstract class OpenNativeImage {

    private final int width;
    private final int height;

    public OpenNativeImage(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public abstract int getPixel(int x, int y);

    public abstract void setPixel(int x, int y, int color);

    @Override
    public abstract OpenNativeImage clone();

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}
