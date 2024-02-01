package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.utils.math.Rectangle2f;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.core.Direction;

import java.util.List;
import java.util.Map;

public class JointShape {

    private final Rectangle3f rect;
    private final ITransformf transform;
    private final List<JointShape> children;
    private final Map<Direction, Rectangle2f> uvs;

    public JointShape(Vector3f origin, Vector3f size, float inflate, ITransformf transform, Map<Direction, Rectangle2f> uvs, List<JointShape> children) {
        float x = origin.getX() - inflate;
        float y = origin.getY() - inflate;
        float z = origin.getZ() - inflate;
        float w = size.getX() + inflate * 2;
        float h = size.getY() + inflate * 2;
        float d = size.getZ() + inflate * 2;
        this.rect = new Rectangle3f(x, y, z, w, h, d);
        this.transform = transform;
        this.children = children;
        this.uvs = uvs;
    }

    public Rectangle2f getUV(Direction dir) {
        if (uvs != null) {
            return uvs.get(dir);
        }
        return null;
    }

    public List<JointShape> children() {
        return children;
    }

    public ITransformf transform() {
        return transform;
    }

    public Rectangle3f bounds() {
        return rect;
    }
}
