package moe.plushie.armourers_workshop.core.client.texture;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.texture.AbstractImageTexture;

import java.awt.image.BufferedImage;

@OnlyIn(Dist.CLIENT)
public class ImageTexture extends AbstractImageTexture {

    protected int width = 0;
    protected int height = 0;

    public ImageTexture(String name) {
        super(name);
    }

    public ImageTexture(String name, BufferedImage image) {
        super(name);
        this.resize(image.getWidth(), image.getHeight());
        this.fill(image);
        this.upload();
    }

    @Override
    public void upload() {
        super.upload();
    }

    public BufferedImage download() {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                var color = pixels.getPixel(x, y);
                image.setRGB(x, y, color);
            }
        }
        return image;
    }

    public void resize(int width, int height) {
        if (this.width == width && this.height == height) {
            return;
        }
        this.width = width;
        this.height = height;
        this.prepare(width, height, true);
    }

    public void fill(BufferedImage image) {
        var width = image.getWidth();
        var height = image.getHeight();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                var color = image.getRGB(x, y);
                pixels.setPixel(x, y, color);
            }
        }
    }

    public void fill(int x0, int y0, int x1, int y1, int color) {
        for (int y = y0; y < y1; ++y) {
            for (int x = x0; x < x1; ++x) {
                pixels.setPixel(x, y, color);
            }
        }
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}
