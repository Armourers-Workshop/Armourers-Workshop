package moe.plushie.armourers_workshop.core.client.texture;

import moe.plushie.armourers_workshop.core.utils.Colors;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@SuppressWarnings({"SameParameterValue"})
public class PlayerSkinDecoder {

    private static final PlayerSkinDecoder INSTANCE = new PlayerSkinDecoder();

    public static PlayerSkinDecoder getInstance() {
        return INSTANCE;
    }

    public BufferedImage decode(InputStream stream) throws IOException {
        var image = ImageIO.read(stream);
        return upscale(image);
    }

    public BufferedImage decode(int width, int height, ByteBuffer buffer) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                var i = (y * width + x) * 4;
                var r = buffer.get(i) & 0xff;
                var g = buffer.get(i + 1) & 0xff;
                var b = buffer.get(i + 2) & 0xff;
                var a = buffer.get(i + 3) & 0xff;
                var color = Colors.toARGB(r, g, b, a);
                // ..
                image.setRGB(x, y, color);
            }
        }

        return upscale(image);
    }

    // 1024x512  https://tlauncher.org/catalog/hd/download/98ca4b9410c94348e912d920877bceb7.png
    // 1024x1024 https://minecraft.novaskin.me/download/3551513265
    private BufferedImage upscale(BufferedImage image) {
        var scale = Math.max(image.getWidth() / 64, 1);
        var height = image.getHeight() / scale;
        var width = image.getWidth() / scale;
        // it is a 64x64 image?
        if (width == 64 && height == 64) {
            return image;
        }
        // it is a 64x32 image?
        if (width == 64 && height == 32) {
            var builder = new Builder(BufferedImage.TYPE_INT_ARGB, 64, 64, scale);
            builder.copy(image);
            builder.copy(0, 16, 16, 48, 4, 12, 4, true, false); // left leg
            builder.copy(40, 16, 32, 48, 4, 12, 4, true, false); // left arm
            return builder.build();
        }
        throw new RuntimeException("not support image size " + image.getWidth() + "x" + image.getHeight());
    }

    private static class Builder {

        private final int scale;

        private final BufferedImage contents;

        private Builder(int type, int width, int height, int scale) {
            this.scale = scale;
            this.contents = new BufferedImage(width * scale, height * scale, type);
        }

        public void copy(BufferedImage src) {
            contents.setData(src.getData());
        }

        public void copy(int sx, int sy, int dx, int dy, int width, int height, int depth, boolean mirrorX, boolean mirrorY) {
            copy(sx + depth, sy, dx + depth, dy, width, depth, true, false); // up
            copy(sx + depth + width, sy, dx + depth + width, dy, width, depth, true, false); // down
            copy(sx, sy + depth, dx + depth + width, dy + depth, depth, height, true, false); // right
            copy(sx + depth, sy + depth, dx + depth, dy + depth, width, height, true, false); // front
            copy(sx + depth + width, sy + depth, dx, dy + depth, depth, height, true, false); // left
            copy(sx + depth + width + depth, sy + depth, dx + depth + width + depth, dy + depth, width, height, true, false);  // back
        }

        public void copy(int sx, int sy, int dx, int dy, int width, int height, boolean mirrorX, boolean mirrorY) {
            _copy(sx * scale, sy * scale, dx * scale, dy * scale, width * scale, height * scale, mirrorX, mirrorY);
        }

        private void _copy(int sx, int sy, int dx, int dy, int width, int height, boolean mirrorX, boolean mirrorY) {
            for (int j = 0; j < height; ++j) {
                for (int i = 0; i < width; ++i) {
                    var mi = i;
                    if (mirrorX) {
                        mi = width - i - 1;
                    }
                    var mj = j;
                    if (mirrorY) {
                        mj = height - j - 1;
                    }
                    contents.setRGB(dx + mi, dy + mj, contents.getRGB(sx + i, sy + j));
                }
            }
        }

        public BufferedImage build() {
            return contents;
        }
    }
}
