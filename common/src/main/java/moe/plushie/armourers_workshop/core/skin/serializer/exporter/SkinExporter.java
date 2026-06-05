package moe.plushie.armourers_workshop.core.skin.serializer.exporter;

import moe.plushie.armourers_workshop.core.math.OpenSize2i;
import moe.plushie.armourers_workshop.core.math.OpenVector2i;
import moe.plushie.armourers_workshop.core.skin.Skin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class SkinExporter {

    // we must reduce the floating point zero padding to save space.
    public static final DecimalFormat FLOAT_FORMAT = new DecimalFormat("#.#####");
    public static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("#.############");

    public abstract Collection<String> extensions();

    public abstract void exportSkin(Skin skin, File filePath, String filename, float scale) throws Exception;


    public static class TextureImage {

        private final String name;

        private final int width;
        private final int height;

        private final BufferedImage image;

        private final Map<Integer, OpenVector2i> colors = new HashMap<>();

        public TextureImage(String name, Collection<Integer> colors) {
            this(name, calcTextureSize(colors.size()), colors);
        }

        public TextureImage(String name, OpenSize2i size, Collection<Integer> colors) {
            this.name = name;
            this.width = size.width;
            this.height = size.height;
            // create a buffer.
            this.image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            var index = 0;
            for (var color : colors) {
                var x = index % size.width;
                var y = index / size.width;
                this.image.setRGB(x, y, color);
                this.colors.put(color, new OpenVector2i(x, y));
                index += 1;
            }
        }

        private static OpenSize2i calcTextureSize(int length) {
            for (var width = 16; /* nop */ ; width <<= 1) {
                for (var height = 16; height <= width; height <<= 1) {
                    if (width * height >= length) {
                        return new OpenSize2i(width, height);
                    }
                }
            }
        }

        public OpenVector2i get(int color) {
            return colors.getOrDefault(color, OpenVector2i.ZERO);
        }

        public String name() {
            return name;
        }

        public int width() {
            return width;
        }

        public int height() {
            return height;
        }

        public BufferedImage image() {
            return image;
        }
    }
}
