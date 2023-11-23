package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.armature.IJoint;

@SuppressWarnings("unused")
public class Joints {

    public static final IJoint BIPPED_HEAD = Armatures.BIPPED.getJoint("Head");

    public static final IJoint BIPPED_CHEST = Armatures.BIPPED.getJoint("Chest");
    public static final IJoint BIPPED_TORSO = Armatures.BIPPED.getJoint("Torso");

    public static final IJoint BIPPED_SKIRT = Armatures.BIPPED.getJoint("Skirt");

    public static final IJoint BIPPED_LEFT_ARM = Armatures.BIPPED.getJoint("Arm_L");
    public static final IJoint BIPPED_LEFT_HAND = Armatures.BIPPED.getJoint("Hand_L");
    public static final IJoint BIPPED_LEFT_ELBOW = Armatures.BIPPED.getJoint("Elbow_L");

    public static final IJoint BIPPED_RIGHT_ARM = Armatures.BIPPED.getJoint("Arm_R");
    public static final IJoint BIPPED_RIGHT_HAND = Armatures.BIPPED.getJoint("Hand_R");
    public static final IJoint BIPPED_RIGHT_ELBOW = Armatures.BIPPED.getJoint("Elbow_R");

    public static final IJoint BIPPED_LEFT_THIGH = Armatures.BIPPED.getJoint("Thigh_L");
    public static final IJoint BIPPED_LEFT_LEG = Armatures.BIPPED.getJoint("Leg_L");
    public static final IJoint BIPPED_LEFT_KNEE = Armatures.BIPPED.getJoint("Knee_L");
    public static final IJoint BIPPED_LEFT_FOOT = Armatures.BIPPED.getJoint("Foot_L");

    public static final IJoint BIPPED_RIGHT_THIGH = Armatures.BIPPED.getJoint("Thigh_R");
    public static final IJoint BIPPED_RIGHT_LEG = Armatures.BIPPED.getJoint("Leg_R");
    public static final IJoint BIPPED_RIGHT_KNEE = Armatures.BIPPED.getJoint("Knee_R");
    public static final IJoint BIPPED_RIGHT_FOOT = Armatures.BIPPED.getJoint("Foot_R");

    public static final IJoint BIPPED_LEFT_WING = Armatures.BIPPED.getJoint("Wing_L");
    public static final IJoint BIPPED_LEFT_PHALANX = Armatures.BIPPED.getJoint("Phalanx_L");

    public static final IJoint BIPPED_RIGHT_WING = Armatures.BIPPED.getJoint("Wing_R");
    public static final IJoint BIPPED_RIGHT_PHALANX = Armatures.BIPPED.getJoint("Phalanx_R");

    public static void init() {
    }
}
