package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import net.minecraft.client.renderer.RenderType;

import java.util.Optional;
import java.util.function.Supplier;

public interface IRenderType extends Supplier<RenderType> {

    boolean isEmissive();

    boolean isTranslucent();

    boolean isOutline();

    int bufferSize();

    int ordinal();

    IVertexFormat.Mode mode();

    IVertexFormat format();

    Optional<IRenderType> outline();

    enum Target {
        MAIN, OUTLINE, TRANSLUCENT, CLOUDS, WEATHER, PARTICLES, ITEM_ENTITY
    }

    enum DepthTestMode {
        NO_DEPTH_TEST,
        EQUAL_DEPTH_TEST,
        LEQUAL_DEPTH_TEST,
        LESS_DEPTH_TEST,
        GREATER_DEPTH_TEST;
    }

    enum LogicOp {
        NONE, OR_REVERSE
    }

    enum BlendMode {
        NONE,
        NORMAL,
        LIGHTNING,
        GLINT,
        OVERLAY,
        TRANSLUCENT,
        TRANSLUCENT_PREMULTIPLIED_ALPHA,
        ADDITIVE,
        ENTITY_OUTLINE_BLIT
    }

    enum PolygonMode {
        FILL,
        WIREFRAME
    }

    interface Builder {

        default Builder texture(IResourceLocation texture) {
            return texture(texture, false, false);
        }

        Builder texture(IResourceLocation texture, boolean blur, boolean mipmap);


        Builder target(Target target);


        Builder polygonMode(PolygonMode mode);

        Builder polygonOffset(float factor, float units);


        Builder stroke(float width);

        Builder lineWidth(float width);


        Builder cull();

        Builder blend(BlendMode blendFunction);


        Builder colorLogic(LogicOp op);


        default Builder colorWrite(boolean bl) {
            return colorWrite(bl, bl);
        }

        Builder colorWrite(boolean writeColor, boolean writeAlpha);


        Builder depthWrite(boolean writeDepth);

        Builder depthTest(DepthTestMode depthTestFunction);


        Builder lightmap();

        Builder overlay();

        Builder emissive();

        Builder outline();

        Builder crumbling();

        Builder sortOnUpload();

        Builder ordinal(int ordinal);

        IRenderType build(String name);
    }
}
