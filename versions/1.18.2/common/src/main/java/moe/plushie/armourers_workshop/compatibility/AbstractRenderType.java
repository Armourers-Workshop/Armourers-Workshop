package moe.plushie.armourers_workshop.compatibility;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import moe.plushie.armourers_workshop.api.client.IRenderTypeBuilder;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderFormat;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(value = EnvType.CLIENT)
public class AbstractRenderType extends RenderType {

    public static final VertexFormat SKIN_NORMAL_FORMAT = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", DefaultVertexFormat.ELEMENT_POSITION).put("Color", DefaultVertexFormat.ELEMENT_COLOR).put("UV0", DefaultVertexFormat.ELEMENT_UV0).put("UV1", DefaultVertexFormat.ELEMENT_UV1).put("Normal", DefaultVertexFormat.ELEMENT_NORMAL).put("Padding", DefaultVertexFormat.ELEMENT_PADDING).build());

    private static final TexturingStateShard OR_REVERSE = new TexturingStateShard("aw_or_reverse", () -> {
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
    }, () -> {
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    });

    private static final Map<SkinRenderFormat, Supplier<IRenderTypeBuilder>> MAPPER = _make(it -> {

        it.put(SkinRenderFormat.LINE, () -> _builder(DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, RENDERTYPE_LINES_SHADER));
        it.put(SkinRenderFormat.IMAGE, () -> _builder(DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, POSITION_COLOR_TEX_SHADER).overlay().lightmap());

        it.put(SkinRenderFormat.GUI_IMAGE, () -> _builder(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, POSITION_TEX_SHADER));
        it.put(SkinRenderFormat.GUI_HIGHLIGHTED_TEXT, () -> _builder(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, POSITION_SHADER).or(builder -> builder.setTexturingState(OR_REVERSE)));

        it.put(SkinRenderFormat.BLOCK, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_SOLID_SHADER));
        it.put(SkinRenderFormat.BLOCK_CUTOUT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_CUTOUT_SHADER));

