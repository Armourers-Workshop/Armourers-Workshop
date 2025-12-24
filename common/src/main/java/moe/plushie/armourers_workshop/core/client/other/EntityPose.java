package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;

public class EntityPose {

    public static final EntityPose ZERO = of(OpenVector3f.ZERO, OpenQuaternionf.identity());

    private final OpenVector3f position;

    private final OpenQuaternionf rotation;

    private EntityPose(OpenVector3f position, OpenQuaternionf rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public static EntityPose of(OpenVector3f position, OpenQuaternionf rotation) {
        return new EntityPose(position, rotation);
    }

    public EntityPose withPosition(OpenVector3f position) {
        return new EntityPose(position, rotation);
    }

    public EntityPose withRotation(OpenQuaternionf rotation) {
        return new EntityPose(position, rotation);
    }

    public OpenVector3f position() {
        return position;
    }

    public OpenQuaternionf rotation() {
        return rotation;
    }
}
