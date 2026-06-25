package moe.plushie.armourers_workshop.core.client.render.element;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.api.client.IGraphicsRenderable;
import moe.plushie.armourers_workshop.api.client.IRenderAttachment;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compat.client.math.AbstractModelViewStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.ConcurrentBufferCompiler;
import moe.plushie.armourers_workshop.core.client.other.ConcurrentRenderingContext;
import moe.plushie.armourers_workshop.core.client.other.ConcurrentRenderingPipeline;
import moe.plushie.armourers_workshop.core.client.other.SceneBufferBuilder;
import moe.plushie.armourers_workshop.core.client.other.SceneGraphicsContext;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexGroup;
import moe.plushie.armourers_workshop.core.client.texture.SmartTexture;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.ObjectPool;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.RenderSystem;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Optional;
import java.util.function.BiConsumer;

@SuppressWarnings("unsed")
public class SkinPartElement implements IGraphicsElement {

    private static final ObjectPool<SkinPartElement> POOL = ObjectPool.create(SkinPartElement::new);

    private static final ConcurrentBufferCompiler COMPILER = new ConcurrentBufferCompiler();
    private static final ConcurrentRenderingPipeline PIPELINE = new ConcurrentRenderingPipeline();

    private BakedSkinPart part;
    private BakedSkin skin;
    private SkinPaintScheme scheme;

    private int lightmap;
    private int overlay;

    private int outlineColor;
    private float renderPriority;

    public static SkinPartElement newInstance(BakedSkinPart part, BakedSkin skin, SkinPaintScheme scheme, ConcurrentRenderingContext context) {
        var that = POOL.alloc();
        that.part = part;
        that.skin = skin;
        that.scheme = scheme;
        that.lightmap = context.lightmap();
        that.overlay = context.overlay();
        that.outlineColor = context.outlineColor();
        that.renderPriority = context.itemSource().renderPriority();
        return that;
    }

    public static void clearCache() {
        COMPILER.clear();
        PIPELINE.clear();
    }

    @Override
    public void prepare(IGraphicsContext context) {
        // submit skin vertex without vbo when debug render mode.
        if (ModDebugger.withoutVBO) {
            submitWithoutVBO(context);
            return;
        }
        // submit skin vertex without async vbo when debug render mode.
        if (ModDebugger.withoutAsyncVBO) {
            var tesselator = SceneGraphicsContext.tesselator();
            tesselator.ctm().last().set(context.ctm().last());
            submitWithVBO(tesselator);
            tesselator.flush();
            return;
        }
        // submit skin vertex into graphics.
        submitWithVBO(context);
    }

    private void submitWithVBO(IGraphicsContext context) {
        var collector = Collector.newInstance(COMPILER, PIPELINE);
        collector.prepare(context);
        collector.draw(part, skin, scheme, lightmap, overlay, false, outlineColor, renderPriority);
        if (shouldRenderOutline()) {
            collector.draw(part, skin, scheme, lightmap, overlay, true, outlineColor, renderPriority);
        }
        collector.submit(context);
    }

    private void submitWithoutVBO(IGraphicsContext context) {
        var poseStack = new OpenPoseStack();
        part.quads().forEach((renderType, quads) -> {
            context.draw(Lazy.create(renderType, (pose, builder) -> {
                var smartTexture = Optional.ofNullable(SmartTexture.of(renderType));
                smartTexture.ifPresent(ReferenceCounted::retain);
                quads.forEach((transform, faces) -> {
                    poseStack.last().set(pose);
                    transform.apply(poseStack);
                    faces.forEach(face -> face.render(part, scheme, lightmap, overlay, poseStack, builder));
                });
                smartTexture.ifPresent(ReferenceCounted::release);
            }));
        });
    }

    private boolean shouldRenderOutline() {
        return (outlineColor & 0xff000000) != 0;
    }

    /**
     * Lazy the contents rendering.
     */
    private interface Lazy extends IGraphicsElement, IGraphicsRenderable {

        static Lazy create(IRenderType renderType, BiConsumer<IPoseStack.Pose, IVertexConsumer> consumer) {
            return new Lazy() {

                @Override
                public void render(IPoseStack.Pose pose, IVertexConsumer builder) {
                    consumer.accept(pose, builder);
                }

                @Override
                public IRenderType renderType() {
                    return renderType;
                }
            };
        }
    }

    private static class Collector {

        private static final ObjectPool<Collector> POOL = ObjectPool.create(Collector::new);

        private ConcurrentBufferCompiler compiler;
        private ConcurrentRenderingPipeline pipeline;

