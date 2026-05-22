package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.core.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.core.math.IRectangle3i;
import moe.plushie.armourers_workshop.api.core.math.IVector3f;
import moe.plushie.armourers_workshop.api.core.math.IVoxelShape;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

@SuppressWarnings("unused")
public class OpenVoxelShape implements IVoxelShape, Iterable<OpenVector4f> {

    private OpenAxisAlignedBoundingBox aabb;
    private OpenRectangle3f box;
    private List<OpenVector4f> vertexes;

    public OpenVoxelShape() {
    }

    public static OpenVoxelShape empty() {
        return new OpenVoxelShape();
    }


    public static OpenVoxelShape box(IRectangle3f bounds) {
        if (bounds instanceof OpenRectangle3f rect) {
            return box(rect);
        }
        return box(new OpenRectangle3f(bounds));
    }

    public static OpenVoxelShape box(IRectangle3i bounds) {
        return box(new OpenRectangle3f(bounds));
    }

    public static OpenVoxelShape box(OpenRectangle3f bounds) {
        var shape = new OpenVoxelShape();
        shape.box = bounds;
        return shape;
    }

    public static OpenVoxelShape box(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return box(new OpenRectangle3f(minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ));
    }

    public static OpenVoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return box(new OpenRectangle3f(minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ));
    }

    @Override
    public void visit(LineConsumer consumer) {
        var box = bounds();

        var minX = box.minX();
        var minY = box.minY();
        var minZ = box.minZ();
        var maxX = box.maxX();
        var maxY = box.maxY();
        var maxZ = box.maxZ();

        consumer.accept(minX, minY, minZ, maxX, minY, minZ);
        consumer.accept(maxX, minY, minZ, maxX, minY, maxZ);
        consumer.accept(maxX, minY, maxZ, minX, minY, maxZ);
        consumer.accept(minX, minY, maxZ, minX, minY, minZ);

        consumer.accept(minX, maxY, minZ, maxX, maxY, minZ);
        consumer.accept(maxX, maxY, minZ, maxX, maxY, maxZ);
        consumer.accept(maxX, maxY, maxZ, minX, maxY, maxZ);
        consumer.accept(minX, maxY, maxZ, minX, maxY, minZ);

        consumer.accept(minX, minY, minZ, minX, maxY, minZ);
        consumer.accept(maxX, minY, minZ, maxX, maxY, minZ);
        consumer.accept(maxX, minY, maxZ, maxX, maxY, maxZ);
        consumer.accept(minX, minY, maxZ, minX, maxY, maxZ);
    }

    public OpenAxisAlignedBoundingBox aabb() {
        if (aabb != null) {
            return aabb;
        }
        aabb = new OpenAxisAlignedBoundingBox(bounds());
        return aabb;
    }

    @Override
    public OpenRectangle3f bounds() {
        if (box != null) {
            return box;
        }
        if (vertexes == null || vertexes.isEmpty()) {
            return OpenRectangle3f.ZERO;
        }
        var iterator = vertexes.iterator();
        var fp = iterator.next();
        float minX = fp.x(), minY = fp.y(), minZ = fp.z();
        float maxX = fp.x(), maxY = fp.y(), maxZ = fp.z();
        while (iterator.hasNext()) {
            var point = iterator.next();
            minX = Math.min(minX, point.x());
            minY = Math.min(minY, point.y());
            minZ = Math.min(minZ, point.z());
            maxX = Math.max(maxX, point.x());
            maxY = Math.max(maxY, point.y());
            maxZ = Math.max(maxZ, point.z());
        }
        box = new OpenRectangle3f(minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ);
        return box;
    }

    public void mul(IMatrix4f matrix) {
        for (var vector : vertexes()) {
            vector.transform(matrix);
        }
        box = null;
        aabb = null;
    }

    public void add(float x, float y, float z, float width, float height, float depth) {
        var list = vertexes();
        list.add(new OpenVector4f(x, y, z, 1.0f));
        list.add(new OpenVector4f(x + width, y, z, 1.0f));
        list.add(new OpenVector4f(x + width, y + height, z, 1.0f));
        list.add(new OpenVector4f(x, y + height, z, 1.0f));
        list.add(new OpenVector4f(x, y, z + depth, 1.0f));
        list.add(new OpenVector4f(x + width, y, z + depth, 1.0f));
        list.add(new OpenVector4f(x + width, y + height, z + depth, 1.0f));
        list.add(new OpenVector4f(x, y + height, z + depth, 1.0f));
        box = null;
        aabb = null;
    }

    public void add(OpenVoxelShape shape1) {
        var list = vertexes();
        list.addAll(shape1.vertexes());
        box = null;
    }

    public void add(IRectangle3f rect) {
        add(rect.x(), rect.y(), rect.z(), rect.width(), rect.height(), rect.depth());
    }

    public void add(float x, float y, float z) {
        add(new OpenVector4f(x, y, z, 1.0f));
    }

    public void add(IVector3f vertex) {
        add(new OpenVector4f(vertex.x(), vertex.y(), vertex.z(), 1.0f));
    }

    public void add(OpenVector4f vertex) {
        var list = vertexes();
        list.add(vertex);
        box = null;
    }

    public boolean isEmpty() {
        return vertexes == null && box == null;
    }

    public void optimize() {
        if (vertexes == null || vertexes.size() <= 8) {
            return;
        }
        var list = vertexes();
        var uniquesVertexes = new LinkedHashSet<OpenVector4f>(list.size());
        uniquesVertexes.addAll(list);
        vertexes = Collections.newList(uniquesVertexes);
    }

    public OpenVoxelShape copy() {
        var shape = new OpenVoxelShape();
        shape.box = box;
        shape.aabb = aabb;
        if (vertexes != null) {
            var newVertexes = new ArrayList<OpenVector4f>();
            newVertexes.ensureCapacity(vertexes.size());
            for (var vector : vertexes) {
                newVertexes.add(vector.copy());
            }
            shape.vertexes = newVertexes;
        }
        return shape;
    }

    @Override
    public Iterator<OpenVector4f> iterator() {
        if (vertexes != null) {
            return vertexes.iterator();
        }
        return vertexesFromBox(box).iterator();
    }

    private List<OpenVector4f> vertexes() {
        if (vertexes == null) {
            vertexes = vertexesFromBox(box);
        }
        return vertexes;
    }

    private List<OpenVector4f> vertexesFromBox(IRectangle3f box) {
        if (box == null) {
            return Collections.newList();
        }
        return Collections.newList(
                new OpenVector4f(box.minX(), box.minY(), box.minZ(), 1.0f),
                new OpenVector4f(box.maxX(), box.minY(), box.minZ(), 1.0f),
                new OpenVector4f(box.maxX(), box.maxY(), box.minZ(), 1.0f),
                new OpenVector4f(box.minX(), box.maxY(), box.minZ(), 1.0f),
                new OpenVector4f(box.minX(), box.minY(), box.maxZ(), 1.0f),
                new OpenVector4f(box.maxX(), box.minY(), box.maxZ(), 1.0f),
                new OpenVector4f(box.maxX(), box.maxY(), box.maxZ(), 1.0f),
                new OpenVector4f(box.minX(), box.maxY(), box.maxZ(), 1.0f)
        );
    }
}
