package moe.plushie.armourers_workshop.compatibility.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IRenderTypeBuilder;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.client.other.SkinVertexFormat;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Available("[1.18, )")
@Environment(EnvType.CLIENT)
public class AbstractRenderTypeImpl extends RenderType {

    private static final Map<SkinVertexFormat, Supplier<IRenderTypeBuilder>> MAPPER = _make(it -> {

        it.put(SkinVertexFormat.LINE, () -> _builder(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.DEBUG_LINES, POSITION_COLOR_SHADER));
        it.put(SkinVertexFormat.LINE_STRIP, () -> _builder(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.DEBUG_LINE_STRIP, POSITION_COLOR_SHADER));

        it.put(SkinVertexFormat.IMAGE, () -> _builder(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, POSITION_COLOR_TEX_LIGHTMAP_SHADER).overlay().lightmap());

        it.put(SkinVertexFormat.BLIT_MASK, () -> _builder(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, POSITION_COLOR_SHADER));

        it.put(SkinVertexFormat.GUI_COLOR, () -> _builder(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, POSITION_COLOR_SHADER));
        it.put(SkinVertexFormat.GUI_IMAGE, () -> _builder(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, POSITION_TEX_SHADER));
        it.put(SkinVertexFormat.GUI_HIGHLIGHTED_TEXT, () -> _builder(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, POSITION_SHADER));

        it.put(SkinVertexFormat.BLOCK, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_SOLID_SHADER));
        it.put(SkinVertexFormat.BLOCK_CUTOUT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_CUTOUT_SHADER));

