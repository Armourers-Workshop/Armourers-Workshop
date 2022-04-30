package moe.plushie.armourers_workshop.utils;

public final class BitwiseUtils {
    
    public static int getUByteFromInt(int source, int index) {
        return source >>> ((3 - index) * 8) & 0xFF;
    }
    
    public static int setUByteToInt(int target, int index, int value) {
        int[] bytes = new int[4];
        bytes[0] = target >>> 24 & 0xFF;
        bytes[1] = target >>> 16 & 0xFF;
        bytes[2] = target >>> 8 & 0xFF;
        bytes[3] = target & 0xFF;
        bytes[index] = value;
        return (bytes[0] << 24) + (bytes[1] << 16) + (bytes[2] << 8) + bytes[3];
    }
}
