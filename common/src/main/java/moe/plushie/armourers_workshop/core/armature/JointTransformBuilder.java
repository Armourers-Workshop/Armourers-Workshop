package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;

public class JointTransformBuilder {

    private final int size;
    private final IJointTransform[] transforms;

    public JointTransformBuilder(Armature armature) {
        this.size = armature.size();
        this.transforms = new IJointTransform[armature.size()];
    }

    public static JointTransformBuilder of(Armature armature) {
        return new JointTransformBuilder(armature);
    }

    public void put(IJoint joint, IJointTransform transform) {
        transforms[joint.getId()] = transform;
    }

    public IJointTransform[] build() {
        return transforms;
    }
}
