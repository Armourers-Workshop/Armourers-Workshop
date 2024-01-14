package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Armature {

    private final IJoint[] joints;
    private final IJointTransform[] transforms;
    private final Map<String, IJoint> namedJoints;
    private final Map<ISkinPartType, IJoint> linkedJoints;
    private final IJoint wildcardJoint;

    private final JointShape[] shapes;

    public Armature(Map<String, Joint> joints, Map<Joint, IJointTransform> transforms, Map<ISkinPartType, Joint> linkedJoints, @Nullable Joint wildcardJoint, Map<Joint, JointShape> shapes) {
        this.joints = new IJoint[joints.size()];
        this.transforms = new IJointTransform[joints.size()];
        this.namedJoints = new LinkedHashMap<>(joints);
        this.linkedJoints = new LinkedHashMap<>(linkedJoints);
        this.wildcardJoint = wildcardJoint;
        this.shapes = new JointShape[joints.size()];
        int id = 0;
        for (Joint joint : joints.values()) {
            this.joints[id] = joint;
            this.shapes[id] = shapes.get(joint);
            this.transforms[id] = transforms.getOrDefault(joint, IJointTransform.NONE);
            joint.setId(id++);
        }
    }

    @Nullable
    public IJoint getJoint(String name) {
        return namedJoints.get(name);
    }

    public IJoint getJoint(int id) {
        return joints[id];
    }

    @Nullable
    public IJoint getJoint(ISkinPartType partType) {
        // ...
        return linkedJoints.getOrDefault(partType, wildcardJoint);
    }

    public IJointTransform getTransform(int id) {
        return transforms[id];
    }

    public JointShape getShape(int id) {
        return shapes[id];
    }

    public Collection<IJoint> allJoints() {
        return namedJoints.values();
    }

    public int size() {
        return joints.length;
    }
}
