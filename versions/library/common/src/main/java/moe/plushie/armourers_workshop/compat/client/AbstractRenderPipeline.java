package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IRenderAttachable;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinBufferBuilder;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.function.BiConsumer;

@Available("[1.16, 1.22)")
@OnlyIn(Dist.CLIENT)
public class AbstractRenderPipeline {

    private static final IdentityHashMap<Object, AttachedBufferSource> ATTACHED_BUFFER_SOURCES = new IdentityHashMap<>();

    public static void submit(IPoseStack poseStack, IBufferSource bufferSource, IRenderType renderType, BiConsumer<IPoseStack.Pose, IVertexConsumer> consumer) {
        var bufferSource1 = createAttachedBufferSource(renderType, bufferSource);
        if (bufferSource1 != null) {
            bufferSource = bufferSource1;
        }
        consumer.accept(poseStack.last(), bufferSource.getBuffer(renderType));
    }

    private static IBufferSource createAttachedBufferSource(IRenderType renderType, IBufferSource bufferSource) {
        var attachmentType = AbstractRenderAttachment.find(renderType.group());
        if (attachmentType == null) {
            return null;
        }
        var key = attachmentType.get();
        var bufferSource1 = ATTACHED_BUFFER_SOURCES.get(key);
        if (bufferSource1 != null) {
            return bufferSource1;
        }
        if (key instanceof IRenderAttachable attachable) {
            var bufferSource2 = new AttachedBufferSource(attachmentType);
            attachable.attachRenderTask(() -> {
                bufferSource2.startBatch(bufferSource);
                return () -> {
                    bufferSource2.endBatch();
                    ATTACHED_BUFFER_SOURCES.remove(key);
                };
            });
            ATTACHED_BUFFER_SOURCES.put(key, bufferSource2);
            return bufferSource2;
        }
        return null;
    }

    protected static class AttachedBufferSource implements IBufferSource {

        private final IRenderType ownerType;
        private final HashMap<IRenderType, SkinBufferBuilder> startedBuilders = new HashMap<>();

        public AttachedBufferSource(IRenderType ownerType) {
            this.ownerType = ownerType;
        }

        public void startBatch(IBufferSource bufferSource) {
            // we still need to add a placeholder block to the vertex builder,
            // otherwise RenderType.clearRenderState maybe ignore of the empty vertexes.
            var builder = bufferSource.getBuffer(ownerType);
            for (var i = 0; i < 4; ++i) {
                builder.vertex(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            }
        }

        @Override
        public IVertexConsumer getBuffer(IRenderType renderType) {
            var bufferBuilder = startedBuilders.get(renderType);
            if (bufferBuilder != null) {
                return bufferBuilder;
            }
            bufferBuilder = new SkinBufferBuilder(renderType.bufferSize());
            bufferBuilder.begin(renderType);
            startedBuilders.put(renderType, bufferBuilder);
            return bufferBuilder;
        }

        @Override
        public void endBatch() {
            startedBuilders.forEach(this::upload);
            startedBuilders.clear();
        }

        protected void upload(IRenderType renderType, SkinBufferBuilder builder) {
            builder.setupRenderState();
            AbstractBufferBuilder.upload(renderType, builder);
            builder.clearRenderState();
        }
    }
}
