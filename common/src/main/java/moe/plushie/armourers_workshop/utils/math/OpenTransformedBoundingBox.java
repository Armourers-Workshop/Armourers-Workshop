package moe.plushie.armourers_workshop.utils.math;


import moe.plushie.armourers_workshop.utils.GJK;
import moe.plushie.armourers_workshop.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class OpenTransformedBoundingBox {

    private final OpenMatrix4f transform;
    private final OpenBoundingBox boundingBox;

    private OpenBoundingBox transformedBoundingBox;

    private Vector3f size;
    private ArrayList<Vector3f> vertices;


    public OpenTransformedBoundingBox(OpenMatrix4f transform, OpenBoundingBox boundingBox) {
        this.transform = transform;
        this.boundingBox = boundingBox;


//        float width = boundingBox.getMaxX() - boundingBox.getMinX();
//        float height = boundingBox.getMaxY() - boundingBox.getMinY();
//        float depth = boundingBox.getMaxZ() - boundingBox.getMinZ();
//
//        size = new Vector3f(width, height, depth);

//        float angle = (float) Math.toRadians(45.0);
//
//        Vector3f A_scale = new Vector3f( 1, 4, 1);
//        Vector3f B_scale = new Vector3f( 4, 4, 4);
//        Vector3f A_rot = new Vector3f( 0, angle, 0);
//        Vector3f B_rot = new Vector3f( 0, angle, 0);
//        Vector3f B_location = new Vector3f(-10,0,0);
//
//        if (!CollisionCheacker.testObOb(player_location.add(motion_direction_x.mul(player_velocity)) , A_scale, A_rot, B_location, B_scale, B_rot))
//        {
//            // can move
//        }

    }

    public ArrayList<Vector3f> getVertices() {
        if (vertices == null) {
            vertices = _vertexs(boundingBox);
            for (Vector3f v : vertices) {
                v.transform(transform);
            }
        }
        return vertices;
    }


    public boolean intersects(OpenBoundingBox box) {
//        ArrayList<Vector3f> v1 = getVertices();
//        ArrayList<Vector3f> v2 = box.getVertices();
//
//        return GJK.BodiesIntersect(v2, v1);
        return getTransformedBoundingBox().intersects(box);
    }

//    public boolean intersects(float d, float e, float f, float g, float h, float i) {
//        return getTransformedBoundingBox().intersects(d, e, f, g, h, i);
//    }
//
    public OpenMatrix4f getTransform() {
        return transform;
    }

    public OpenBoundingBox getBoundingBox() {
        return boundingBox;
    }

    public OpenBoundingBox getTransformedBoundingBox() {
        if (transformedBoundingBox == null) {
            transformedBoundingBox = boundingBox.transforming(transform);
        }
        return transformedBoundingBox;
    }

    private ArrayList<Vector3f> _vertexs(OpenBoundingBox box) {
        ArrayList<Vector3f> v = new ArrayList<>();
        v.add(new Vector3f(box.getMinX(), box.getMinY(), box.getMinZ()));
        v.add(new Vector3f(box.getMaxX(), box.getMinY(), box.getMinZ()));
        v.add(new Vector3f(box.getMinX(), box.getMaxY(), box.getMinZ()));
        v.add(new Vector3f(box.getMaxX(), box.getMaxY(), box.getMinZ()));
        v.add(new Vector3f(box.getMinX(), box.getMinY(), box.getMaxZ()));
        v.add(new Vector3f(box.getMaxX(), box.getMinY(), box.getMaxZ()));
        v.add(new Vector3f(box.getMinX(), box.getMaxY(), box.getMaxZ()));
        v.add(new Vector3f(box.getMaxX(), box.getMaxY(), box.getMaxZ()));
        return v;
    }
}
