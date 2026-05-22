package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.utils.Objects;

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


    public static EntityPose lerp(float position, EntityPose a, EntityPose b) {
        if (position <= 0.0f) {
            return a;
        }
        if (position >= 1.0f) {
            return b;
        }
        return of(OpenMath.lerp(position, a.position, b.position), OpenMath.lerp(position, a.rotation, b.rotation));
    }

    public OpenVector3f position() {
        return position;
    }

    public OpenQuaternionf rotation() {
        return rotation;
    }

    public EntityPose withPosition(OpenVector3f position) {
        return new EntityPose(position, rotation);
    }

    public EntityPose withRotation(OpenQuaternionf rotation) {
        return new EntityPose(position, rotation);
    }

    public EntityPose transforming(EntityPose other) {
        if (other == ZERO) {
            return this;
        }
        if (this == ZERO) {
            return other;
        }
        var pos = other.position.copy();
        pos.transform(rotation);
        pos.add(pos);

        var rot = rotation.copy();
        rot.multiply(other.rotation);

        return of(pos, rot);
    }

    public EntityPose copy() {
        return new EntityPose(position, rotation);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EntityPose that)) return false;
        return Objects.equals(position, that.position) && Objects.equals(rotation, that.rotation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, rotation);
    }
}
