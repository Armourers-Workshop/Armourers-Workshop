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
    private final ISkinPartType[] partTypes;
    private final Map<String, IJoint> namedJoints;
    private final Map<ISkinPartType, IJoint> linkedJoints;
    private final IJoint wildcardJoint;

    private final IJointTransform[] localTransforms;
    private final IJointTransform[] globalTransforms;

    private final JointShape[] shapes;

    public Armature(Map<String, Joint> joints, Map<Joint, IJointTransform> transforms, Map<ISkinPartType, Joint> linkedJoints, @Nullable Joint wildcardJoint, Map<Joint, JointShape> shapes) {
        this.joints = new IJoint[joints.size()];
        this.partTypes = new ISkinPartType[joints.size()];
        this.localTransforms = new IJointTransform[joints.size()];
        this.globalTransforms = new IJointTransform[joints.size()];
        this.namedJoints = new LinkedHashMap<>(joints);
        this.linkedJoints = new LinkedHashMap<>(linkedJoints);
        this.wildcardJoint = wildcardJoint;
        this.shapes = new JointShape[joints.size()];
        int id = 0;
        for (Joint joint : joints.values()) {
            this.joints[id] = joint;
            this.shapes[id] = shapes.get(joint);
            this.localTransforms[id] = transforms.getOrDefault(joint, IJointTransform.NONE);
            this.globalTransforms[id] = calcTransform(joint, transforms);
            joint.setId(id++);
        }
        for (Map.Entry<ISkinPartType, Joint> entry : linkedJoints.entrySet()) {
            Joint joint = entry.getValue();
            partTypes[joint.getId()] = entry.getKey();
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

    @Nullable
    public ISkinPartType getPartType(IJoint joint) {
        return partTypes[joint.getId()];
    }

    public IJointTransform getLocalTransform(int id) {
        return localTransforms[id];
    }

    public IJointTransform getGlobalTransform(int id) {
        return globalTransforms[id];
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

    private IJointTransform calcTransform(Joint joint, Map<Joint, IJointTransform> transforms) {
        IJointTransform childTransform = transforms.getOrDefault(joint, IJointTransform.NONE);
        if (joint.getParent() == null) {
            return childTransform;
        }
        IJointTransform parentTransform = calcTransform(joint.getParent(), transforms);
        // the parent not need the transform, so we just need to keep the child transform.
        if (parentTransform == IJointTransform.NONE) {
            return childTransform;
        }
        // the child not need the transform, so we just need to keep the parent transform.
        if (childTransform == IJointTransform.NONE) {
            return parentTransform;
        }
        // contact parent and child transform.
        return poseStack -> {
            parentTransform.apply(poseStack);
            childTransform.apply(poseStack);
        };
    }
}
