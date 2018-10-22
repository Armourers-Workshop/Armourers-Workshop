package moe.plushie.armourers_workshop.utils;

import java.awt.Color;
import java.util.Random;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;
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
    
    public static enum ColourFamily {
        MINECRAFT("minecraft"),
        //MINECRAFT_WOOL("wool"),
        SHADES("shades");
        
        public final String name;
        
        private ColourFamily(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        public String getLocalizedName() {
            String unlocalizedText = "colourFamily." + LibModInfo.ID.toLowerCase() + ":";
            unlocalizedText += this.name.toLowerCase() + ".name";
            return TranslateUtils.translate(unlocalizedText);
        }
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
    
    /* Old pastel colours
    0xDDDDDD, 0xDB7D3E, 0xB350BC, 0x6B8AC9,
    0xB1A627, 0x41AE38, 0xD08499, 0x404040,
    0x9AA1A1, 0x2E6E89, 0x7E3DB5, 0x2E388D,
    0x4F321F, 0x35461B, 0x963430, 0x191616
    */
    
    public static int getMinecraftColor(int meta, ColourFamily colourFamily) {
        switch (colourFamily) {
        case MINECRAFT:
            if (meta >= 0 && meta < PALETTE_MINECRAFT.length) {
                return PALETTE_MINECRAFT[meta];
            }
            break;
        /*case MINECRAFT_WOOL:
            if (meta >= 0 && meta < minecraftWoolColours.length) {
                return minecraftWoolColours[meta];
            }
            break;*/
        case SHADES:
            if (meta >= 0 && meta < PALETTE_SHADES.length) {
                return PALETTE_SHADES[meta];
            }
            break;
        }
        return 0x000000;
    }
}
