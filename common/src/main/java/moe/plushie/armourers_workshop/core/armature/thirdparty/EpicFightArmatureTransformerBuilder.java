package moe.plushie.armourers_workshop.core.armature.thirdparty;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerBuilder;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.armature.JointModifier;

import java.util.Collection;

public class EpicFightArmatureTransformerBuilder extends ArmatureTransformerBuilder {

    public EpicFightArmatureTransformerBuilder(IResourceLocation name) {
        super(name);
    }

    @Override
    protected IJointTransform buildTransform(IJoint joint, Collection<JointModifier> modifiers, ArmatureTransformerContext context) {
        var transform = super.buildTransform(joint, modifiers, context);
        return poseStack -> {
            transform.apply(poseStack);
            poseStack.scale(-1, -1, 1);
        };
    }

    @Override
    protected JointModifier buildJointTarget(String name) {
        return new EpicFightJointBinder(name);
    }
}
