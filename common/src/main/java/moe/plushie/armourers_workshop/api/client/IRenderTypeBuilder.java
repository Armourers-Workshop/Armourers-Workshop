package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IRenderTypeBuilder {

    default IRenderTypeBuilder texture(IResourceLocation texture) {
        return texture(texture, false, false);
    }

    IRenderTypeBuilder texture(IResourceLocation texture, boolean blur, boolean mipmap);

    IRenderTypeBuilder texturing(IRenderType.Texturing texturing);

    IRenderTypeBuilder target(IRenderType.Target target);

    IRenderTypeBuilder transparency(IRenderType.Transparency transparency);

    IRenderTypeBuilder writeMask(IRenderType.WriteMask state);

    IRenderTypeBuilder depthTest(IRenderType.DepthTest state);

    IRenderTypeBuilder colorLogic(IRenderType.ColorLogic state);

    IRenderTypeBuilder polygonOffset(float factor, float units);

    IRenderTypeBuilder stroke(float width);

    IRenderTypeBuilder lineWidth(float width);

    IRenderTypeBuilder cull();

    IRenderTypeBuilder lightmap();

    IRenderTypeBuilder overlay();

    IRenderTypeBuilder emissive();

    IRenderTypeBuilder outline();

    IRenderTypeBuilder crumbling();

    IRenderTypeBuilder sortOnUpload();

    IRenderTypeBuilder ordinal(int ordinal);

    IRenderType build(String name);
}
