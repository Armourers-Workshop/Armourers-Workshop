package moe.plushie.armourers_workshop.core.armature;

import java.util.HashMap;

public class Armature2 {

    private int size = 0;
    private final HashMap<String, Joint2> joints = new HashMap<>();

    public Joint2 getJoint(String name) {
        Joint2 joint = joints.get(name);
        if (joint != null) {
            return joint;
        }
        joint = new Joint2(name);
        joint.setId(size);
        joints.put(name, joint);
        size += 1;
        return joint;
    }

    public int size() {
        return size;
    }
}
