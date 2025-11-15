package moe.plushie.armourers_workshop.compat.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.client.other.SkinVertexFormat;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.function.Supplier;

@Available("[1.18, 1.22)")
public class AbstractRenderTypeImpl extends RenderType {

    private static final Map<SkinVertexFormat, Supplier<Builder>> MAPPER = Collections.immutableMap(it -> {

        it.put(SkinVertexFormat.LINE, () -> _builder(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.DEBUG_LINES, POSITION_COLOR_SHADER));
        it.put(SkinVertexFormat.LINE_STRIP, () -> _builder(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.DEBUG_LINE_STRIP, POSITION_COLOR_SHADER));

        it.put(SkinVertexFormat.BLIT_MASK, () -> _builder(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, POSITION_COLOR_SHADER));
        it.put(SkinVertexFormat.BLIT_TEXTURED, () -> _builder(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, POSITION_COLOR_TEX_LIGHTMAP_SHADER).overlay().lightmap());

        it.put(SkinVertexFormat.GUI_COLOR, () -> _builder(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, POSITION_COLOR_SHADER));
        it.put(SkinVertexFormat.GUI_TEXTURED, () -> _builder(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, POSITION_TEX_SHADER));

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

    public static IRenderType.Builder builder(SkinVertexFormat format) {
        var provider = MAPPER.get(format);
        if (provider != null) {
            var builder = provider.get();
            builder.format(format);
            return builder;
        }
        throw new RuntimeException("can't supported render mode");
    }

    private static Builder _builder(VertexFormat format, VertexFormat.Mode mode, ShaderStateShard shader) {
        var builder = new Builder(format, mode);
        builder.stateBuilder.setShaderState(shader);
        return builder;
    }

    private static class Builder extends AbstractRenderType.Builder {

        private static final Map<IRenderType.Target, OutputStateShard> TABLE_OUTPUT = Collections.immutableMap(it -> {
            it.put(IRenderType.Target.MAIN, MAIN_TARGET);
            it.put(IRenderType.Target.OUTLINE, OUTLINE_TARGET);
            it.put(IRenderType.Target.TRANSLUCENT, TRANSLUCENT_TARGET);
            it.put(IRenderType.Target.CLOUDS, CLOUDS_TARGET);
            it.put(IRenderType.Target.WEATHER, WEATHER_TARGET);
            it.put(IRenderType.Target.PARTICLES, PARTICLES_TARGET);
            it.put(IRenderType.Target.ITEM_ENTITY, ITEM_ENTITY_TARGET);
        });

        private static final Map<IRenderType.BlendMode, TransparencyStateShard> TABLE_TRANSPARENCY = Collections.immutableMap(it -> {
            it.put(IRenderType.BlendMode.NONE, new TransparencyStateShard("none_transparency", Objects::hash, Objects::hash));
            it.put(IRenderType.BlendMode.NORMAL, NO_TRANSPARENCY);
            it.put(IRenderType.BlendMode.TRANSLUCENT, TRANSLUCENT_TRANSPARENCY);
        });

        private static final Map<IRenderType.DepthTestMode, DepthTestStateShard> TABLE_DEPTH_TEST = Collections.immutableMap(it -> {
            it.put(IRenderType.DepthTestMode.NO_DEPTH_TEST, NO_DEPTH_TEST);
            it.put(IRenderType.DepthTestMode.EQUAL_DEPTH_TEST, EQUAL_DEPTH_TEST);
            it.put(IRenderType.DepthTestMode.LEQUAL_DEPTH_TEST, LEQUAL_DEPTH_TEST);
        });

        private boolean isOutline = false;
        private boolean affectsCrumbling = false;
        private boolean sortOnUpload = false;

        private boolean writeColor = true;
        private boolean writeAlpha = true;
        private boolean writeDepth = true;

        private final CompositeState.CompositeStateBuilder stateBuilder = CompositeState.builder();
        private final ArrayList<RenderStateShard> customStates = new ArrayList<>();

        private final VertexFormat format;
        private final VertexFormat.Mode mode;

        private Builder(VertexFormat format, VertexFormat.Mode mode) {
            this.format = format;
            this.mode = mode;
            this.setupDefault();
        }

        private void setupDefault() {
            stateBuilder.setCullState(NO_CULL);
            // stateBuilder.setAlphaState(DEFAULT_ALPHA);
        }

        @Override
        public Builder texture(IResourceLocation texture, boolean blur, boolean mipmap) {
            stateBuilder.setTextureState(new TextureStateShard(texture.toLocation(), blur, mipmap));
            return this;
        }

        @Override
        public Builder target(IRenderType.Target target) {
            stateBuilder.setOutputState(TABLE_OUTPUT.getOrDefault(target, MAIN_TARGET));
            super.target(target);
            return this;
        }

        @Override
        public Builder blend(IRenderType.BlendMode mode) {
            stateBuilder.setTransparencyState(TABLE_TRANSPARENCY.getOrDefault(mode, NO_TRANSPARENCY));
            super.blend(mode);
            return this;
        }

        @Override
        public Builder colorWrite(boolean writeColor, boolean writeAlpha) {
            this.writeColor = writeColor;
            this.writeAlpha = writeAlpha;
            return this;
        }

        @Override
        public Builder depthWrite(boolean writeDepth) {
            this.writeDepth = writeDepth;
            return this;
        }

        @Override
        public Builder depthTest(IRenderType.DepthTestMode mode) {
            stateBuilder.setDepthTestState(TABLE_DEPTH_TEST.getOrDefault(mode, NO_DEPTH_TEST));
            return this;
        }

        @Override
        public Builder colorLogic(IRenderType.LogicOp op) {
            stateBuilder.setColorLogicState(op);
            return this;
        }

        @Override
        public Builder polygonMode(IRenderType.PolygonMode mode) {
            customStates.add(new TexturingStateShard("polygon_offset_mode", () -> {
                if (mode == IRenderType.PolygonMode.WIREFRAME) {
                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                }
            }, () -> {
                if (mode == IRenderType.PolygonMode.WIREFRAME) {
                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                }
            }));
            return this;
        }

        @Override
        public Builder polygonOffset(float factor, float units) {
            customStates.add(new TexturingStateShard("polygon_offset", () -> {
                GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                GL11.glPolygonOffset(factor, units);
            }, () -> {
                GL11.glPolygonOffset(0, 0);
                GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            }));
            return this;
        }

        @Override
        public Builder lineWidth(float width) {
            stateBuilder.setLineState(new LineStateShard(OptionalDouble.of(width)));
            return this;
        }

        @Override
        public Builder stroke(float width) {
            customStates.add(new TexturingStateShard("stroke_line", () -> {
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                GL11.glLineWidth(width);
            }, () -> {
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            }));
            return this;
        }

        @Override
        public Builder cull() {
            stateBuilder.setCullState(CULL);
            return this;
        }

        @Override
        public Builder lightmap() {
            stateBuilder.setLightmapState(LIGHTMAP);
            return this;
        }

        @Override
        public Builder emissive() {
            super.emissive();
            return this;
        }

        @Override
        public Builder overlay() {
            stateBuilder.setOverlayState(OVERLAY);
            return this;
        }

        @Override
        public Builder outline() {
            isOutline = true;
            return this;
        }

        @Override
        public Builder crumbling() {
            affectsCrumbling = true;
            return this;
        }

        @Override
        public Builder sortOnUpload() {
            sortOnUpload = true;
            return this;
        }

        private CompositeState state() {
            if (writeColor && writeDepth) {
                stateBuilder.setWriteMaskState(COLOR_DEPTH_WRITE);
            } else if (writeColor) {
                stateBuilder.setWriteMaskState(COLOR_WRITE);
            } else if (writeDepth) {
                stateBuilder.setWriteMaskState(DEPTH_WRITE);
            } else {
                stateBuilder.setWriteMaskState(new WriteMaskStateShard(false, false));
            }
            if (!customStates.isEmpty()) {
                stateBuilder.setTexturingState(new TexturingStateShard("custom_texturing", () -> customStates.forEach(RenderStateShard::setupRenderState), () -> customStates.forEach(RenderStateShard::clearRenderState)));
            }
            return stateBuilder.createCompositeState(isOutline);
        }

        @Override
        public AbstractRenderType build(String name) {
            var renderType = AbstractRenderType.of(RenderType.create(ModConstants.key("rendertype/" + name).toString(), format, mode, 256, affectsCrumbling, sortOnUpload, state()));
            renderType.apply(updater);
            return renderType;
        }

        public Builder or(Function<CompositeState.CompositeStateBuilder, CompositeState.CompositeStateBuilder> builder) {
            builder.apply(stateBuilder);
            return this;
        }
    }
}
