package moe.plushie.armourers_workshop.core.client.render.model;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compat.client.renderer.model.AbstractHumanoidModel;
import moe.plushie.armourers_workshop.core.client.render.MannequinEntityRenderer;
import moe.plushie.armourers_workshop.core.client.render.state.MannequinRenderState;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import net.minecraft.core.Rotations;

@OnlyIn(Dist.CLIENT)
public class MannequinModel extends AbstractHumanoidModel<MannequinEntity, MannequinRenderState> {

    private Rotations rootPose;

    public MannequinModel(Context context) {
        super(context);
    }

    public static MannequinModel placeholder() {
        return new MannequinModel(getEmptyContext());
    }

    @Override
    public void abi$setupAnim(MannequinRenderState renderState) {
        super.abi$setupAnim(renderState);
        this.head.setRotation(renderState.headPose());
        this.leftArm.setRotation(renderState.leftArmPose());
        this.rightArm.setRotation(renderState.rightArmPose());
        this.leftLeg.setRotation(renderState.leftLegPose());
        this.rightLeg.setRotation(renderState.rightLegPose());
        this.hat.copyFrom(this.head);
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);
        this.rootPose = renderState.bodyPose();
    }

    @Override
    protected void abi$translateAndRotate(IPoseStack poseStack) {
        if (rootPose == null) {
            return;
        }
        var rx = rootPose.x();
        var ry = rootPose.y();
        var rz = rootPose.z();

        // when rendering in the GUI, we don't use body rotation
        // to avoid display entity at weird angles.
        if (MannequinEntityRenderer.enableLimitYRot) {
            ry = 0;
        }

        poseStack.rotate(new OpenQuaternionf(rx, ry, rz, true));
    }
}
