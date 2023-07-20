package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.client.armature.IJoint;

import java.util.Collection;
import java.util.HashMap;

public class Armature {

    private int size = 0;
    private final HashMap<String, IJoint> joints = new HashMap<>();

    public IJoint getJoint(String name) {
        IJoint joint = joints.get(name);
        if (joint != null) {
            return joint;
        }
        Joint joint1 = new Joint(name);
        joint1.setId(size);
        joints.put(name, joint1);
        size += 1;
        return joint1;
    }

    public IJoint searchJointByName(String name) {
        return joints.get(name);
    }

    public Collection<IJoint> allJoints() {
        return joints.values();
    }

    public int size() {
        return size;
    }
}


