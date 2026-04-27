package moe.plushie.armourers_workshop.compat.client.renderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IGraphicsRenderable;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;

@Available("[16, 26)")
@OnlyIn(Dist.CLIENT)
public abstract class AbstractRenderPipelineImpl implements IBufferSource {

    protected IRenderType owner;
    protected boolean started = false;

    public void submit(IPoseStack poseStack, IBufferSource bufferSource, IGraphicsRenderable renderable) {
        // we must start batch pipeline in first submit rendering task.
        if (!started) {
            startBatch(bufferSource);
        }
        // submit a task into pipeline.
        var builder = getBuffer(renderable.renderType());
        renderable.render(poseStack.last(), builder);
    }

    public void startBatch(IBufferSource bufferSource) {
        // we still need to add a placeholder block to the vertex builder,
        // otherwise RenderType.clearRenderState maybe ignore of the empty vertexes.
        var builder = bufferSource.getBuffer(owner);
        for (var i = 0; i < 4; ++i) {
            builder.vertex(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
        started = true;
    }

    @Override
    public void endBatch() {
        // mark the pipeline is ended.
        started = false;
    }
}
