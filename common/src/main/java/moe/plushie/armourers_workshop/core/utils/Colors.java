package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.data.ItemStackStorage;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

@SuppressWarnings("unused")
public class Colors {

    public static final int WHITE = 0xffffffff;
    public static final int LIGHT_GRAY = 0xffc0c0c0;
    public static final int GRAY = 0xff808080;
    public static final int DARK_GRAY = 0xff404040;
    public static final int BLACK = 0xff000000;
    public static final int RED = 0xffff0000;
    public static final int PINK = 0xffff4b4b;
    public static final int ORANGE = 0xffffc800;
    public static final int YELLOW = 0xffffff00;
    public static final int GREEN = 0xff00ff00;
    public static final int MAGENTA = 0xffff00ff;
    public static final int CYAN = 0xff00ffff;
    public static final int BLUE = 0xff0000ff;

    public static final int[] PALETTE_MINECRAFT = {
            0xffffff, 0xffff55, 0xff55ff, 0xff5555,
            0x55ffff, 0x55ff55, 0x5555ff, 0x555555,
            0xaaaaaa, 0xffaa00, 0xaa00aa, 0xaa0000,
            0x00aaaa, 0x00aa00, 0x0000aa, 0x000000,
            0xdddddd, 0xdb7d3e, 0xb350bc, 0x6b8ac9,
            0xb1a627, 0x41ae38, 0xd08499, 0x404040,
            0x9aa1a1, 0x2e6e89, 0x7e3db5, 0x2e388d,
            0x4f321f, 0x35461b, 0x963430, 0x191616
    };
    public static final int[] PALETTE_SHADES = {
            0x070707, 0x0f0f0f, 0x171717, 0x1f1f1f,
            0x272727, 0x2f2f2f, 0x373737, 0x3f3f3f,
            0x474747, 0x4f4f4f, 0x575757, 0x5f5f5f,
            0x676767, 0x6f6f6f, 0x777777, 0x7f7f7f,
            0x878787, 0x8f8f8f, 0x979797, 0x9f9f9f,
            0xa7a7a7, 0xafafaf, 0xb7b7b7, 0xbfbfbf,
            0xc7c7c7, 0xcfcfcf, 0xd7d7d7, 0xdfdfdf,
            0xe7e7e7, 0xefefef, 0xf7f7f7, 0xffffff,
    };
    // Warm32 from https://lospec.com/palette-list/warm32
    public static final int[] PALETTE_WARM32 = {
            0x0d0e1e, 0x2f3144, 0x626a73, 0x94a5aa,
            0xd3dfe1, 0x291820, 0x694749, 0xa56e66,
            0xcb9670, 0xecd8b7, 0x28092d, 0x692b58,
            0x804061, 0xa1516a, 0xe19393, 0x1e1d38,
            0x514569, 0x84788b, 0xbea8bf, 0x232d4f,
            0x3a4b6d, 0x65799a, 0x99b4dd, 0x41648b,
            0x6fa9c3, 0xb9e2e5, 0xd3ead8, 0x0a2325,
            0x204039, 0x3e6248, 0x778f73, 0xb4c3a8
    };
    // Pastel-64 from https://lospec.com/palette-list/pastel-64
    public static final int[] PALETTE_PASTEL_64_A = {
            0x998276, 0xc4c484, 0xabd883, 0xa2f2bd,
            0xb88488, 0xd1b182, 0xd4eb91, 0xccfcc4,
            0x907699, 0xc484a4, 0xea8c79, 0xf2e5a2,
            0x9a84b8, 0xd182ca, 0xeb91a8, 0xffddc4,
            0x768d99, 0x8484c4, 0xc479ea, 0xf2a2d7,
            0x84b8b4, 0x82a2d1, 0xa791eb, 0xfbc8f5,
            0x7c957a, 0x84c4a4, 0x79d7ea, 0xa2aff2,
            0xa2b884, 0x82d189, 0x91ebd4, 0xc9e5fa
    };
    public static final int[] PALETTE_PASTEL_64_B = {
            0xb8a784, 0xb9ca89, 0x91eb91, 0xc9fce9,
            0x957686, 0xc49484, 0xeade7a, 0xc3f2a2,
            0xb884af, 0xd1828f, 0xebbd91, 0xf7f9c4,
            0x797699, 0xb484c4, 0xea79bb, 0xf2a9a2,
            0x8495b8, 0x9d82d1, 0xea91eb, 0xffc8d4,
            0x76958d, 0x84b4c4, 0x7982ea, 0xd1a2f2,
            0x84b88d, 0x82d1c4, 0x91beeb, 0xd2c6fa,
            0x969976, 0x94c484, 0x79eaa8, 0xa2ebf2
    };
    // SoftMilk32 from https://gumroad.com/l/qMfX
    public static final int[] PALETTE_SOFTMILK32 = {
            0xd95b9a, 0x9e4491, 0x633662, 0x903d62,
            0xbd515a, 0xd69a4e, 0xf3d040, 0xffe88c,
            0xf2f2f0, 0x94e092, 0x1f9983, 0x22636b,
            0xc56876, 0x5c3841, 0x945848, 0xd17f6b,
            0xeb9f7f, 0xf1c28f, 0xb9b5c3, 0x76747d,
            0x57546f, 0x23213d, 0x454194, 0x425bbd,
            0x4884d4, 0x45a1de, 0x7cd8eb, 0xe2f266,
            0xc3d442, 0x82aa28, 0x597f1e, 0x376129
    };
    // Endesga 32 from https://lospec.com/palette-list/endesga-32
    public static final int[] PALETTE_ENDESGA_32 = {
            0xbe4a2f, 0xd77643, 0xead4aa, 0xe4a672,
            0xb86f50, 0x733e39, 0x3e2731, 0xa22633,
            0xe43b44, 0xf77622, 0xfeae34, 0xfee761,
            0x63c74d, 0x3e8948, 0x265c42, 0x193c3e,
            0x124e89, 0x0099db, 0x2ce8f5, 0xffffff,
            0xc0cbdc, 0x8b9bb4, 0x5a6988, 0x3a4466,
            0x262b44, 0x181425, 0xff0044, 0x68386c,
            0xb55088, 0xf6757a, 0xe8b796, 0xc28569
    };

