package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class JointShape {

    private final Rectangle3f rect;

    public JointShape(Vector3f origin, Vector3f size) {
        this.rect = new Rectangle3f(origin.getX(), origin.getY(), origin.getZ(), size.getX(), size.getY(), size.getZ());
    }

    public Rectangle3f bounds() {
        return rect;
    }
}
