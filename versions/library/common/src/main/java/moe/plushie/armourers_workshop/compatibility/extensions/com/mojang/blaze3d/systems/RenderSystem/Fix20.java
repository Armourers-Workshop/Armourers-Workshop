package moe.plushie.armourers_workshop.compatibility.extensions.com.mojang.blaze3d.systems.RenderSystem;

import moe.plushie.armourers_workshop.api.annotation.Available;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.20, )")
@Extension
public class Fix20 {

    public static void disableTexture(@ThisClass Class<?> clazz) {
    }

    public static void enableTexture(@ThisClass Class<?> clazz) {
    }
}