    public static int makeColorBighter(int rgb, int amount) {
        int r = getRed(rgb) + amount;
        int g = getGreen(rgb) + amount;
        int b = getBlue(rgb) + amount;

        r = OpenMath.clamp(r, 0, 255);
        g = OpenMath.clamp(g, 0, 255);
        b = OpenMath.clamp(b, 0, 255);

        return toRGB(r, g, b);
    }

    public static int makeColourDarker(int rgb, int amount) {
        int r = getRed(rgb) - amount;
        int g = getGreen(rgb) - amount;
        int b = getBlue(rgb) - amount;

        r = OpenMath.clamp(r, 0, 255);
        g = OpenMath.clamp(g, 0, 255);
        b = OpenMath.clamp(b, 0, 255);

        return toRGB(r, g, b);
    }

    public static int addColorNoise(int rgb, int amount, Random random) {
        int r = getRed(rgb) - amount + random.nextInt((amount * 2));
        int g = getGreen(rgb) - amount + random.nextInt((amount * 2));
        int b = getBlue(rgb) - amount + random.nextInt((amount * 2));

        r = OpenMath.clamp(r, 0, 255);
        g = OpenMath.clamp(g, 0, 255);
        b = OpenMath.clamp(b, 0, 255);

        return toRGB(r, g, b);
    }

    public static int addShadeNoise(int rgb, int amount, Random random) {
        int shadeAmount = random.nextInt(amount * 2);

        int r = getRed(rgb) - amount + shadeAmount;
        int g = getGreen(rgb) - amount + shadeAmount;
        int b = getBlue(rgb) - amount + shadeAmount;

        r = OpenMath.clamp(r, 0, 255);
        g = OpenMath.clamp(g, 0, 255);
        b = OpenMath.clamp(b, 0, 255);

        return toRGB(r, g, b);
    }

    public static int getAverageColor(Iterable<Integer> colors) {
        int red = 0;
        int green = 0;
        int blue = 0;
        int count = 0;
        for (int rgb : colors) {
            red += Colors.getRed(rgb);
            green += Colors.getGreen(rgb);
            blue += Colors.getBlue(rgb);
            count++;
        }
        if (count == 0) {
            return 0;
        }
        return toRGB(red / count, green / count, blue / count);
    }

    public static int getPaletteColor(int index) {
        return PALETTE_MINECRAFT[((index & 0x7fffffff) + 1) % PALETTE_MINECRAFT.length] | 0xff000000;
    }


    /* Old pastel colors
    0xDDDDDD, 0xDB7D3E, 0xB350BC, 0x6B8AC9,
    0xB1A627, 0x41AE38, 0xD08499, 0x404040,
    0x9AA1A1, 0x2E6E89, 0x7E3DB5, 0x2E388D,
    0x4F321F, 0x35461B, 0x963430, 0x191616
    */

    public static int getRainbowRGB() {
        float f = System.currentTimeMillis() % (255L * 25) / 25F;
        return HSBtoRGB(f / 255F, 1F, 1F);
    }

    public static int getPulse1Color(int color) {
        float f = (float) (System.currentTimeMillis() % (255L * 25D)) / 25F;
        f = f * 2F;
        if (f > 255) {
            f = 255F - (f - 255);
        }
        f = OpenMath.clamp(f, 0, 255);
        float[] hsb = RGBtoHSB(getRed(color), getGreen(color), getBlue(color), null);
        return HSBtoRGB(hsb[0], hsb[1], f / 255F);
    }

