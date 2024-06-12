package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;

@Environment(EnvType.CLIENT)
public interface IRenderTypeBuilder {

    default IRenderTypeBuilder texture(IResourceLocation texture) {
        return texture(texture, false, false);
    }

    IRenderTypeBuilder texture(IResourceLocation texture, boolean blur, boolean mipmap);

    IRenderTypeBuilder texturing(Texturing texturing);

    IRenderTypeBuilder target(Target target);

    IRenderTypeBuilder transparency(Transparency transparency);

    IRenderTypeBuilder writeMask(WriteMask state);

    IRenderTypeBuilder depthTest(DepthTest state);

    IRenderTypeBuilder colorLogic(ColorLogic state);

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
        MAIN, OUTLINE, TRANSLUCENT, CLOUDS, WEATHER, PARTICLES, ITEM_ENTITY
    }

    enum Transparency {
        NONE, DEFAULT, TRANSLUCENT
    }

    enum Texturing {
    }

    enum WriteMask {
        NONE, COLOR_DEPTH_WRITE, COLOR_WRITE, DEPTH_WRITE
    }

    enum DepthTest {
        NONE, EQUAL, LESS_EQUAL
    }

    enum ColorLogic {
        NONE, OR_REVERSE
    }
}
