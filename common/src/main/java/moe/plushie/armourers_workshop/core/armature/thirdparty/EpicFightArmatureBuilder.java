package moe.plushie.armourers_workshop.core.armature.thirdparty;

import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.core.armature.ArmatureBuilder;
import moe.plushie.armourers_workshop.core.armature.ArmatureModifier;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

public class EpicFightArmatureBuilder extends ArmatureBuilder {

    public EpicFightArmatureBuilder(ResourceLocation name) {
        super(name);
    }

    @Override
    public ITransformf buildTransform(Collection<ArmatureModifier> modifiers, IModelHolder<?> model) {
        ITransformf transform = super.buildTransform(modifiers, model);
        return poseStack -> {
            transform.apply(poseStack);
            poseStack.scale(-1, -1, 1);
        };
    }

    @Override
    public ArmatureModifier getTarget(IDataPackObject object) {
        if (object.type() == IDataPackObject.Type.STRING) {
            return new EpicFightJointBinder(object.stringValue());
        }
        return new EpicFightJointBinder(object.get("target").stringValue());
    }
}
