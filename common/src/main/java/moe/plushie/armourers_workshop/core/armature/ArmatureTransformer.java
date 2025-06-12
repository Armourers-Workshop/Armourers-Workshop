package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;

import java.util.Arrays;
import java.util.Collection;

public class ArmatureTransformer {

    private final Armature armature;
    private final ArmaturePlugin[] plugins;
    private final IJointTransform[] transforms;
    private final ArmatureTransformerContext context;

    public ArmatureTransformer(Armature armature, Collection<ArmaturePlugin> plugins, ArmatureTransformerContext context) {
        this.armature = armature;
        this.plugins = plugins.toArray(new ArmaturePlugin[0]);
        this.transforms = new IJointTransform[armature.size()];
        this.context = context;
        Arrays.fill(transforms, IJointTransform.NONE);
    }

    public void put(IJoint joint, IJointTransform transform) {
        transforms[joint.id()] = transform;
    }

    public Armature armature() {
        return armature;
    }

    public ArmaturePlugin[] plugins() {
        return plugins;
    }

    public IJointTransform[] transforms() {
        return transforms;
    }

    public ArmatureTransformerContext context() {
        return context;
    }
}
