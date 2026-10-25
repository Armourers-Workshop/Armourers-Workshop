package moe.plushie.armourers_workshop.compat.client.gui.renderer;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuSampler;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.client.renderer.rendertype.*;
import net.minecraft.resources.Identifier;

/** Keeps every texture and blend setting when splitting connected primitives for clipping. */
public final class NativeGuiRenderTypes {
    private static final Map<RenderType, RenderType> CACHE = new ConcurrentHashMap<>();
    private NativeGuiRenderTypes() { }
    public static RenderType independent(RenderType original) {
        PrimitiveTopology t = original.primitiveTopology();
        if (t != PrimitiveTopology.TRIANGLE_FAN && t != PrimitiveTopology.TRIANGLE_STRIP && t != PrimitiveTopology.DEBUG_LINE_STRIP) return original;
        return CACHE.computeIfAbsent(original, NativeGuiRenderTypes::copy);
    }
    private static Object field(Object object, String name) throws ReflectiveOperationException {
        Field f = object.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(object);
    }
    @SuppressWarnings("unchecked")
    private static RenderType copy(RenderType original) {
        try {
            RenderPipeline p = original.pipeline();
            var pc = RenderPipeline.class.getDeclaredConstructors()[0];
            pc.setAccessible(true);
            PrimitiveTopology topology = original.primitiveTopology() == PrimitiveTopology.DEBUG_LINE_STRIP ? PrimitiveTopology.DEBUG_LINES : PrimitiveTopology.TRIANGLES;
            RenderPipeline pipeline = (RenderPipeline) pc.newInstance(
                Identifier.fromNamespaceAndPath("armourers_workshop", "pipeline/clipped/" + p.getLocation().getNamespace() + "/" + p.getLocation().getPath()),
                p.getVertexShader(), p.getFragmentShader(), p.getShaderDefines(), p.getBindGroupLayouts(),
                p.getColorTargetStates(), p.getDepthStencilState(), p.getPolygonMode(), p.isCull(), p.getVertexFormatBindings(), topology, (int) field(p, "sortKey"));
            Object state = field(original, "state");
            var builder = RenderSetup.builder(pipeline);
            for (var entry : ((Map<String, Object>) field(state, "textures")).entrySet()) {
                builder.withTexture(entry.getKey(), (Identifier) field(entry.getValue(), "location"), (Supplier<GpuSampler>) field(entry.getValue(), "sampler"));
            }
            builder.setOutputTarget((OutputTarget) field(state, "outputTarget"));
            builder.setTextureTransform((TextureTransform) field(state, "textureTransform"));
            builder.setLayeringTransform((LayeringTransform) field(state, "layeringTransform"));
            builder.setOutline((RenderSetup.OutlineProperty) field(state, "outlineProperty"));
            if ((boolean) field(state, "useLightmap")) builder.useLightmap();
            if ((boolean) field(state, "useOverlay")) builder.useOverlay();
            if ((boolean) field(state, "affectsCrumbling")) builder.affectsCrumbling();
            if ((boolean) field(state, "sortOnUpload")) builder.sortOnUpload();
            var constructor = RenderType.class.getDeclaredConstructor(String.class, RenderSetup.class);
            constructor.setAccessible(true);
            return constructor.newInstance("aw_clipped/" + original, builder.createRenderSetup());
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot preserve GUI render state while splitting connected primitives", exception);
        }
    }
}
