package moe.plushie.armourers_workshop.core.skin.geometry;

import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometrySet;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.core.utils.Objects;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class SkinGeometrySet<T extends SkinGeometry> implements ISkinGeometrySet<T> {

    protected int id = -1;

    public int getId() {
        return id;
    }

    @Override
    public OpenVoxelShape getShape() {
        var poseStack = new OpenPoseStack();
        var combinedShape = new OpenVoxelShape();
        for (var geometry : this) {
            var shape = geometry.getShape();
            var transform = geometry.getTransform();
            if (transform.isIdentity()) {
                combinedShape.add(shape);
                continue;
            }
            poseStack.pushPose();
            var shape1 = shape.copy();
            transform.apply(poseStack);
            shape1.mul(poseStack.last().pose());
            combinedShape.add(shape1);
            poseStack.popPose();
        }
        combinedShape.optimize();
        return combinedShape;
    }

    @Nullable
    @Override
    public Collection<SkinGeometryType> getSupportedTypes() {
        // we don't know the included cube types.
        return null;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "id", getId(), "size", size(), "types", getSupportedTypes());
    }
}
