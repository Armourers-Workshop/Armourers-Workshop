package moe.plushie.armourers_workshop.core.client.render.model;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.renderer.model.AbstractHumanoidArmourModel;
import moe.plushie.armourers_workshop.core.client.render.state.MannequinRenderState;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;

@OnlyIn(Dist.CLIENT)
public class MannequinArmorModel extends AbstractHumanoidArmourModel<MannequinEntity, MannequinRenderState> {

    public MannequinArmorModel(Context context) {
        super(context);
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
    }
}
