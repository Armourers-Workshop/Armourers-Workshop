package moe.plushie.armourers_workshop.core.utils;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class OpenNativeImage {

    private final NativeImage impl;

    private OpenNativeImage(NativeImage impl) {
        this.impl = impl;
    }

    public static OpenNativeImage of(NativeImage image) {
        if (image != null) {
            return new OpenNativeImage(image);
        }
        return null;
    }

    public static OpenNativeImage of(int textureId, int width, int height) {
        var image = new OpenNativeImage(new NativeImage(width, height, true));
        image.impl.downloadTexture(textureId, false);
        return image;
    }

    public int getPixel(int x, int y) {
        // ABGR => ARGB
        int color = impl.getPixelRGBA(x, y);
        int red = (color << 16) & 0xff0000;
        int blue = (color >> 16) & 0x0000ff;
        return (color & 0xff00ff00) | red | blue;
    }

    public void setPixel(int x, int y, int color) {
        // ARGB => ABGR
        var red = (color >> 16) & 0x0000ff;
        var blue = (color << 16) & 0xff0000;
        impl.setPixelRGBA(x, y, (color & 0xff00ff00) | red | blue);
    }

    public void copyFrom(OpenNativeImage image) {
        impl.copyFrom(image.impl);
    }

    @Override
    public OpenNativeImage clone() {
        var newValue = new NativeImage(impl.format(), impl.getWidth(), impl.getHeight(), true);
        return new OpenNativeImage(newValue);
    }

    public int width() {
        return impl.getWidth();
    }

    public int height() {
        return impl.getHeight();
    }
}