    public static int getPulse2Color(int color) {
        float f = (float) (System.currentTimeMillis() % (255L * 12.5D)) / 12.5F;
        f = f * 2F;
        if (f > 255) {
            f = 255F - (f - 255);
        }
        f = OpenMath.clamp(f, 0, 255);
        float[] hsb = RGBtoHSB(getRed(color), getGreen(color), getBlue(color), null);
        return HSBtoRGB(hsb[0], hsb[1], f / 255F);
    }

    public static int getDisplayRGB(SkinPaintColor paintColor) {
        var paintType = paintColor.paintType();
        if (paintType == SkinPaintTypes.RAINBOW) {
            return getRainbowRGB();
        }
        if (paintType == SkinPaintTypes.PULSE_1) {
            return getPulse1Color(paintColor.argb());
        }
        if (paintType == SkinPaintTypes.PULSE_2) {
            return getPulse2Color(paintColor.argb());
        }
        return paintColor.argb();
    }

    public static int getDisplayRGB(ItemStack itemStack) {
        var paintColor = itemStack.get(ModDataComponents.TOOL_COLOR.get());
        if (paintColor != null) {
            return Colors.getDisplayRGB(paintColor) | 0xff000000;
        }
        return 0xffffffff;
    }

    @Nullable
    public static BlockPaintColor getBlockColor(ItemStack itemStack) {
        var storage = ItemStackStorage.of(itemStack);
        if (storage.blockPaintColor != null) {
            return storage.blockPaintColor.orElse(null);
        }
        BlockPaintColor color = null;
        var entityData = itemStack.get(ModDataComponents.BLOCK_ENTITY_DATA.get());
        if (entityData != null) {
            var colorTag = entityData.tag().getOptionalCompound(Constants.Key.COLOR).orElse(null);
            if (colorTag != null && !colorTag.isEmpty()) {
                color = new BlockPaintColor(new TagSerializer(colorTag));
            }
        }
        storage.blockPaintColor = Optional.ofNullable(color);
        return color;
    }

    public static ArrayList<Component> getColorTooltips(SkinPaintColor color, boolean useDisplayColor) {
        var tooltips = new ArrayList<Component>();
        int rgb = color.argb();
        if (useDisplayColor) {
            rgb = getDisplayRGB(color);
        }
        var paintType = color.paintType();
        var hexColor = String.format("#%06x", rgb & 0xffffff);
        var paintName = TranslateUtils.Name.of(paintType);
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.colour", rgb & 0xffffff));
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.hex", hexColor));
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.paintType", paintName));
        return tooltips;
    }

    public static int mix(int a, int b, float q) {
        return (int) (a * (1 - q) + b * q);
    }


    public static int parseColor(String colorString) {
        try {
            int value = Integer.decode(colorString);
            if ((value & 0xff000000) == 0) {
                value |= 0xff000000;
            }
            return value;
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    public static int getRed(int rgb) {
        return (rgb >> 16) & 0xff;
    }

    public static int getGreen(int rgb) {
        return (rgb >> 8) & 0xff;
    }

    public static int getBlue(int rgb) {
        return rgb & 0xff;
    }

    public static int getAlpha(int rgb) {
        return (rgb >> 24) & 0xff;
    }

    public static int toRGB(int red, int green, int blue) {
        return red << 16 | green << 8 | blue;
    }

    public static int toRGB(float red, float green, float blue) {
        return toRGB((int) (red * 255), (int) (green * 255), (int) (blue * 255));
    }

    public static int toARGB(int red, int green, int blue, int alpha) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static int toARGB(float red, float green, float blue, float alpha) {
        return toARGB((int) (red * 255), (int) (green * 255), (int) (blue * 255), (int) (alpha * 255));
    }

    public static int HSBtoRGB(float[] hsb) {
        return HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    }

    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) java.lang.Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
                case 0:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 1:
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 2:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                    break;
                case 3:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 4:
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 5:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                    break;
            }
        }
        return 0xff000000 | (r << 16) | (g << 8) | (b);
    }

    public static float[] RGBtoHSB(int rgb) {
        return RGBtoHSB(getRed(rgb), getGreen(rgb), getBlue(rgb), null);
    }

    public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
        float hue, saturation, brightness;
        if (hsbvals == null) {
            hsbvals = new float[3];
        }
        int cmax = (r > g) ? r : g;
        if (b > cmax) cmax = b;
        int cmin = (r < g) ? r : g;
        if (b < cmin) cmin = b;

        brightness = ((float) cmax) / 255.0f;
        if (cmax != 0) saturation = ((float) (cmax - cmin)) / ((float) cmax);
        else saturation = 0;
        if (saturation == 0) hue = 0;
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax) hue = bluec - greenc;
            else if (g == cmax) hue = 2.0f + redc - bluec;
            else hue = 4.0f + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0) hue = hue + 1.0f;
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }
}
