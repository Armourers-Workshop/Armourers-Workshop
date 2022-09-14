package moe.plushie.armourers_workshop.api.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

@Environment(value = EnvType.CLIENT)
public interface IRenderTypeBuilder {

    default IRenderTypeBuilder texture(ResourceLocation texture) {
        return texture(texture, false, false);
    }

    IRenderTypeBuilder texture(ResourceLocation texture, boolean blur, boolean mipmap);

    IRenderTypeBuilder texturing(Texturing texturing);

    IRenderTypeBuilder target(Target target);

    IRenderTypeBuilder transparency(Transparency transparency);

    IRenderTypeBuilder writeMask(WriteMask state);

    IRenderTypeBuilder depthTest(DepthTest state);

    IRenderTypeBuilder polygonOffset(float factor, float units);

    IRenderTypeBuilder stroke(float width);

    IRenderTypeBuilder lineWidth(float width);

    IRenderTypeBuilder cull();

    IRenderTypeBuilder lightmap();

    IRenderTypeBuilder overlay();

    IRenderTypeBuilder outline();

    IRenderTypeBuilder crumbling();

    IRenderTypeBuilder sortOnUpload();

    RenderType build(String name);

    enum Target {
        MAIN, TRANSLUCENT
    }

    enum Transparency {
        NONE, TRANSLUCENT
    }

    enum Texturing {
    }

    enum WriteMask {
        COLOR_DEPTH_WRITE, COLOR_WRITE, DEPTH_WRITE
    }

    enum DepthTest {
        NONE, EQUAL, LESS_EQUAL
    }
}