        it.put(SkinVertexFormat.ENTITY_CUTOUT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_CUTOUT_SHADER));
        it.put(SkinVertexFormat.ENTITY_CUTOUT_NO_CULL, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER));
        it.put(SkinVertexFormat.ENTITY_TRANSLUCENT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER));
        it.put(SkinVertexFormat.ENTITY_ALPHA, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_ALPHA_SHADER));

        it.put(SkinVertexFormat.SKIN_BLOCK_FACE_SOLID, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_SOLID_SHADER).overlay().lightmap());
        it.put(SkinVertexFormat.SKIN_BLOCK_FACE_LIGHTING, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_SHADOW_SHADER).overlay().lightmap().emissive());
        it.put(SkinVertexFormat.SKIN_BLOCK_FACE_TRANSLUCENT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_SOLID_SHADER).overlay().lightmap());
        it.put(SkinVertexFormat.SKIN_BLOCK_FACE_LIGHTING_TRANSLUCENT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_SHADOW_SHADER).overlay().lightmap().emissive());

        it.put(SkinVertexFormat.SKIN_CUBE_FACE_SOLID, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_CUTOUT_SHADER).overlay().lightmap());
        it.put(SkinVertexFormat.SKIN_CUBE_FACE_LIGHTING, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENERGY_SWIRL_SHADER).overlay().lightmap().emissive());
        it.put(SkinVertexFormat.SKIN_CUBE_FACE_TRANSLUCENT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_SOLID_SHADER).overlay().lightmap());
        it.put(SkinVertexFormat.SKIN_CUBE_FACE_LIGHTING_TRANSLUCENT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENERGY_SWIRL_SHADER).overlay().lightmap().emissive());

        it.put(SkinVertexFormat.SKIN_MESH_FACE_SOLID, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, RENDERTYPE_ENTITY_CUTOUT_SHADER).overlay().lightmap());
        it.put(SkinVertexFormat.SKIN_MESH_FACE_LIGHTING, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, RENDERTYPE_ENERGY_SWIRL_SHADER).overlay().lightmap().emissive());
        it.put(SkinVertexFormat.SKIN_MESH_FACE_TRANSLUCENT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, RENDERTYPE_ENTITY_SOLID_SHADER).overlay().lightmap());
        it.put(SkinVertexFormat.SKIN_MESH_FACE_LIGHTING_TRANSLUCENT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, RENDERTYPE_ENERGY_SWIRL_SHADER).overlay().lightmap().emissive());
    });


    public AbstractRenderTypeImpl(String name, RenderType delegate, boolean affectsCrumbling, boolean sortUpload, Runnable setupRenderState, Runnable clearRenderState) {
        super(name, delegate.format(), delegate.mode(), delegate.bufferSize(), affectsCrumbling, sortUpload, () -> {
            delegate.setupRenderState();
            setupRenderState.run();
        }, () -> {
            clearRenderState.run();
            delegate.clearRenderState();
        });
    }

    public static IRenderTypeBuilder builder(SkinVertexFormat format) {
        var provider = MAPPER.get(format);
        if (provider != null) {
            var builder = provider.get();
            if (builder instanceof Builder builder1) {
                builder1.format(format);
            }
            return builder;
        }
        throw new RuntimeException("can't supported render mode");
    }

    private static Builder _builder(VertexFormat format, VertexFormat.Mode mode, ShaderStateShard shader) {
        var builder = new Builder(format, mode);
        builder.stateBuilder.setShaderState(shader);
        return builder;
    }

    private static <T, U> HashMap<T, U> _make(Consumer<HashMap<T, U>> consumer) {
        var map = new HashMap<T, U>();
        consumer.accept(map);
        return map;
    }

    public static class Builder extends AbstractRenderType.Builder {

        private static final Map<IRenderType.Texturing, TexturingStateShard> TABLE_TEXTURING = _make(it -> {
//            it.put(Texturing.ENTITY_COLOR_OFFSET, new TexturingStateShard("aw_offset_texturing", RenderSystem::setupColorOffsetState, RenderSystem::clearColorOffsetState));
        });


        private static final Map<IRenderType.Target, OutputStateShard> TABLE_OUTPUT = _make(it -> {
            it.put(IRenderType.Target.MAIN, MAIN_TARGET);
            it.put(IRenderType.Target.OUTLINE, OUTLINE_TARGET);
            it.put(IRenderType.Target.TRANSLUCENT, TRANSLUCENT_TARGET);
            it.put(IRenderType.Target.CLOUDS, CLOUDS_TARGET);
            it.put(IRenderType.Target.WEATHER, WEATHER_TARGET);
            it.put(IRenderType.Target.PARTICLES, PARTICLES_TARGET);
            it.put(IRenderType.Target.ITEM_ENTITY, ITEM_ENTITY_TARGET);
        });

        private static final Map<IRenderType.Transparency, TransparencyStateShard> TABLE_TRANSPARENCY = _make(it -> {
            it.put(IRenderType.Transparency.DEFAULT, TRANSLUCENT_TRANSPARENCY);
            it.put(IRenderType.Transparency.TRANSLUCENT, TRANSLUCENT_TRANSPARENCY);
            it.put(IRenderType.Transparency.NONE, NO_TRANSPARENCY);
        });

        private static final Map<IRenderType.WriteMask, WriteMaskStateShard> TABLE_WRITE_MASK = _make(it -> {
            it.put(IRenderType.WriteMask.NONE, new WriteMaskStateShard(false, false));
            it.put(IRenderType.WriteMask.COLOR_DEPTH_WRITE, COLOR_DEPTH_WRITE);
            it.put(IRenderType.WriteMask.COLOR_WRITE, COLOR_WRITE);
            it.put(IRenderType.WriteMask.DEPTH_WRITE, DEPTH_WRITE);
        });

        private static final Map<IRenderType.DepthTest, DepthTestStateShard> TABLE_DEPTH_TEST = _make(it -> {
            it.put(IRenderType.DepthTest.NONE, NO_DEPTH_TEST);
            it.put(IRenderType.DepthTest.EQUAL, EQUAL_DEPTH_TEST);
            it.put(IRenderType.DepthTest.LESS_EQUAL, LEQUAL_DEPTH_TEST);
        });

        boolean isOutline = false;
        boolean affectsCrumbling = false;
        boolean sortOnUpload = false;

        CompositeState.CompositeStateBuilder stateBuilder = CompositeState.builder();

        final VertexFormat format;
        final VertexFormat.Mode mode;

        private Builder(VertexFormat format, VertexFormat.Mode mode) {
            this.format = format;
            this.mode = mode;
            this.setupDefault();
        }

        private void setupDefault() {
            stateBuilder = stateBuilder.setCullState(NO_CULL);
            // stateBuilder.setAlphaState(DEFAULT_ALPHA);
        }

        @Override
        public IRenderTypeBuilder texture(IResourceLocation texture, boolean blur, boolean mipmap) {
            this.stateBuilder = stateBuilder.setTextureState(new TextureStateShard(texture.toLocation(), blur, mipmap));
            return this;
        }

        @Override
        public IRenderTypeBuilder texturing(IRenderType.Texturing texturing) {
            this.stateBuilder = stateBuilder.setTexturingState(TABLE_TEXTURING.getOrDefault(texturing, DEFAULT_TEXTURING));
            return this;
        }

        @Override
        public IRenderTypeBuilder target(IRenderType.Target target) {
            this.stateBuilder = stateBuilder.setOutputState(TABLE_OUTPUT.getOrDefault(target, MAIN_TARGET));
            return super.target(target);
        }

        @Override
        public IRenderTypeBuilder transparency(IRenderType.Transparency transparency) {
            this.stateBuilder = stateBuilder.setTransparencyState(TABLE_TRANSPARENCY.getOrDefault(transparency, NO_TRANSPARENCY));
            return super.transparency(transparency);
        }

        @Override
        public IRenderTypeBuilder writeMask(IRenderType.WriteMask mask) {
            this.stateBuilder = stateBuilder.setWriteMaskState(TABLE_WRITE_MASK.getOrDefault(mask, COLOR_DEPTH_WRITE));
            return this;
        }

        @Override
        public IRenderTypeBuilder depthTest(IRenderType.DepthTest test) {
            this.stateBuilder = stateBuilder.setDepthTestState(TABLE_DEPTH_TEST.getOrDefault(test, NO_DEPTH_TEST));
            return this;
        }

        @Override
        public IRenderTypeBuilder colorLogic(IRenderType.ColorLogic state) {
            this.stateBuilder = stateBuilder.setColorLogicState(state);
            return this;
        }

        @Override
        public IRenderTypeBuilder polygonOffset(float factor, float units) {
            this.stateBuilder = stateBuilder.setLayeringState(new LayeringStateShard("aw_polygon_offset_" + units, () -> {
                RenderSystem.enablePolygonOffset();
                RenderSystem.polygonOffset(factor, units);
            }, () -> {
                RenderSystem.polygonOffset(0, 0);
                RenderSystem.disablePolygonOffset();
            }));
            return this;
        }

        @Override
        public IRenderTypeBuilder lineWidth(float width) {
            this.stateBuilder = stateBuilder.setLineState(new LineStateShard(OptionalDouble.of(width)));
            return this;
        }

        @Override
        public IRenderTypeBuilder stroke(float width) {
            this.stateBuilder = stateBuilder.setLayeringState(new LayeringStateShard("aw_custom_line", () -> {
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                GL11.glLineWidth(width);
            }, () -> {
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            }));
            return this;
        }

        @Override
        public IRenderTypeBuilder cull() {
            this.stateBuilder = stateBuilder.setCullState(CULL);
            return this;
        }

        @Override
        public IRenderTypeBuilder lightmap() {
            this.stateBuilder = stateBuilder.setLightmapState(LIGHTMAP);
            return this;
        }

        @Override
        public IRenderTypeBuilder overlay() {
            this.stateBuilder = stateBuilder.setOverlayState(OVERLAY);
            return this;
        }

        @Override
        public IRenderTypeBuilder outline() {
            this.isOutline = true;
            return this;
        }

        @Override
        public IRenderTypeBuilder crumbling() {
            this.affectsCrumbling = true;
            return this;
        }

        @Override
        public IRenderTypeBuilder sortOnUpload() {
            this.sortOnUpload = true;
            return this;
        }

        @Override
        public IRenderType build(String name) {
            var renderType = AbstractRenderType.of(RenderType.create(name, format, mode, 256, affectsCrumbling, sortOnUpload, stateBuilder.createCompositeState(isOutline)));
            updater.forEach(it -> it.accept(renderType));
            return renderType;
        }

        public Builder or(Function<CompositeState.CompositeStateBuilder, CompositeState.CompositeStateBuilder> builder) {
            this.stateBuilder = builder.apply(stateBuilder);
            return this;
        }
    }
}
