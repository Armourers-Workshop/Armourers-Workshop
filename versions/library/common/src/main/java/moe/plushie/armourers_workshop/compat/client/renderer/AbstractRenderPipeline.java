package moe.plushie.armourers_workshop.compat.client.renderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainer;
import moe.plushie.armourers_workshop.compat.client.renderer.vertex.AbstractBufferBuilder;
import moe.plushie.armourers_workshop.core.client.other.SceneBufferBuilder;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.util.HashMap;

@Available("[16, )")
@OnlyIn(Dist.CLIENT)
public class AbstractRenderPipeline extends AbstractRenderPipelineImpl implements IBufferSource {

    private static final DataContainer.Key<AbstractRenderPipeline> KEY = DataContainer.key("RenderAttachment", AbstractRenderPipeline::new);

    private final HashMap<IRenderType, SceneBufferBuilder> startedBuilders = new HashMap<>();

    protected AbstractRenderPipeline(IRenderType owner) {
        this.owner = owner;
    }

    /// Get a render pipeline from the render group.
    public static AbstractRenderPipeline of(IRenderType renderType) {
        var attachmentType = AbstractRenderAttachment.find(renderType.group());
        if (attachmentType == null) {
            return null;
        }
        return DataContainer.of(attachmentType, KEY);
    }

    /// Callback the render pipeline before attached render type will start rendering.
    public static void setupRenderState(Object value) {
        // nop.
    }

    /// Callback the render pipeline after attached render type did end rendering.
    public static void clearRenderState(Object value) {
        // only allow render type.
        if (!(value instanceof IAssociatedContainer container)) {
            return;
        }
        // only callback once.
        var pipeline = container.getAssociatedObject(KEY);
        if (pipeline != null) {
            container.setAssociatedObject(KEY, null);
            pipeline.endBatch();
        }
    }

    protected void upload(IRenderType renderType, SceneBufferBuilder builder) {
        builder.setupRenderState();
        AbstractBufferBuilder.upload(renderType, builder);
        builder.clearRenderState();
    }

    @Override
    public IVertexConsumer getBuffer(IRenderType renderType) {
        var bufferBuilder = startedBuilders.get(renderType);
        if (bufferBuilder != null) {
            return bufferBuilder;
        }
        bufferBuilder = new SceneBufferBuilder(renderType.bufferSize());
        bufferBuilder.begin(renderType);
        startedBuilders.put(renderType, bufferBuilder);
        return bufferBuilder;
    }

    @Override
    public void endBatch() {
        super.endBatch();
        startedBuilders.forEach(this::upload);
        startedBuilders.clear();
    }

    @Override
    public String toString() {
        return Objects.toString(this, "owner", owner.name());
    }
}
