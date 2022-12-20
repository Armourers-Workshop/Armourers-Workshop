package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.math.ITransformf;

public abstract class ArmatureModifier {

    public abstract ITransformf apply(ITransformf transform, IModelHolder<?> model);
}



