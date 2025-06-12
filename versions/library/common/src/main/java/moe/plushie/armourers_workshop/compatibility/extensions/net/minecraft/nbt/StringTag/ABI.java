package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.nbt.StringTag;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.nbt.StringTag;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, 1.23)")
@Extension
public class ABI {

    public static String value(@This StringTag tag) {
        return tag.getAsString();
    }
}
