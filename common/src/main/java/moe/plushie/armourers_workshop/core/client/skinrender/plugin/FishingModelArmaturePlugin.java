package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.compat.client.AbstractCamera;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;

public class FishingModelArmaturePlugin extends ArmaturePlugin {

    public FishingModelArmaturePlugin(ArmatureTransformerContext context) {
    }

    @Override
    public void activate(EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
        var camera = AbstractCamera.getMainCamera();
        var rotation = camera.orientation().eulerAnglesYXZ();
        context.rotateCTM(OpenQuaternionf.fromEulerAnglesYXZ(rotation.y(), 0, 0));
        context.rotateCTM(OpenVector3f.YP.rotationDegrees(180.0f));
        context.translateCTM(0.03125f, 0.1875f, 0); // 0.5, 3, 0

//        int invert = getHoldingArm(owner) == HumanoidArm.RIGHT ? 1 : -1;
//        if (this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && owner == Minecraft.getInstance().player) {
//            float fov = (float)(Integer)this.entityRenderDispatcher.options.fov().get();
//            double viewBobbingScale = (double)960.0F / (double)fov;
//            Vec3 viewVec = this.entityRenderDispatcher.camera.getNearPlane(fov).getPointOnPlane((float)invert * 0.525F, -0.1F).scale(viewBobbingScale).yRot(swing * 0.5F).xRot(-swing * 0.7F);
//            return owner.getEyePosition(partialTicks).add(viewVec);
//        } else {
//            float ownerYRot = Mth.lerp(partialTicks, owner.yBodyRotO, owner.yBodyRot) * ((float)Math.PI / 180F);
//            double sin = (double)Mth.sin((double)ownerYRot);
//            double cos = (double)Mth.cos((double)ownerYRot);
//            float playerScale = owner.getScale();
//            double rightOffset = (double)invert * 0.35 * (double)playerScale;
//            double forwardOffset = 0.8 * (double)playerScale;
//            float yOffset = owner.isCrouching() ? -0.1875F : 0.0F;
//            return owner.getEyePosition(partialTicks).add(-cos * rightOffset - sin * forwardOffset, (double)yOffset - 0.45 * (double)playerScale, -sin * rightOffset + cos * forwardOffset);
//        }
    }
}
