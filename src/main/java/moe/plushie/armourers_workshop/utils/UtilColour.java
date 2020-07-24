package moe.plushie.armourers_workshop.utils;

import java.awt.Color;
import java.util.Random;

import net.minecraft.util.math.MathHelper;

public class UtilColour {

    public static Color makeColourBighter(Color c, int amount) {
        int r = c.getRed() + amount;
        int g = c.getGreen() + amount;
        int b = c.getBlue() + amount;

        r = MathHelper.clamp(r, 0, 255);
        g = MathHelper.clamp(g, 0, 255);
        b = MathHelper.clamp(b, 0, 255);

        return new Color(r, g, b);
    }

    public static Color makeColourDarker(Color c, int amount) {
        int r = c.getRed() - amount;
        int g = c.getGreen() - amount;
        int b = c.getBlue() - amount;

        r = MathHelper.clamp(r, 0, 255);
        g = MathHelper.clamp(g, 0, 255);
        b = MathHelper.clamp(b, 0, 255);

        return new Color(r, g, b);
    }

    public static Color addColourNoise(Color c, int amount) {
        Random rnd = new Random();
        int r = c.getRed() - amount + rnd.nextInt((amount * 2));
        int g = c.getGreen() - amount + rnd.nextInt((amount * 2));
        int b = c.getBlue() - amount + rnd.nextInt((amount * 2));

        r = MathHelper.clamp(r, 0, 255);
        g = MathHelper.clamp(g, 0, 255);
        b = MathHelper.clamp(b, 0, 255);

        return new Color(r, g, b);
    }

    public static Color addShadeNoise(Color c, int amount) {
        Random rnd = new Random();

        int shadeAmount = rnd.nextInt(amount * 2);

        int r = c.getRed() - amount + shadeAmount;
        int g = c.getGreen() - amount + shadeAmount;
        int b = c.getBlue() - amount + shadeAmount;

        r = MathHelper.clamp(r, 0, 255);
        g = MathHelper.clamp(g, 0, 255);
        b = MathHelper.clamp(b, 0, 255);

        return new Color(r, g, b);
    }

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
    static {
        PALETTE_SHADES = new int[32];
        PALETTE_SHADES[0] = 0xFFFFFFFF;
        for (int i = 1; i < PALETTE_SHADES.length + 1; i++) {
            Color c = new Color((8 * i) - 1 , (8 * i) - 1, (8 * i) - 1);
            PALETTE_SHADES[i - 1] = c.getRGB();
        }
    }

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

    /* Old pastel colours
    0xDDDDDD, 0xDB7D3E, 0xB350BC, 0x6B8AC9,
    0xB1A627, 0x41AE38, 0xD08499, 0x404040,
    0x9AA1A1, 0x2E6E89, 0x7E3DB5, 0x2E388D,
    0x4F321F, 0x35461B, 0x963430, 0x191616
    */
}
