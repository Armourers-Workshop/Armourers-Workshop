package moe.plushie.armourers_workshop.compat.client;

import com.mojang.blaze3d.platform.NativeImage;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.utils.OpenNativeImage;
import net.minecraft.client.renderer.texture.AbstractTexture;

@OnlyIn(Dist.CLIENT)
public class AbstractNativeImage extends OpenNativeImage {

    private final NativeImage impl;

    public AbstractNativeImage(NativeImage impl) {
        super(impl.getWidth(), impl.getHeight());
        this.impl = impl;
    }

    public static AbstractNativeImage of(NativeImage image) {
        if (image != null) {
            return new AbstractNativeImage(image);
        }
        return null;
    }

    public static AbstractNativeImage fromTexture(AbstractTexture texture, int width, int height) {
        var image = new AbstractNativeImage(new NativeImage(width, height, true));
        image.impl.downloadFromTexture(texture);
        return image;
    }

    @Override
    public int getPixel(int x, int y) {
        // ABGR => ARGB
        int color = impl.getPixelRGBA(x, y);
        int red = (color << 16) & 0xff0000;
        int blue = (color >> 16) & 0x0000ff;
        return (color & 0xff00ff00) | red | blue;
    }

    @Override
    public void setPixel(int x, int y, int color) {
        // ARGB => ABGR
        var red = (color >> 16) & 0x0000ff;
        var blue = (color << 16) & 0xff0000;
        impl.setPixelRGBA(x, y, (color & 0xff00ff00) | red | blue);
    }

    public void copyFrom(AbstractNativeImage image) {
        impl.copyFrom(image.impl);
    }

    @Override
    public AbstractNativeImage clone() {
        var newValue = new NativeImage(impl.format(), impl.getWidth(), impl.getHeight(), true);
        return new AbstractNativeImage(newValue);
    }
}
