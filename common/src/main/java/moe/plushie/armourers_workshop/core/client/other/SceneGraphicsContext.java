package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.api.client.IGraphicsRenderable;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.core.math.IQuaternionf;
import moe.plushie.armourers_workshop.compat.client.renderer.AbstractGraphicsRenderable;
import moe.plushie.armourers_workshop.compat.client.renderer.AbstractGraphicsRenderer;

@OnlyIn(Dist.CLIENT)
public class SceneGraphicsContext implements IGraphicsContext {

    private static final SceneGraphicsContext OUTLINE = new SceneGraphicsContext(AbstractGraphicsRenderer.OUTLINE);
    private static final SceneGraphicsContext TESSELATOR = new SceneGraphicsContext(AbstractGraphicsRenderer.TESSELATOR);

    protected final IPoseStack poseStack;
    protected final SceneGraphicsRenderer renderer;

    public SceneGraphicsContext(SceneGraphicsRenderer renderer) {
        this.poseStack = renderer.poseStack();
        this.renderer = renderer;
    }

    public static SceneGraphicsContext outline() {
        return OUTLINE;
    }

    public static SceneGraphicsContext tesselator() {
        return TESSELATOR;
    }

    @Override
    public void saveGraphicsState() {
        poseStack.pushPose();
    }

    @Override
    public void restoreGraphicsState() {
        poseStack.popPose();
    }

    @Override
    public void translateCTM(float x, float y, float z) {
        poseStack.translate(x, y, z);
    }

    @Override
    public void scaleCTM(float x, float y, float z) {
        poseStack.scale(x, y, z);
    }

    @Override
    public void rotateCTM(IQuaternionf quaternion) {
        poseStack.rotate(quaternion);
    }

    @Override
    public void concatenateCTM(IPoseStack.Pose pose) {
        poseStack.multiply(pose.pose());
        poseStack.multiply(pose.normal());
    }

    @Override
    public void draw(IGraphicsElement element) {
        element.prepare(this);
        // we should try to call the render for the renderable element.
        if (element instanceof IGraphicsRenderable renderable && renderable.shouldRender(this)) {
            renderer.submit(renderable);
        }
        // we should try to call the render for the special renderable element.
        if (element instanceof AbstractGraphicsRenderable renderable) {
            renderer.submit(renderable);
        }
    }

    public void flush() {
        renderer.flush();
    }

    @Override
    public IPoseStack ctm() {
        return poseStack;
    }

    public SceneGraphicsRenderer renderer() {
        return renderer;
    }
}
