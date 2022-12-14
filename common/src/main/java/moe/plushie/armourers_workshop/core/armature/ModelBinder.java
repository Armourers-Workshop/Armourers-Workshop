package moe.plushie.armourers_workshop.core.armature;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.client.IJoint;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;

public class ModelBinder {

    // offset +24
    public static final ImmutableMap<IJoint, ModelBinder> BIPPED = ImmutableMap.<IJoint, ModelBinder>builder()
            .put(Joints.BIPPED_HEAD, new ModelBinder(0, 24, 0))

            .put(Joints.BIPPED_CHEST, new ModelBinder(0, 24, 0))
            .put(Joints.BIPPED_TORSO, new ModelBinder(0, 18, 0))

            .put(Joints.BIPPED_SKIRT, new ModelBinder(0, 12, 0).rebind("Torso"))

            .put(Joints.BIPPED_LEFT_ARM, new ModelBinder(-5, 22, 0))
            .put(Joints.BIPPED_RIGHT_ARM, new ModelBinder(5, 22, 0))

            .put(Joints.BIPPED_LEFT_HAND, new ModelBinder(-5, 18, 0))
            .put(Joints.BIPPED_RIGHT_HAND, new ModelBinder(5, 18, 0))

            .put(Joints.BIPPED_LEFT_THIGH, new ModelBinder(-2, 12, 0))
            .put(Joints.BIPPED_RIGHT_THIGH, new ModelBinder(2, 12, 0))

            .put(Joints.BIPPED_LEFT_LEG, new ModelBinder(-2, 6, 0))
            .put(Joints.BIPPED_RIGHT_LEG, new ModelBinder(2, 6, 0))

            .put(Joints.BIPPED_LEFT_FOOT, new ModelBinder(-2, 12, 0).rebind("Leg_L"))
            .put(Joints.BIPPED_RIGHT_FOOT, new ModelBinder(2, 12, 0).rebind("Leg_R"))

            .put(Joints.BIPPED_LEFT_PHALANX, new ModelBinder(0, 24, 2).rebind("Chest"))
            .put(Joints.BIPPED_RIGHT_PHALANX, new ModelBinder(0, 24, 2).rebind("Chest"))
//
////			overrides.put("Wing_L", get(mm::getBodyPart));
////        overrides.put("Wing_R", get(mm::getBodyPart));
//
            .build();

    public static final ImmutableMap<IJoint, Rectangle3f> BIPPED_BOXES = ImmutableMap.<IJoint, Rectangle3f>builder()
            .put(Joints.BIPPED_HEAD, new Rectangle3f(-4, -8, -4, 8, 8, 8))

            .put(Joints.BIPPED_CHEST, new Rectangle3f(-4, 0, -2, 8, 6, 4))
            .put(Joints.BIPPED_TORSO, new Rectangle3f(-4, 0, -2, 8, 6, 4))

            .put(Joints.BIPPED_SKIRT, new Rectangle3f(-4, 0, -2, 8, 12, 4))

            .put(Joints.BIPPED_LEFT_ARM, new Rectangle3f(-1, -2, -2, 4, 6, 4))
            .put(Joints.BIPPED_RIGHT_ARM, new Rectangle3f(-3, -2, -2, 4, 6, 4))

            .put(Joints.BIPPED_LEFT_HAND, new Rectangle3f(-1, 0, -2, 4, 6, 4))
            .put(Joints.BIPPED_RIGHT_HAND, new Rectangle3f(-3, 0, -2, 4, 6, 4))

            .put(Joints.BIPPED_LEFT_THIGH, new Rectangle3f(-2, 0, -2, 4, 6, 4))
            .put(Joints.BIPPED_RIGHT_THIGH, new Rectangle3f(-2, 0, -2, 4, 6, 4))

            .put(Joints.BIPPED_LEFT_LEG, new Rectangle3f(-2, 0, -2, 4, 6, 4))
            .put(Joints.BIPPED_RIGHT_LEG, new Rectangle3f(-2, 0, -2, 4, 6, 4))

            .put(Joints.BIPPED_LEFT_FOOT, new Rectangle3f(-2, 8, -2, 4, 4, 4))
            .put(Joints.BIPPED_RIGHT_FOOT, new Rectangle3f(-2, 8, -2, 4, 4, 4))

            .put(Joints.BIPPED_LEFT_PHALANX, new Rectangle3f(0, 0, 0, 4, 12, 1))
            .put(Joints.BIPPED_RIGHT_PHALANX, new Rectangle3f(-4, 0, 0, 4, 12, 1))

            .build();


    public final float x;
    public final float y;
    public final float z;

    public String name;

    protected ModelBinder(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    protected ModelBinder rebind(String name) {
        this.name = name;
        return this;
    }
}
