package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.nbt.NumericTag;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.nbt.NumericTag;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, 1.26)")
@Extension
public class ABI {

    public static double doubleValue(@This NumericTag tag) {
        return tag.getAsDouble();
    }

    public static float floatValue(@This NumericTag tag) {
        return tag.getAsFloat();
    }

    public static long longValue(@This NumericTag tag) {
        return tag.getAsLong();
    }

    public static int intValue(@This NumericTag tag) {
        return tag.getAsInt();
    }

    public static short shortValue(@This NumericTag tag) {
        return tag.getAsShort();
    }

    public static byte byteValue(@This NumericTag tag) {
        return tag.getAsByte();
    }
}
