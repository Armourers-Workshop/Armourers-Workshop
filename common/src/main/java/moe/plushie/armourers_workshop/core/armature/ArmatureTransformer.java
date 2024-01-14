package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;

import java.util.Arrays;
import java.util.Collection;

public class ArmatureTransformer {

    private final Armature armature;
    private final ArmaturePlugin[] plugins;
    private final IJointTransform[] transforms;

    public ArmatureTransformer(Armature armature, Collection<ArmaturePlugin> plugins) {
        this.armature = armature;
        this.plugins = plugins.toArray(new ArmaturePlugin[0]);
        this.transforms = new IJointTransform[armature.size()];
        Arrays.fill(transforms, IJointTransform.NONE);
    }

    public void put(IJoint joint, IJointTransform transform) {
        transforms[joint.getId()] = transform;
    }

    public Armature getArmature() {
        return armature;
    }

    public ArmaturePlugin[] getPlugins() {
        return plugins;
    }

    public IJointTransform[] getTransforms() {
        return transforms;
    }
}
