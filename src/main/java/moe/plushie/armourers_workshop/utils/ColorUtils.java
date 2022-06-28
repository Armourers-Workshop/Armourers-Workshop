package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class ColorUtils {

    private static final Random RANDOM = new Random();

    public static int[] PALETTE_MINECRAFT = {
            0xFFFFFF, 0xFFFF55, 0xFF55FF, 0xFF5555,
            0x55FFFF, 0x55FF55, 0x5555FF, 0x555555,
            0xAAAAAA, 0xFFAA00, 0xAA00AA, 0xAA0000,
            0x00AAAA, 0x00AA00, 0x0000AA, 0x000000,
            0xDDDDDD, 0xDB7D3E, 0xB350BC, 0x6B8AC9,
            0xB1A627, 0x41AE38, 0xD08499, 0x404040,
            0x9AA1A1, 0x2E6E89, 0x7E3DB5, 0x2E388D,
            0x4F321F, 0x35461B, 0x963430, 0x191616
    };
    public static int[] PALETTE_SHADES = {};
    // Warm32 from https://lospec.com/palette-list/warm32
    public static int[] PALETTE_WARM32 = {
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
    public static int[] PALETTE_PASTEL_64_A = {
            0x998276, 0xc4c484, 0xabd883, 0xa2f2bd,
            0xb88488, 0xd1b182, 0xd4eb91, 0xccfcc4,
            0x907699, 0xc484a4, 0xea8c79, 0xf2e5a2,
            0x9a84b8, 0xd182ca, 0xeb91a8, 0xffddc4,
            0x768d99, 0x8484c4, 0xc479ea, 0xf2a2d7,
            0x84b8b4, 0x82a2d1, 0xa791eb, 0xfbc8f5,
            0x7c957a, 0x84c4a4, 0x79d7ea, 0xa2aff2,
            0xa2b884, 0x82d189, 0x91ebd4, 0xc9e5fa
    };
    public static int[] PALETTE_PASTEL_64_B = {
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
    public static int[] PALETTE_SOFTMILK32 = {
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
    public static int[] PALETTE_ENDESGA_32 = {
            0xbe4a2f, 0xd77643, 0xead4aa, 0xe4a672,
            0xb86f50, 0x733e39, 0x3e2731, 0xa22633,
            0xe43b44, 0xf77622, 0xfeae34, 0xfee761,
            0x63c74d, 0x3e8948, 0x265c42, 0x193c3e,
            0x124e89, 0x0099db, 0x2ce8f5, 0xffffff,
            0xc0cbdc, 0x8b9bb4, 0x5a6988, 0x3a4466,
            0x262b44, 0x181425, 0xff0044, 0x68386c,
            0xb55088, 0xf6757a, 0xe8b796, 0xc28569
    };

    static {
        PALETTE_SHADES = new int[32];
        PALETTE_SHADES[0] = 0xFFFFFFFF;
        for (int i = 1; i < PALETTE_SHADES.length + 1; i++) {
            Color c = new Color((8 * i) - 1, (8 * i) - 1, (8 * i) - 1);
            PALETTE_SHADES[i - 1] = c.getRGB();
        }
    }

    public static int makeColourBighter(int rgb, int amount) {
        int r = getRed(rgb) + amount;
        int g = getGreen(rgb) + amount;
        int b = getBlue(rgb) + amount;

        r = MathHelper.clamp(r, 0, 255);
        g = MathHelper.clamp(g, 0, 255);
        b = MathHelper.clamp(b, 0, 255);

        return getRGB(r, g, b);
    }

    public static int makeColourDarker(int rgb, int amount) {
        int r = getRed(rgb) - amount;
        int g = getGreen(rgb) - amount;
        int b = getBlue(rgb) - amount;

        r = MathHelper.clamp(r, 0, 255);
        g = MathHelper.clamp(g, 0, 255);
        b = MathHelper.clamp(b, 0, 255);

        return getRGB(r, g, b);
    }

    public static int addColorNoise(int rgb, int amount) {
        int r = getRed(rgb) - amount + RANDOM.nextInt((amount * 2));
        int g = getGreen(rgb) - amount + RANDOM.nextInt((amount * 2));
        int b = getBlue(rgb) - amount + RANDOM.nextInt((amount * 2));

        r = MathHelper.clamp(r, 0, 255);
        g = MathHelper.clamp(g, 0, 255);
        b = MathHelper.clamp(b, 0, 255);

        return getRGB(r, g, b);
    }

    public static int addShadeNoise(int rgb, int amount) {
        int shadeAmount = RANDOM.nextInt(amount * 2);

        int r = getRed(rgb) - amount + shadeAmount;
        int g = getGreen(rgb) - amount + shadeAmount;
        int b = getBlue(rgb) - amount + shadeAmount;

        r = MathHelper.clamp(r, 0, 255);
        g = MathHelper.clamp(g, 0, 255);
        b = MathHelper.clamp(b, 0, 255);

        return getRGB(r, g, b);
    }

    public static Color getPaletteColor(int index) {
        return new Color(PALETTE_MINECRAFT[(index + 1) % PALETTE_MINECRAFT.length]);
    }


    public static int getPaletteColor(int index, int type) {
        return (type << 24) | (getPaletteColor(index).getRGB() & 0xffffff);
    }

    /* Old pastel colours
    0xDDDDDD, 0xDB7D3E, 0xB350BC, 0x6B8AC9,
    0xB1A627, 0x41AE38, 0xD08499, 0x404040,
    0x9AA1A1, 0x2E6E89, 0x7E3DB5, 0x2E388D,
    0x4F321F, 0x35461B, 0x963430, 0x191616
    */

    public static int getRainbowRGB() {
        float f = System.currentTimeMillis() % (255L * 25) / 25F;
        return Color.HSBtoRGB(f / 255F, 1F, 1F);
    }

    public static int getPulse1Color(int color) {
        float f = (float) (System.currentTimeMillis() % (255L * 25D)) / 25F;
        f = f * 2F;
        if (f > 255) {
            f = 255F - (f - 255);
        }
        f = MathHelper.clamp(f, 0, 255);
        float[] hsb = Color.RGBtoHSB(getRed(color), getGreen(color), getBlue(color), null);
        return Color.HSBtoRGB(hsb[0], hsb[1], f / 255F);
    }

    public static int getPulse2Color(int color) {
        float f = (float) (System.currentTimeMillis() % (255L * 12.5D)) / 12.5F;
        f = f * 2F;
        if (f > 255) {
            f = 255F - (f - 255);
        }
        f = MathHelper.clamp(f, 0, 255);
        float[] hsb = Color.RGBtoHSB(getRed(color), getGreen(color), getBlue(color), null);
        return Color.HSBtoRGB(hsb[0], hsb[1], f / 255F);
    }

    public static int getDisplayRGB(IPaintColor paintColor) {
        ISkinPaintType paintType = paintColor.getPaintType();
        if (paintType == SkinPaintTypes.RAINBOW) {
            return getRainbowRGB();
        }
        if (paintType == SkinPaintTypes.PULSE_1) {
            return getPulse1Color(paintColor.getRGB());
        }
        if (paintType == SkinPaintTypes.PULSE_2) {
            return getPulse2Color(paintColor.getRGB());
        }
        return paintColor.getRGB();
//        IPaintType paintType = PaintTypeRegistry.getInstance().getPaintTypeFormByte(rgbt[3]);
//        if (paintType == PaintTypeRegistry.PAINT_TYPE_RAINBOW) {
//            return getRainbowColour();
//        } else if (paintType == PaintTypeRegistry.PAINT_TYPE_PULSE_1) {
//            return getPulse1Colour(rgbt);
//        } else if (paintType == PaintTypeRegistry.PAINT_TYPE_PULSE_2) {
//            return getPulse2Colour(rgbt);
//        }
//        return new Color(rgbt[0] & 0xFF, rgbt[1] & 0xFF, rgbt[2] & 0xFF, 255);
    }

    public static int getDisplayRGB(ItemStack itemStack) {
        IPaintColor paintColor = getColor(itemStack);
        if (paintColor != null) {
            return ColorUtils.getDisplayRGB(paintColor) | 0xff000000;
        }
        return 0xffffffff;
    }

    public static boolean hasColor(ItemStack itemStack) {
        CompoundNBT tag = itemStack.getTag();
        return tag != null && tag.contains(AWConstants.NBT.COLOR, Constants.NBT.TAG_INT);
    }

    public static void setColor(ItemStack itemStack, IPaintColor color) {
        AWDataSerializers.putPaintColor(itemStack.getOrCreateTag(), AWConstants.NBT.COLOR, color, null);
    }

    @Nullable
    public static IPaintColor getColor(ItemStack itemStack) {
        return getColor(itemStack, null);
    }

    public static void setColor(ItemStack itemStack, @Nullable String rootPath, IPaintColor color) {
        CompoundNBT tag = itemStack.getOrCreateTag();
        if (rootPath != null) {
            if (tag.contains(rootPath)) {
                tag = tag.getCompound(rootPath);
            } else {
                CompoundNBT newTag = new CompoundNBT();
                tag.put(rootPath, newTag);
                tag = newTag;
            }
        }
        AWDataSerializers.putPaintColor(tag, AWConstants.NBT.COLOR, color, null);
    }

    @Nullable
    public static IPaintColor getColor(ItemStack itemStack, @Nullable String rootPath) {
        CompoundNBT tag = itemStack.getTag();
        if (tag != null && rootPath != null) {
            tag = tag.getCompound(rootPath);
        }
        if (tag != null && tag.contains(AWConstants.NBT.COLOR)) {
            INBT nbt = tag.get(AWConstants.NBT.COLOR);
            if (nbt instanceof NumberNBT) {
                return PaintColor.of(((NumberNBT) nbt).getAsInt());
            }
            if (nbt instanceof StringNBT) {
                Color color = ColorUtils.parseColor(nbt.getAsString());
                tag.putInt(AWConstants.NBT.COLOR, color.getRGB());
                return PaintColor.of(color.getRGB());
            }
            tag.remove(AWConstants.NBT.COLOR);
        }
        return null;
    }


    public static ArrayList<ITextComponent> getColorTooltips(IPaintColor color, boolean useDisplayColor) {
        ArrayList<ITextComponent> tooltips = new ArrayList<>();
        int rgb = color.getRGB();
        if (useDisplayColor) {
            rgb = getDisplayRGB(color);
        }
        ISkinPaintType paintType = color.getPaintType();
        String hexColor = String.format("#%06x", rgb & 0xffffff);
        ITextComponent paintName = TranslateUtils.Name.of(paintType);
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.colour", rgb & 0xffffff));
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.hex", hexColor));
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.paintType", paintName));
        return tooltips;
    }

    // #[A]RGB or 0x[A]RGB
    public static Color parseColor(String colorString) {
        try {
            long value = Long.decode(colorString);
            if ((value & 0xff000000) == 0) {
                value |= 0xff000000;
            }
            return new Color((int) value, true);
        } catch (NumberFormatException e) {
            return Color.black;
        }
    }

    public static int HSBtoRGB(float[] hsb) {
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    }

    public static float[] RGBtoHSB(IPaintColor paintColor) {
        int rgb = paintColor.getRGB();
        return Color.RGBtoHSB(getRed(rgb), getGreen(rgb), getBlue(rgb), null);
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

    public static int getRGB(int red, int green, int blue) {
        return red << 16 | green << 8 | blue;
    }
}
