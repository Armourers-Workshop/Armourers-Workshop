package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.api.client.IGraphicsRenderable;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compat.client.AbstractRenderListener;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.init.ModDebugger;

@OnlyIn(Dist.CLIENT)
public class SkinGraphicsContext {

    private static final SkinGraphicsContext INSTANCE = new SkinGraphicsContext();

    private final ConcurrentBufferCompiler compiler = new ConcurrentBufferCompiler();
    private final ConcurrentRenderingPipeline pipeline = new ConcurrentRenderingPipeline();

    private final Channel solidChannel = new Channel("Solid Channel", pipeline::endSolidBatch, AbstractRenderListener.SKIN_ATTACHED_SOLID_SHEET);
    private final Channel outlineChannel = new Channel("Outline Channel", pipeline::endOutlineBatch, AbstractRenderListener.SKIN_ATTACHED_OUTLINE_SHEET);
    private final Channel translucentChannel = new Channel("Translucent Channel", pipeline::endTranslucentBatch, AbstractRenderListener.SKIN_ATTACHED_TRANSLUCENT_SHEET);

    public static SkinGraphicsContext getInstance() {
        return INSTANCE;
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
            if ((outlineColor & 0xff000000) == 0 && pass.isOutline) {
                continue;
            }
            pipeline.submit(pass, lightmap, overlay, outlineColor, renderPriority, pose);
        }
    }

    public void submit(IGraphicsContext context) {
        // debug render with sync vbo.
        if (ModDebugger.withoutAsyncVBO) {
            pipeline.endSolidBatch();
            pipeline.endTranslucentBatch();
            pipeline.endOutlineBatch();
            return;
        }
        // send the solid channel request when have solid tasks.
        if (pipeline.hasSolidTasks()) {
            context.draw(solidChannel);
        }
        // send the translucent channel request when have solid tasks.
        if (pipeline.hasTranslucentTasks()) {
            context.draw(translucentChannel);
        }
        // send the outline channel request when have solid tasks.
        if (pipeline.hasOutlineTasks()) {
            context.draw(outlineChannel);
        }
    }

    public void clear() {
        compiler.clear();
        pipeline.clear();
    }

    /**
     * A concurrent rendering channel, it will call back when the target render type is ready.
     */
    private static class Channel implements IGraphicsElement, IGraphicsRenderable {

        private final String name;
        private final IRenderType renderType;
        private final Runnable callback;

        private Object lastContext;

        protected Channel(String name, Runnable callback, IRenderType renderType) {
            this.name = name;
            this.renderType = renderType;
            this.callback = callback;
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
            AbstractRenderListener.endBatch(renderType, builder, this::callout);
        }

        @Override
        public IRenderType renderType() {
            return renderType;
        }

        @Override
        public String toString() {
            return name;
        }

        private void callout() {
            lastContext = null;
            callback.run();
        }
    }
}

