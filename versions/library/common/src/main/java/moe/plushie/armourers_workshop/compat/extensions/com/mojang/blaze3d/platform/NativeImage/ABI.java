package moe.plushie.armourers_workshop.compat.extensions.com.mojang.blaze3d.platform.NativeImage;

import com.mojang.blaze3d.platform.NativeImage;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.Colors;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[16, 26)")
@Extension
public class ABI {

    public static void setPixel(@This NativeImage image, int x, int y, int color) {
        image.setPixelRGBA(x, y, Colors.ARGBtoABGR(color));
    }

    public static int getPixel(@This NativeImage image, int x, int y) {
        return Colors.ABGRtoARGB(image.getPixelRGBA(x, y));
    }
}
