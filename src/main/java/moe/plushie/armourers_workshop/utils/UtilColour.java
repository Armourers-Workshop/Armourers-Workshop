package moe.plushie.armourers_workshop.utils;

import java.awt.Color;
import java.util.Random;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;

public class UtilColour {

    public static Color makeColourBighter(Color c, int amount) {
        int r = c.getRed() + amount;
        int g = c.getGreen() + amount;
        int b = c.getBlue() + amount;
        
        if (r > 255) { r = 255; }
        if (g > 255) { g = 255; }
        if (b > 255) { b = 255; }
        
        return new Color(r, g, b);
    }
    
    public static Color makeColourDarker(Color c, int amount) {
        int r = c.getRed() - amount;
        int g = c.getGreen() - amount;
        int b = c.getBlue() - amount;
        
        if (r < 0) { r = 0; }
        if (g < 0) { g = 0; }
        if (b < 0) { b = 0; }
        
        return new Color(r, g, b);
    }
    
    public static Color addColourNoise(Color c, int amount) {
        Random rnd = new Random();
        int r = c.getRed() - amount + rnd.nextInt((amount * 2) + 1);
        int g = c.getGreen() - amount + rnd.nextInt((amount * 2) + 1);
        int b = c.getBlue() - amount + rnd.nextInt((amount * 2) + 1);
        
        if (r < 0) { r = 0; }
        if (g < 0) { g = 0; }
        if (b < 0) { b = 0; }
        if (r > 255) { r = 255; }
        if (g > 255) { g = 255; }
        if (b > 255) { b = 255; }
        
        return new Color(r, g, b);
    }
    
    public static Color addShadeNoise(Color c, int amount) {
        Random rnd = new Random();
        
        int shadeAmount = rnd.nextInt(amount * 2);
        
        int r = c.getRed() - amount + shadeAmount;
        int g = c.getGreen() - amount + shadeAmount;
        int b = c.getBlue() - amount + shadeAmount;
        
        if (r < 0) { r = 0; }
        if (g < 0) { g = 0; }
        if (b < 0) { b = 0; }
        if (r > 255) { r = 255; }
        if (g > 255) { g = 255; }
        if (b > 255) { b = 255; }
        
        return new Color(r, g, b);
    }
    
    public static enum ColourFamily {
        MINECRAFT("minecraft"),
        MINECRAFT_WOOL("wool"),
        PASTEL("pastel");
        
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
    
    public static int[] minecraftChatColours = {
        0xFFFFFF, 0xFFFF55, 0xFF55FF, 0xFF5555,
        0x55FFFF, 0x55FF55, 0x5555FF, 0x555555,
        0xAAAAAA, 0xFFAA00, 0xAA00AA, 0xAA0000,
        0x00AAAA, 0x00AA00, 0x0000AA, 0x000000
    };
    
    public static int[] minecraftWoolColours = {
        0xDDDDDD, 0xDB7D3E, 0xB350BC, 0x6B8AC9,
        0xB1A627, 0x41AE38, 0xD08499, 0x404040,
        0x9AA1A1, 0x2E6E89, 0x7E3DB5, 0x2E388D,
        0x4F321F, 0x35461B, 0x963430, 0x191616
    };
    
    
    
    public static int getMinecraftColor(int meta, ColourFamily colourFamily) {
        switch (colourFamily) {
        case MINECRAFT:
            if (meta >= 0 && meta < minecraftChatColours.length) {
                return minecraftChatColours[meta];
            }
            break;
        case MINECRAFT_WOOL:
            if (meta >= 0 && meta < minecraftWoolColours.length) {
                return minecraftWoolColours[meta];
            }
            break;
        case PASTEL:
            int[] pastelColours = {
                    0xEEEEEE, 0xFFFFCC, 0xFFCCFF, 0xFFCCCC,
                    0xDDFFFF, 0xDDFFDD, 0xDDDDFF, 0xDDDDDD,
                    0xCCCCCC, 0xFFEECC, 0xFFEEFF, 0xFFEEEE,
                    0xEEFFFF, 0xFFEEFF, 0xFFFFEE, 0x808080
                    };
            if (meta >= 0 && meta < pastelColours.length) {
                return pastelColours[meta];
            }
            break;
        }
        return 0x000000;
    }

    public static String getMinecraftColorName(int meta) {
        if (meta == 0) {
            return "White";
        }
        if (meta == 1) {
            return "Orange";
        }
        if (meta == 2) {
            return "Magenta";
        }
        if (meta == 3) {
            return "Light blue";
        }
        if (meta == 4) {
            return "Yellow";
        }
        if (meta == 5) {
            return "Lime";
        }
        if (meta == 6) {
            return "Pink";
        }
        if (meta == 7) {
            return "Gray";
        }
        if (meta == 8) {
            return "Light gray";
        }
        if (meta == 9) {
            return "Cyan";
        }
        if (meta == 10) {
            return "Purple";
        }
        if (meta == 11) {
            return "Blue";
        }
        if (meta == 12) {
            return "Brown";
        }
        if (meta == 13) {
            return "Green";
        }
        if (meta == 14) {
            return "Red";
        }
        if (meta == 15) {
            return "Black";
        }
        return "?";
    }

    public static String getMinecraftColorOreName(int meta) {
        if (meta == 0) {
            return "dyeWhite";
        }
        if (meta == 1) {
            return "dyeOrange";
        }
        if (meta == 2) {
            return "dyeMagenta";
        }
        if (meta == 3) {
            return "dyeLightBlue";
        }
        if (meta == 4) {
            return "dyeYellow";
        }
        if (meta == 5) {
            return "dyeLime";
        }
        if (meta == 6) {
            return "dyePink";
        }
        if (meta == 7) {
            return "dyeGray";
        }
        if (meta == 8) {
            return "dyeLightGray";
        }
        if (meta == 9) {
            return "dyeCyan";
        }
        if (meta == 10) {
            return "dyePurple";
        }
        if (meta == 11) {
            return "dyeBlue";
        }
        if (meta == 12) {
            return "dyeBrown";
        }
        if (meta == 13) {
            return "dyeGreen";
        }
        if (meta == 14) {
            return "dyeRed";
        }
        if (meta == 15) {
            return "dyeBlack";
        }
        return "?";
    }
}
