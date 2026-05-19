package moe.plushie.armourers_workshop.compat.client.renderer.rendertype;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IResourceKey;
import moe.plushie.armourers_workshop.compat.client.renderer.vertex.AbstractVertexFormat;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.other.SkinVertexFormat;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.function.Supplier;

@Available("[18, 26)")
public abstract class AbstractRenderTypeBuilder extends SkinRenderType.Builder {

    public static SkinRenderType create(RenderType renderType) {
        return DataContainer.of(renderType, Wrapper::new);
    }

    public static SkinRenderType.Builder create(SkinVertexFormat format) {
        var provider = Provider.INSTANCES.get(format);
        if (provider != null) {
            return provider.get();
        }
        throw new RuntimeException("can't supported render mode");
    }

    private static class Wrapper extends SkinRenderType {

        private final RenderType impl;

        private Wrapper(RenderType renderType) {
            this.format = AbstractVertexFormat.create(renderType.format(), renderType.mode());
            this.outline = renderType.outline().map(AbstractRenderType::wrap);
            this.isOutline = renderType.isOutline();
            this.name = renderType.toString().replaceAll("RenderType\\[(.+?):CompositeState.+$", "$1");
            this.bufferSize = renderType.bufferSize();
            this.impl = renderType;
        }

        @Override
        public RenderType get() {
            return impl;
        }
    }

    private static class Provider extends RenderType {

        private static final TransparencyStateShard NONE_TRANSPARENCY = new TransparencyStateShard("none_transparency", Objects::hash, Objects::hash);

        private static final Map<SkinVertexFormat, Supplier<Builder>> INSTANCES = Collections.immutableMap(it -> {

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

            it.put(SkinVertexFormat.SKIN_PARTICLE_CUTOUT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_CUTOUT_SHADER));
            it.put(SkinVertexFormat.SKIN_PARTICLE_CUTOUT_EMISSIVE, () -> _builder(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, POSITION_COLOR_TEX_LIGHTMAP_SHADER));

            it.put(SkinVertexFormat.SKIN_BLOCK_FACE_SOLID, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_SOLID_SHADER).overlay().lightmap());
            it.put(SkinVertexFormat.SKIN_BLOCK_FACE_EMISSIVE, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_SHADOW_SHADER).overlay().lightmap().emissive());
            it.put(SkinVertexFormat.SKIN_BLOCK_FACE_TRANSLUCENT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_SOLID_SHADER).overlay().lightmap());
            it.put(SkinVertexFormat.SKIN_BLOCK_FACE_TRANSLUCENT_EMISSIVE, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_SHADOW_SHADER).overlay().lightmap().emissive());

            it.put(SkinVertexFormat.SKIN_CUBE_FACE_SOLID, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_CUTOUT_SHADER).overlay().lightmap());
            it.put(SkinVertexFormat.SKIN_CUBE_FACE_EMISSIVE, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENERGY_SWIRL_SHADER).overlay().lightmap().emissive());
            it.put(SkinVertexFormat.SKIN_CUBE_FACE_TRANSLUCENT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_CUTOUT_SHADER).overlay().lightmap());
            it.put(SkinVertexFormat.SKIN_CUBE_FACE_TRANSLUCENT_EMISSIVE, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENERGY_SWIRL_SHADER).overlay().lightmap().emissive());

            it.put(SkinVertexFormat.SKIN_MESH_FACE_SOLID, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, RENDERTYPE_ENTITY_CUTOUT_SHADER).overlay().lightmap());
            it.put(SkinVertexFormat.SKIN_MESH_FACE_EMISSIVE, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, RENDERTYPE_ENERGY_SWIRL_SHADER).overlay().lightmap().emissive());
            it.put(SkinVertexFormat.SKIN_MESH_FACE_TRANSLUCENT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, RENDERTYPE_ENTITY_CUTOUT_SHADER).overlay().lightmap());
            it.put(SkinVertexFormat.SKIN_MESH_FACE_TRANSLUCENT_EMISSIVE, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, RENDERTYPE_ENERGY_SWIRL_SHADER).overlay().lightmap().emissive());
        });

        private Provider(String name, RenderType delegate, boolean affectsCrumbling, boolean sortUpload, Runnable setupRenderState, Runnable clearRenderState) {
            super(name, delegate.format(), delegate.mode(), delegate.bufferSize(), affectsCrumbling, sortUpload, setupRenderState, clearRenderState);
        }

        private static Builder _builder(VertexFormat format, VertexFormat.Mode mode, ShaderStateShard shader) {
            var builder = new Builder(format, mode);
            builder.stateBuilder.setShaderState(shader);
            return builder;
        }

        private static class Builder extends AbstractRenderTypeBuilder {

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
            public Builder texture(IResourceKey texture, boolean blur, boolean mipmap) {
                stateBuilder.setTextureState(new TextureStateShard(texture.get(), blur, mipmap));
                super.texture(texture, blur, mipmap);
                return this;
            }

            @Override
            public Builder target(SkinRenderType.Target target) {
                stateBuilder.setOutputState(switch (target) {
                    case MAIN -> MAIN_TARGET;
                    case OUTLINE -> OUTLINE_TARGET;
                    case TRANSLUCENT -> TRANSLUCENT_TARGET;
                    case CLOUDS -> CLOUDS_TARGET;
                    case WEATHER -> WEATHER_TARGET;
                    case PARTICLES -> PARTICLES_TARGET;
                    case ITEM_ENTITY -> ITEM_ENTITY_TARGET;
                });
                return this;
            }

            @Override
            public Builder blend(SkinRenderType.BlendMode mode) {
                stateBuilder.setTransparencyState(switch (mode) {
                    case NONE -> NONE_TRANSPARENCY;
                    case NORMAL -> NO_TRANSPARENCY;
                    case TRANSLUCENT -> TRANSLUCENT_TRANSPARENCY;
                    case ADDITIVE -> ADDITIVE_TRANSPARENCY;
                    case INVERT -> ADDITIVE_TRANSPARENCY;
                    default -> NO_TRANSPARENCY;
                });
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
            public Builder depthTest(SkinRenderType.DepthTestMode mode) {
                stateBuilder.setDepthTestState(switch (mode) {
                    case NO_DEPTH_TEST -> NO_DEPTH_TEST;
                    case EQUAL_DEPTH_TEST -> EQUAL_DEPTH_TEST;
                    case LEQUAL_DEPTH_TEST -> LEQUAL_DEPTH_TEST;
                    case LESS_DEPTH_TEST -> NO_DEPTH_TEST;
                    case GREATER_DEPTH_TEST -> NO_DEPTH_TEST;
                });
                return this;
            }

            @Override
            public Builder polygonMode(SkinRenderType.PolygonMode mode) {
                customStates.add(new TexturingStateShard("polygon_offset_mode", () -> {
                    if (mode == SkinRenderType.PolygonMode.WIREFRAME) {
                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                    }
                }, () -> {
                    if (mode == SkinRenderType.PolygonMode.WIREFRAME) {
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

            private Builder or(Function<CompositeState.CompositeStateBuilder, CompositeState.CompositeStateBuilder> builder) {
                builder.apply(stateBuilder);
                return this;
            }

            @Override
            protected SkinRenderType create(String registryName) {
                var renderType = RenderType.create(registryName, format, mode, 256, affectsCrumbling, sortOnUpload, state());
                return AbstractRenderType.wrap(renderType);
            }
        }
    }
}
