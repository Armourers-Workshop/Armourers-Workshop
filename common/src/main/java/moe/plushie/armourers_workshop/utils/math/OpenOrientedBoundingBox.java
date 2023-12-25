package moe.plushie.armourers_workshop.utils.math;

/**
 * Representation for an oriented bounding box.
 * Uses a combination of an axis-aligned bounding box and a rotation vector around the centroid of the said
 * axis-aligned bounding box to represent an oriented bounding box.
 */
public class OpenOrientedBoundingBox {

    private final OpenQuaternionf orientation;
    private final OpenBoundingBox boundingBox;

    private Vector3f[] vertices;
    private Vector3f right;
    private Vector3f up;
    private Vector3f forward;

    public OpenOrientedBoundingBox(OpenQuaternionf orientation, OpenBoundingBox boundingBox) {
        this.orientation = orientation;
        this.boundingBox = boundingBox;
    }

    public boolean intersects(OpenBoundingBox box) {
//
//        if (Separated(a.Vertices, b.Vertices, a.Right))
//            return false;
//        if (Separated(a.Vertices, b.Vertices, a.Up))
//            return false;
//        if (Separated(a.Vertices, b.Vertices, a.Forward))
//            return false;
//
//        if (Separated(a.Vertices, b.Vertices, b.Right))
//            return false;
//        if (Separated(a.Vertices, b.Vertices, b.Up))
//            return false;
//        if (Separated(a.Vertices, b.Vertices, b.Forward))
//            return false;
//
//        if (Separated(a.Vertices, b.Vertices, Vector3.Cross(a.Right, b.Right)))
//            return false;
//        if (Separated(a.Vertices, b.Vertices, Vector3.Cross(a.Right, b.Up)))
//            return false;
//        if (Separated(a.Vertices, b.Vertices, Vector3.Cross(a.Right, b.Forward)))
//            return false;
//
//        if (Separated(a.Vertices, b.Vertices, Vector3.Cross(a.Up, b.Right)))
//            return false;
//        if (Separated(a.Vertices, b.Vertices, Vector3.Cross(a.Up, b.Up)))
//            return false;
//        if (Separated(a.Vertices, b.Vertices, Vector3.Cross(a.Up, b.Forward)))
//            return false;
//
//        if (Separated(a.Vertices, b.Vertices, Vector3.Cross(a.Forward, b.Right)))
//            return false;
//        if (Separated(a.Vertices, b.Vertices, Vector3.Cross(a.Forward, b.Up)))
//            return false;
//        if (Separated(a.Vertices, b.Vertices, Vector3.Cross(a.Forward, b.Forward)))
//            return false;
//
        return false;
    }

    public OpenQuaternionf getOrientation() {
        return orientation;
    }

    public OpenBoundingBox getBoundingBox() {
        return boundingBox;
    }


}
