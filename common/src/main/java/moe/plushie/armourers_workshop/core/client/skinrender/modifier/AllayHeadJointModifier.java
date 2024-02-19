package moe.plushie.armourers_workshop.core.client.skinrender.modifier;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.core.armature.JointModifier;

public class AllayHeadJointModifier extends JointModifier {

    @Override
    public IJointTransform apply(IJoint joint, IModel model, IJointTransform transform) {
        IModelPart modelPart = model.getPart("root");
        if (modelPart == null) {
            return transform;
        }
        IModelPartPose rootPose = modelPart.pose();
        return poseStack -> {
            rootPose.transform(poseStack);
            transform.apply(poseStack);
            poseStack.scale(0.625f, 0.625f, 0.625f);
        };
    }
}
