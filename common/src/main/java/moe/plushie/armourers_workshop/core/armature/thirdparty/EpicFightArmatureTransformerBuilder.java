package moe.plushie.armourers_workshop.core.armature.thirdparty;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.core.armature.JointModifier;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;

public class EpicFightArmatureTransformerBuilder extends ArmatureTransformerBuilder {

    public EpicFightArmatureTransformerBuilder(ResourceLocation name) {
        super(name);
    }

    @Override
    protected IJointTransform buildTransform(IJoint joint, IModel model, Collection<JointModifier> modifiers) {
        IJointTransform transform = super.buildTransform(joint, model, modifiers);
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
