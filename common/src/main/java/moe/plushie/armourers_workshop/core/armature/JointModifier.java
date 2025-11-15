package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.armature.IJointTransform;

public abstract class JointModifier {

    public abstract IJointTransform apply(IJointTransform transform, Joint joint, JointContext context);
}



