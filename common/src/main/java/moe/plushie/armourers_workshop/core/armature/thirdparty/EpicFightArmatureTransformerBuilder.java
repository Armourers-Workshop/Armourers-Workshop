package moe.plushie.armourers_workshop.core.armature.thirdparty;

import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerBuilder;
import moe.plushie.armourers_workshop.core.armature.Joint;
import moe.plushie.armourers_workshop.core.armature.JointContext;
import moe.plushie.armourers_workshop.core.armature.JointModifier;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

import java.util.Collection;

public class EpicFightArmatureTransformerBuilder extends ArmatureTransformerBuilder {

    public EpicFightArmatureTransformerBuilder(OpenResourceKey name) {
        super(name);
    }

    @Override
    protected IJointTransform buildTransform(Joint joint, Collection<JointModifier> modifiers, JointContext context) {
        var transform = super.buildTransform(joint, modifiers, context);
        return poseStack -> {
            transform.apply(poseStack);
            poseStack.scale(-1, -1, 1);
        };
    }

    @Override
    protected JointModifier buildJointTarget(String name, IODataObject parameters) {
        return new EpicFightJointBinder(name, parameters);
    }
}
