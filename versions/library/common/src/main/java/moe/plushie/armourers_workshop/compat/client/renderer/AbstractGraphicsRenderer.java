package moe.plushie.armourers_workshop.compat.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.api.client.IGraphicsRenderable;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compat.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compat.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.compat.client.AbstractRenderPipeline;
import moe.plushie.armourers_workshop.core.client.other.OpenGraphicsContext;
import moe.plushie.armourers_workshop.core.client.other.OpenGraphicsRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

@Available("[1.16, 1.22)")
@OnlyIn(Dist.CLIENT)
public class AbstractGraphicsRenderer implements OpenGraphicsRenderer {

    public static final AbstractGraphicsRenderer OUTLINE = new AbstractGraphicsRenderer(new AbstractPoseStack(), AbstractBufferSource.outline());
    public static final AbstractGraphicsRenderer TESSELATOR = new AbstractGraphicsRenderer(new AbstractPoseStack(), AbstractBufferSource.tesselator());

    protected Consumer<Invoker<? super Entity>> call;

    protected final IPoseStack poseStack;
    protected final IBufferSource bufferSource;

    public AbstractGraphicsRenderer(IPoseStack poseStack, IBufferSource bufferSource) {
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
    }

    public static OpenGraphicsContext wrap(PoseStack poseStack, MultiBufferSource bufferSource) {
        return new OpenGraphicsContext(new AbstractGraphicsRenderer(AbstractPoseStack.wrap(poseStack), AbstractBufferSource.wrap(bufferSource)));
    }

    public static OpenGraphicsContext wrap(Entity entity, float f, float g, PoseStack poseStack, MultiBufferSource bufferSource, int i) {
        var renderer = new AbstractGraphicsRenderer(AbstractPoseStack.wrap(poseStack), AbstractBufferSource.wrap(bufferSource));
        renderer.call = (impl) -> impl.accept(entity, f, g, poseStack, bufferSource, i);
        return new OpenGraphicsContext(renderer);
    }

    public static <T extends Entity> Invoker<T> invoke(Invoker<T> call) {
        return call;
    }

    @Override
    public void submit(IGraphicsRenderable renderable) {
        var renderType = renderable.renderType();
        var bufferSource = getBufferSource(renderType);
        AbstractRenderPipeline.submit(poseStack, bufferSource, renderType, renderable::render);
    }

    @Override
    public void submit(AbstractGraphicsRenderable renderable) {
        var poseStack1 = AbstractPoseStack.unwrap(poseStack);
        var bufferSource1 = AbstractBufferSource.unwrap(bufferSource);
        renderable.render(poseStack1, bufferSource1);
    }

    @Override
    public void flush() {
        bufferSource.endBatch();
    }

    @Override
    public IPoseStack poseStack() {
        return poseStack;
    }

    private IBufferSource getBufferSource(IRenderType renderType) {
        if (renderType.isOutline()) {
            return AbstractBufferSource.outline();
        }
        return bufferSource;
    }

    @FunctionalInterface
    public interface Invoker<T extends Entity> extends IGraphicsElement {

        void accept(T entity, float f, float g, PoseStack poseStack, MultiBufferSource bufferSource, int i);

        @Override
        default void prepare(IGraphicsContext context) {
            if (context instanceof OpenGraphicsContext impl && impl.renderer() instanceof AbstractGraphicsRenderer renderer) {
                // noinspection unchecked
                renderer.call.accept((Invoker<Entity>) this);
            }
        }
    }
}
