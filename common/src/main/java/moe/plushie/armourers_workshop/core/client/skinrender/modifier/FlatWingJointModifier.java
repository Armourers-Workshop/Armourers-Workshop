package moe.plushie.armourers_workshop.core.client.skinrender.modifier;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.core.armature.JointModifier;

public class FlatWingJointModifier extends JointModifier {

    @Override
    public IJointTransform apply(IJoint joint, IModel model, IJointTransform transform) {
        return IJointTransform.NONE;
    }
}
