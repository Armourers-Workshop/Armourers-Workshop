package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.core.IResourceKey;
import moe.plushie.armourers_workshop.compat.client.renderer.rendertype.AbstractRenderType;

import java.util.Optional;

public interface IRenderType extends AbstractRenderType {

    boolean isEmissive();

    boolean isTranslucent();

    boolean isOutline();

    int bufferSize();

    int ordinal();

    String name();

    Group group();

    IVertexFormat.Mode mode();

    IVertexFormat format();

    Optional<IRenderType> outline();

    enum Target {
        MAIN, OUTLINE, TRANSLUCENT, CLOUDS, WEATHER, PARTICLES, ITEM_ENTITY
    }

    enum Group {
        MAIN,

        SOLID_BLOCKS,
        CUTOUT_BLOCKS,
        TRANSLUCENT_BLOCKS,
        OUTLINE_BLOCKS,

        SOLID_ENTITIES,
        CUTOUT_ENTITIES,
        TRANSLUCENT_ENTITIES,
        OUTLINE_ENTITIES,

        CLOUDS,
        WEATHER,
        PARTICLES;

        /// Returns the group outline version, if the group not support outline will return main.
        public Group outline() {
            return switch (this) {
                case OUTLINE_BLOCKS, OUTLINE_ENTITIES -> this;
                case SOLID_BLOCKS, CUTOUT_BLOCKS, TRANSLUCENT_BLOCKS -> OUTLINE_BLOCKS;
                case SOLID_ENTITIES, CUTOUT_ENTITIES, TRANSLUCENT_ENTITIES -> OUTLINE_ENTITIES;
                default -> MAIN;
            };
        }
    }

    enum DepthTestMode {
        NO_DEPTH_TEST,
        EQUAL_DEPTH_TEST,
        LEQUAL_DEPTH_TEST,
        LESS_DEPTH_TEST,
        GREATER_DEPTH_TEST;
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
        INVERT,
    }

    enum PolygonMode {
        FILL,
        WIREFRAME
    }

    interface Builder {

        default Builder texture(IResourceKey texture) {
            return texture(texture, false, false);
        }

        Builder texture(IResourceKey texture, boolean blur, boolean mipmap);


        Builder group(Group group);

        Builder target(Target target);


        Builder polygonMode(PolygonMode mode);

        Builder polygonOffset(float factor, float units);


        Builder stroke(float width);

        Builder lineWidth(float width);


        Builder cull();

        Builder blend(BlendMode blendFunction);


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
