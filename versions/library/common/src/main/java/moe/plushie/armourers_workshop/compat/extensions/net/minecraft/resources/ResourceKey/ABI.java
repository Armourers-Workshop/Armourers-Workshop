package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.resources.ResourceKey;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[16, 26)")
public class ABI {

    public static ResourceLocation identifier(@This ResourceKey<?> key) {
        return key.location();
    }
}
