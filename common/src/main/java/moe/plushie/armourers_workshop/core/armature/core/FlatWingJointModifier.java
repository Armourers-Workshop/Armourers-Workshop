package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.client.armature.IJoint;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.core.armature.ArmatureModifier;

public class FlatWingJointModifier extends ArmatureModifier {

    @Override
    public ITransformf apply(IJoint joint, IModel model, ITransformf transform) {
        return ITransformf.NONE;
    }
}
