package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Armature {

    private final IJoint[] joints;
    private final SkinPartType[] partTypes;
    private final Map<String, IJoint> namedJoints;
    private final Map<SkinPartType, IJoint> linkedJoints;
    private final IJoint wildcardJoint;

    private final IJointTransform[] localTransforms;
    private final IJointTransform[] globalTransforms;

    private final JointShape[] shapes;

    public Armature(Map<String, Joint> joints, Map<Joint, IJointTransform> transforms, Map<SkinPartType, Joint> linkedJoints, @Nullable Joint wildcardJoint, Map<Joint, JointShape> shapes) {
        this.joints = new IJoint[joints.size()];
        this.partTypes = new SkinPartType[joints.size()];
        this.localTransforms = new IJointTransform[joints.size()];
        this.globalTransforms = new IJointTransform[joints.size()];
        this.namedJoints = new LinkedHashMap<>(joints);
        this.linkedJoints = new LinkedHashMap<>(linkedJoints);
        this.wildcardJoint = wildcardJoint;
        this.shapes = new JointShape[joints.size()];
        int id = 0;
        for (var joint : joints.values()) {
            this.joints[id] = joint;
            this.shapes[id] = shapes.get(joint);
            this.localTransforms[id] = transforms.getOrDefault(joint, IJointTransform.NONE);
            this.globalTransforms[id] = calcTransform(joint, transforms);
            joint.setId(id++);
        }
        for (var entry : linkedJoints.entrySet()) {
            var joint = entry.getValue();
            partTypes[joint.id()] = entry.getKey();
        }
    }

    @Nullable
    public IJoint jointByName(String name) {
        return namedJoints.get(name);
    }

    public IJoint jointById(int id) {
        return joints[id];
    }

    @Nullable
    public IJoint jointByType(SkinPartType partType) {
        // ...
        return linkedJoints.getOrDefault(partType, wildcardJoint);
    }

    @Nullable
    public SkinPartType typeByJoint(IJoint joint) {
        return partTypes[joint.id()];
    }

    public IJointTransform localTransformById(int id) {
        return localTransforms[id];
    }

    public IJointTransform globalTransformById(int id) {
        return globalTransforms[id];
    }

    public JointShape shapeById(int id) {
        return shapes[id];
    }

    public Collection<IJoint> allJoints() {
        return namedJoints.values();
    }

    public int size() {
        return joints.length;
    }

    private IJointTransform calcTransform(Joint joint, Map<Joint, IJointTransform> transforms) {
        var childTransform = transforms.getOrDefault(joint, IJointTransform.NONE);
        if (joint.parent() == null) {
            return childTransform;
        }
        var parentTransform = calcTransform(joint.parent(), transforms);
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
