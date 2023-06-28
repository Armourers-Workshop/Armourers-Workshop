package moe.plushie.armourers_workshop.utils.math;

@SuppressWarnings("unused")
public class OpenNearPlane {

    private final float fov;
    private final float width;
    private final float height;

    private final Vector3f forwards = new Vector3f(0, 0, 1);
    private final Vector3f up = new Vector3f(0, 1, 0);
    private final Vector3f left = new Vector3f(1, 0, 0);

    public OpenNearPlane(float rx, float ry, float width, float height, float fov) {
        OpenQuaternionf quaternion = OpenQuaternionf.fromYXZ(-ry * ((float) Math.PI / 180), rx * ((float) Math.PI / 180), 0.0f);
        this.forwards.transform(quaternion);
        this.up.transform(quaternion);
        this.left.transform(quaternion);
        this.fov = fov;
        this.width = width;
        this.height = height;
    }

    public Vector3f at(float deltaX, float deltaY, float deltaZ) {
        float d0 = width / height;
        float d1 = (float) Math.tan((fov / 2.0) * (Math.PI / 180));

        float sx = deltaX * deltaZ * d1 * d0;
        float sy = deltaY * deltaZ * d1;
        float sz = deltaZ;

        // (forwards * sz) + (up * sy) - (left * sx)
        float tx = forwards.getX() * sz + up.getX() * sy - left.getX() * sx;
        float ty = forwards.getY() * sz + up.getY() * sy - left.getY() * sx;
        float tz = forwards.getZ() * sz + up.getZ() * sy - left.getZ() * sx;

        return new Vector3f(tx, ty, tz);
    }

    public Vector3f getLookVector() {
        return forwards;
    }

    public Vector3f getUpVector() {
        return up;
    }

    public Vector3f getLeftVector() {
        return left;
    }
}
