package moe.plushie.armourers_workshop.core.client.render.element;

import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.api.client.IGraphicsRenderable;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.utils.ObjectPool;
import moe.plushie.armourers_workshop.core.utils.OpenModelPart;

@SuppressWarnings("unsed")
public class ModelPartElement implements IGraphicsElement, IGraphicsRenderable {

    private static final ObjectPool<ModelPartElement> POOL = ObjectPool.create(ModelPartElement::new);

    private int color;

    private int lightmap;
    private int overlay;

    private OpenModelPart part;
    private IRenderType renderType;

    private final OpenPoseStack poseStack = new OpenPoseStack();

    public static ModelPartElement newInstance(OpenModelPart part, int lightmap, int overlay, IRenderType renderType) {
        return newInstance(part, lightmap, overlay, 0xffffffff, renderType);
    }

    public static ModelPartElement newInstance(OpenModelPart part, int lightmap, int overlay, int color, IRenderType renderType) {
        var that = POOL.alloc();
        that.color = color;
        that.lightmap = lightmap;
        that.overlay = overlay;
        that.part = part;
        that.renderType = renderType;
        return that;
    }

    @Override
    public void render(IPoseStack.Pose pose, IVertexConsumer builder) {
        poseStack.last().set(pose);
        part.render(poseStack, builder, lightmap, overlay, color);
    }

    public void setRenderType(IRenderType renderType) {
        this.renderType = renderType;
    }

    @Override
    public IRenderType renderType() {
        return renderType;
    }
}
