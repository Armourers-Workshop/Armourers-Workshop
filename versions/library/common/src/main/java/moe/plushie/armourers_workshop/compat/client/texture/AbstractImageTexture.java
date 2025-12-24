package moe.plushie.armourers_workshop.compat.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;

@Available("[1.16, 1.22)")
@OnlyIn(Dist.CLIENT)
public class AbstractImageTexture extends AbstractTexture {

    protected final String name;

    protected NativeImage pixels;

    public AbstractImageTexture(String name) {
        this.name = name;
    }

    protected void prepare(int width, int height, boolean bl) {
        // close old resources if needed.
        if (this.pixels != null) {
            this.pixels.close();
        }
        // create a new pixel buffer.
        this.pixels = new NativeImage(width, height, bl);
        TextureUtil.prepareImage(this.getId(), width, height);
    }

    protected void upload() {
        if (this.pixels == null) {
            return;
        }
        this.bind();
        this.pixels.upload(0, 0, 0, false);
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {
        // nop.
    }

    @Override
    public void close() {
        if (this.pixels != null) {
            this.pixels.close();
            this.pixels = null;
        }
        super.close();
    }

    public String name() {
        return name;
    }
}