        it.put(SkinRenderFormat.ENTITY_CUTOUT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_CUTOUT_SHADER));
        it.put(SkinRenderFormat.ENTITY_CUTOUT_NO_CULL, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER));
        it.put(SkinRenderFormat.ENTITY_TRANSLUCENT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER));

        it.put(SkinRenderFormat.SKIN_FACE_SOLID, () -> _builder(SKIN_NORMAL_FORMAT, VertexFormat.Mode.QUADS, () -> AbstractShaders.SKIN_SOLID_SHADER).overlay().lightmap());
        it.put(SkinRenderFormat.SKIN_FACE_TRANSLUCENT, () -> _builder(SKIN_NORMAL_FORMAT, VertexFormat.Mode.QUADS, () -> AbstractShaders.SKIN_TRANSLUCENT_SHADER).overlay().lightmap());
        it.put(SkinRenderFormat.SKIN_FACE_LIGHTING, () -> _builder(SKIN_NORMAL_FORMAT, VertexFormat.Mode.QUADS, () -> AbstractShaders.SKIN_LIGHTING_SOLID_SHADER).overlay().lightmap());
        it.put(SkinRenderFormat.SKIN_FACE_LIGHTING_TRANSLUCENT, () -> _builder(SKIN_NORMAL_FORMAT, VertexFormat.Mode.QUADS, () -> AbstractShaders.SKIN_LIGHTING_TRANSLUCENT_SHADER).overlay().lightmap());
    });

    public AbstractRenderType(String name, RenderType delegate, boolean affectsCrumbling, boolean sortUpload, Runnable setupRenderState, Runnable clearRenderState) {
        super(name, delegate.format(), delegate.mode(), delegate.bufferSize(), affectsCrumbling, sortUpload, () -> {
            delegate.setupRenderState();
            setupRenderState.run();
        }, () -> {
            clearRenderState.run();
            delegate.clearRenderState();
        });
    }

    public static IRenderTypeBuilder builder(SkinRenderFormat format) {
        Supplier<IRenderTypeBuilder> provider = MAPPER.get(format);
        if (provider != null) {
            return provider.get();
        }
        throw new RuntimeException("can't supported render mode");
    }

    private static Builder _builder(VertexFormat format, VertexFormat.Mode mode, ShaderStateShard shader) {
        Builder builder = new Builder(format, mode);
        builder.stateBuilder.setShaderState(shader);
        return builder;
    }

    private static Builder _builder(VertexFormat format, VertexFormat.Mode mode, Supplier<ShaderInstance> shaderProvider) {
        return _builder(format, mode, new ShaderStateShard(shaderProvider));
    }

    private static <T, U> HashMap<T, U> _make(Consumer<HashMap<T, U>> consumer) {
        HashMap<T, U> map = new HashMap<>();
        consumer.accept(map);
        return map;
    }

    public static class Builder implements IRenderTypeBuilder {

        private static final Map<Texturing, TexturingStateShard> TABLE_TEXTURING = _make(it -> {
//            it.put(Texturing.ENTITY_COLOR_OFFSET, new TexturingStateShard("aw_offset_texturing", RenderSystem::setupColorOffsetState, RenderSystem::clearColorOffsetState));
        });

        private static final Map<Target, OutputStateShard> TABLE_OUTPUT = _make(it -> {
            it.put(Target.TRANSLUCENT, TRANSLUCENT_TARGET);
            it.put(Target.MAIN, MAIN_TARGET);
        });

        private static final Map<Transparency, TransparencyStateShard> TABLE_TRANSPARENCY = _make(it -> {
            it.put(Transparency.TRANSLUCENT, TRANSLUCENT_TRANSPARENCY);
            it.put(Transparency.NONE, NO_TRANSPARENCY);
        });

        private static final Map<WriteMask, WriteMaskStateShard> TABLE_WRITE_MASK = _make(it -> {
            it.put(WriteMask.COLOR_DEPTH_WRITE, COLOR_DEPTH_WRITE);
            it.put(WriteMask.COLOR_WRITE, COLOR_WRITE);
            it.put(WriteMask.DEPTH_WRITE, DEPTH_WRITE);
        });

        private static final Map<DepthTest, DepthTestStateShard> TABLE_DEPTH_TEST = _make(it -> {
            it.put(DepthTest.NONE, NO_DEPTH_TEST);
            it.put(DepthTest.EQUAL, EQUAL_DEPTH_TEST);
            it.put(DepthTest.LESS_EQUAL, LEQUAL_DEPTH_TEST);
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
        public IRenderTypeBuilder texture(ResourceLocation texture, boolean blur, boolean mipmap) {
            this.stateBuilder = stateBuilder.setTextureState(new TextureStateShard(texture, blur, mipmap));
            return this;
        }

        @Override
        public IRenderTypeBuilder texturing(Texturing texturing) {
            this.stateBuilder = stateBuilder.setTexturingState(TABLE_TEXTURING.getOrDefault(texturing, DEFAULT_TEXTURING));
            return this;
        }

        @Override
        public IRenderTypeBuilder target(Target target) {
            this.stateBuilder = stateBuilder.setOutputState(TABLE_OUTPUT.getOrDefault(target, MAIN_TARGET));
            return this;
        }

        @Override
        public IRenderTypeBuilder transparency(Transparency transparency) {
            this.stateBuilder = stateBuilder.setTransparencyState(TABLE_TRANSPARENCY.getOrDefault(transparency, NO_TRANSPARENCY));
            return this;
        }

        @Override
        public IRenderTypeBuilder writeMask(WriteMask mask) {
            this.stateBuilder = stateBuilder.setWriteMaskState(TABLE_WRITE_MASK.getOrDefault(mask, COLOR_DEPTH_WRITE));
            return this;
        }

        @Override
        public IRenderTypeBuilder depthTest(DepthTest test) {
            this.stateBuilder = stateBuilder.setDepthTestState(TABLE_DEPTH_TEST.getOrDefault(test, NO_DEPTH_TEST));
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
        public RenderType build(String name) {
            return RenderType.create(name, format, mode, 256, affectsCrumbling, sortOnUpload, stateBuilder.createCompositeState(isOutline));
        }

        public Builder or(Function<CompositeState.CompositeStateBuilder, CompositeState.CompositeStateBuilder> builder) {
            this.stateBuilder = builder.apply(stateBuilder);
            return this;
        }
    }
}
