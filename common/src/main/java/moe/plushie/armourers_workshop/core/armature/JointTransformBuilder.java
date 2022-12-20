package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.client.IJoint;
import moe.plushie.armourers_workshop.api.math.ITransformf;

public class JointTransformBuilder {

    private final int size;
    private final ITransformf[] transforms;

    public JointTransformBuilder(Armature armature) {
        this.size = armature.size();
        this.transforms = new ITransformf[armature.size()];
    }

    public static JointTransformBuilder of(Armature armature) {
        return new JointTransformBuilder(armature);
    }

    public void put(IJoint joint, ITransformf transform) {
        transforms[joint.getId()] = transform;
    }

    public ITransformf[] build() {
        return transforms;
    }
}