        private final OpenPoseStack.Pose pose = new OpenPoseStack.Pose();
        private final HashSet<IRenderType> usingTypes = new HashSet<>();

        public static Collector newInstance(ConcurrentBufferCompiler compiler, ConcurrentRenderingPipeline pipeline) {
            var collector = POOL.alloc();
            collector.compiler = compiler;
            collector.pipeline = pipeline;
            return collector;
        }

        public void prepare(IGraphicsContext context) {
            var ms = AbstractModelViewStack.getInstance();
            var src = context.ctm().last();
            // https://web.archive.org/web/20240125142900/http://www.songho.ca/opengl/gl_normaltransform.html
            pose.setProperties(src.properties());
            pose.pose().set(ms.last());
            pose.pose().multiply(src.pose());
            pose.normal().set(src.normal());
            // reset the using types.
            usingTypes.clear();
        }

        public void draw(BakedSkinPart part, BakedSkin skin, SkinPaintScheme scheme, int lightmap, int overlay, boolean isOutline, int outlineColor, float renderPriority) {
            // we need to compile the skin part, but not render when part invisible.
            var group = compiler.compile(part, skin, scheme, isOutline);
            if (group == null || group.isEmpty() || !part.isVisible()) {
                return;
            }
            // append all task into pipeline.
            for (var pass : group.passes()) {
                // skip outline task, when not enable.
                if ((outlineColor & 0xff000000) == 0 && pass.isOutline()) {
                    continue;
                }
                pipeline.submit(pass, lightmap, overlay, outlineColor, renderPriority, pose);
                usingTypes.add(pass.renderType());
            }
        }

        public void submit(IGraphicsContext context) {
            // submit into context.
            for (var renderType : usingTypes) {
                context.draw(Channel.newInstance(renderType, pipeline));
            }
        }
    }

    private static class Channel implements IGraphicsElement, IGraphicsRenderable, IRenderAttachment {

        private static final IdentityHashMap<IRenderType, Channel> CHANNELS = new IdentityHashMap<>();

        private final IRenderType renderType;
        private final ConcurrentRenderingPipeline pipeline;

        private Object lastContext;

        protected Channel(IRenderType renderType, ConcurrentRenderingPipeline pipeline) {
            this.pipeline = pipeline;
            this.renderType = renderType;
        }

        public static Channel newInstance(IRenderType renderType, ConcurrentRenderingPipeline pipeline) {
            var channel = CHANNELS.get(renderType);
            if (channel != null) {
                return channel;
            }
            channel = new Channel(renderType, pipeline);
            CHANNELS.put(renderType, channel);
            return channel;
        }

        @Override
        public void setupRenderState() {
            // open a render transaction of the pipeline.
            pipeline.beginTransaction();

            // we let the vanilla's rendering system normal call rendering once,
            // and then insert our the rendering content in end stage.
            RenderSystem.setDrawElementsCallback(() -> {
                // allow the next render task.
                reset();
                // flush the pipeline pass of the render type.
                pipeline.flush(renderType);
            });
        }

        @Override
        public void clearRenderState() {
            // the draw callback is completed, clear it.
            RenderSystem.setDrawElementsCallback(null);

            // end a render transaction of the device.
            pipeline.endTransaction();
        }

        public void reset() {
            lastContext = null;
        }

        @Override
        public boolean shouldRender(IGraphicsContext context) {
            // we don’t need to attach every time, this is will optimize performance.
            if (lastContext != context) {
                lastContext = context;
                return true;
            }
            return false;
        }

        @Override
        public void render(IPoseStack.Pose pose, IVertexConsumer builder) {
            // try attach the render pipeline into builder buffer.
            if (builder instanceof SceneBufferBuilder builder1 && !builder1.attachments().contains(this)) {
                // we'll use vanilla's rendering system to immediately draw a transparent point,
                // and then we will get this call in `GlStateManager._drawElements`.
                for (var i = 0; i < 4; ++i) {
                    builder.vertex(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                }
                builder1.addAttachment(this);
            }
        }

        @Override
        public IRenderType renderType() {
            return renderType;
        }

        @Override
        public String toString() {
            var group = pipeline.find(renderType);
            var passCount = Objects.flatMap(group, ShaderVertexGroup::passCount, 0);
            var vertexCount = Objects.flatMap(group, ShaderVertexGroup::vertexCount, 0);
            return Objects.toString(this, "type", renderType.name(), "passes", passCount, "vertices", vertexCount);
        }
    }
}
