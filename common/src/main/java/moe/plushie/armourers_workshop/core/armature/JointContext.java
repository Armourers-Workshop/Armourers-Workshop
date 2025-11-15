package moe.plushie.armourers_workshop.core.armature;

public class JointContext {

    private final JointPoseProvider poses;

    public JointContext(ArmatureTransformer transformer, ArmatureTransformerContext context) {
        this.poses = new JointPoseProvider(context);
    }

    public JointPoseProvider poses() {
        return poses;
    }
}
