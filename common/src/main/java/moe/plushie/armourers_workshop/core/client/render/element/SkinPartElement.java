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
import moe.plushie.armourers_workshop.core.client.shader.Shader;
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
        Dispatcher.INSTANCE.clear();
    }

    @Override
    public void prepare(IGraphicsContext context) {
        // debug render without vbo.
        if (ModDebugger.withoutVBO) {
            submitWithoutVBO(context);
            return;
        }
        // save ctm.
        var ms = AbstractModelViewStack.getInstance();
        var src = context.ctm().last();
        var dest = new OpenPoseStack.Pose();
        // https://web.archive.org/web/20240125142900/http://www.songho.ca/opengl/gl_normaltransform.html
        dest.setProperties(src.properties());
        dest.pose().set(ms.last());
        dest.pose().multiply(src.pose());
        dest.normal().set(src.normal());
        // submit skin vertex into graphics.
        var collector = Dispatcher.INSTANCE.collect();
        submitWithVBO(dest, collector);
        collector.submit(context);
    }

    private void submitWithVBO(OpenPoseStack.Pose pose, Collector collector) {
        collector.draw(part, skin, scheme, lightmap, overlay, pose, false, outlineColor, renderPriority);
        if (shouldRenderOutline()) {
            collector.draw(part, skin, scheme, lightmap, overlay, pose, true, outlineColor, renderPriority);
        }
    }

    private void submitWithoutVBO(IGraphicsContext context) {
        var poseStack = new OpenPoseStack();
        part.quads().forEach((renderType, quads) -> {
            context.draw(LazyPassImpl.create(renderType, (pose, builder) -> {
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
    protected interface LazyPassImpl extends IGraphicsElement, IGraphicsRenderable {

        static LazyPassImpl create(IRenderType renderType, BiConsumer<IPoseStack.Pose, IVertexConsumer> consumer) {
            return new LazyPassImpl() {

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

    protected static class Dispatcher {

        private static final Dispatcher INSTANCE = new Dispatcher();

        private final ConcurrentBufferCompiler compiler = new ConcurrentBufferCompiler();
        private final ConcurrentRenderingPipeline pipeline = new ConcurrentRenderingPipeline();

        public void clear() {
            compiler.clear();
            pipeline.clear();
        }

        public Collector collect() {
            return new Collector(compiler, pipeline);
        }
    }

    protected static class Collector {

        private final ConcurrentBufferCompiler compiler;
        private final ConcurrentRenderingPipeline pipeline;

        private final HashSet<IRenderType> usingTypes = new HashSet<>();

        public Collector(ConcurrentBufferCompiler compiler, ConcurrentRenderingPipeline pipeline) {
            this.compiler = compiler;
            this.pipeline = pipeline;
        }

        public void draw(BakedSkinPart part, BakedSkin skin, SkinPaintScheme scheme, int lightmap, int overlay, OpenPoseStack.Pose pose, boolean isOutline, int outlineColor, float renderPriority) {
            // we need compile the skin part, but not render when part invisible.
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

    protected static class Channel implements IGraphicsElement, IGraphicsRenderable, IRenderAttachment {

        private static final IdentityHashMap<IRenderType, Channel> CHANNELS = new IdentityHashMap<>();

        private final IRenderType renderType;
        private final ConcurrentRenderingPipeline pipeline;

        private final Shader shader = new Shader();

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
            // ..
            shader.setupRenderState();

            // we let the vanilla's rendering system normal call rendering once,
            // and then insert our the rendering content in end stage.
            RenderSystem.setDrawElementsCallback(this::callout);
        }

        @Override
        public void clearRenderState() {
            // the draw callback is completed, clear it.
            RenderSystem.setDrawElementsCallback(null);

            // ..
            shader.clearRenderState();
        }

        protected void callout() {
            lastContext = null;
            pipeline.render(shader, renderType);
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
