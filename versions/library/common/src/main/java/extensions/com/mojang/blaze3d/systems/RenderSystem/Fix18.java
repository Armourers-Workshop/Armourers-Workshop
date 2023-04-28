package extensions.com.mojang.blaze3d.systems.RenderSystem;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;

@Available("[1.18, )")
@Extension
public class Fix18 {

    public static void disableAlphaTest(@ThisClass Class<?> clazz) {
    }

    public static void enableAlphaTest(@ThisClass Class<?> clazz) {
    }
}
