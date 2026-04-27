package moe.plushie.armourers_workshop.compat.client.gui.element;

import com.apple.library.coregraphics.CGPoint;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.gui.element.EntityGuiElement;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.world.entity.Entity;

@Available("[16, 26)")
@OnlyIn(Dist.CLIENT)
public class AbstractEntityGuiElement extends EntityGuiElement implements AbstractEntityGuiElementImpl {

    private final Entity entity;

    private final int lightmap;
    private final int overlay;

    private final float tx;
    private final float ty;
    private final float rx;
    private final float ry;
    private final float scale;

    protected AbstractEntityGuiElement(Entity entity, int lightmap, int overlay, CGPoint origin, float scale, CGPoint focus) {
        this.entity = entity;
        this.lightmap = lightmap;
        this.overlay = overlay;
        this.tx = origin.x();
        this.ty = origin.y();
        this.rx = (float) Math.atan((0 - focus.y) / 40.0f);
        this.ry = (float) Math.atan((0 - focus.x) / 40.0f);
        this.scale = scale;
    }

    public static AbstractEntityGuiElement newInstance(Entity entity, int lightmap, int overlay, CGPoint origin, float scale, CGPoint focus) {
        return new AbstractEntityGuiElement(entity, lightmap, overlay, origin, scale, focus);
    }

    @Override
    public void render(IPoseStack poseStack, IBufferSource bufferSource) {
        apply(entity, rx, ry, () -> RenderSystem.runAsFancy(() -> {
            var quaternion = OpenVector3f.ZP.rotationDegrees(180.0f);
            var quaternion2 = OpenVector3f.XP.rotationDegrees(rx * 20.0f);

            quaternion.multiply(quaternion2);

            poseStack.pushPose();
            poseStack.translate(tx, ty, 50.0f);
            poseStack.multiply(OpenMatrix4f.createScaleMatrix(scale, scale, -scale));
            //poseStack.translate(0, center, 0);
            poseStack.rotate(quaternion);

            renderEntityInInventory(entity, lightmap, overlay, OpenVector3f.ZERO, quaternion2, poseStack, bufferSource);

            poseStack.popPose();
        }));
    }
}
