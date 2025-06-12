package moe.plushie.armourers_workshop.core.math;


import java.util.ArrayList;

public class OpenTransformedBoundingBox {

    private final OpenMatrix4f transform;
    private final OpenAxisAlignedBoundingBox boundingBox;

    private OpenAxisAlignedBoundingBox transformedBoundingBox;

    private OpenVector3f size;
    private ArrayList<OpenVector3f> vertices;


    public OpenTransformedBoundingBox(OpenMatrix4f transform, OpenAxisAlignedBoundingBox boundingBox) {
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

    public ArrayList<OpenVector3f> vertices() {
        if (vertices == null) {
            vertices = _vertexs(boundingBox);
            for (var v : vertices) {
                v.transform(transform);
            }
        }
        return vertices;
    }


    public boolean intersects(OpenAxisAlignedBoundingBox box) {
//        ArrayList<Vector3f> v1 = getVertices();
//        ArrayList<Vector3f> v2 = box.getVertices();
//
//        return GJK.BodiesIntersect(v2, v1);
        return transformedBoundingBox().intersects(box);
    }

    //    public boolean intersects(float d, float e, float f, float g, float h, float i) {
//        return getTransformedBoundingBox().intersects(d, e, f, g, h, i);
//    }
//
    public OpenMatrix4f transform() {
        return transform;
    }

    public OpenAxisAlignedBoundingBox boundingBox() {
        return boundingBox;
    }

    public OpenAxisAlignedBoundingBox transformedBoundingBox() {
        if (transformedBoundingBox == null) {
            transformedBoundingBox = boundingBox.transforming(transform);
        }
        return transformedBoundingBox;
    }

    private ArrayList<OpenVector3f> _vertexs(OpenAxisAlignedBoundingBox box) {
        var v = new ArrayList<OpenVector3f>();
        v.add(new OpenVector3f(box.minX(), box.minY(), box.minZ()));
        v.add(new OpenVector3f(box.maxX(), box.minY(), box.minZ()));
        v.add(new OpenVector3f(box.minX(), box.maxY(), box.minZ()));
        v.add(new OpenVector3f(box.maxX(), box.maxY(), box.minZ()));
        v.add(new OpenVector3f(box.minX(), box.minY(), box.maxZ()));
        v.add(new OpenVector3f(box.maxX(), box.minY(), box.maxZ()));
        v.add(new OpenVector3f(box.minX(), box.maxY(), box.maxZ()));
        v.add(new OpenVector3f(box.maxX(), box.maxY(), box.maxZ()));
        return v;
    }
}
