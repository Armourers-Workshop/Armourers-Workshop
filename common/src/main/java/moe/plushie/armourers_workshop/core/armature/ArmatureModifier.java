package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.client.IJoint;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.math.ITransformf;

public abstract class ArmatureModifier {

    public abstract ITransformf apply(IJoint joint, IModel model, ITransformf transform);
}



