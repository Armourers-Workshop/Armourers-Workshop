package moe.plushie.armourers_workshop.core.skin.cube;

import moe.plushie.armourers_workshop.api.skin.ISkinCubeProvider;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinUsedCounter;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.OpenPoseStack;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

public abstract class SkinCubes implements ISkinCubeProvider {

    protected final OpenPoseStack poseStack = new OpenPoseStack();
    protected final SkinUsedCounter usedCounter = new SkinUsedCounter();

    protected int id = -1;

    public void forEach(Consumer<SkinCube> consumer) {
        var count = getCubeTotal();
        for (var i = 0; i < count; ++i) {
            consumer.accept(getCube(i));
        }
    }

    public OpenVoxelShape getShape() {
        var shape = OpenVoxelShape.empty();
        var total = getCubeTotal();
        if (total == 0) {
            return shape;
        }
        for (var i = 0; i < total; ++i) {
            var cube = getCube(i);
            var rect = cube.getShape();
            var transform = cube.getTransform();
            if (transform.isIdentity()) {
                shape.add(rect);
                continue;
            }
            poseStack.pushPose();
            var shape1 = OpenVoxelShape.box(rect);
            transform.apply(poseStack);
            shape1.mul(poseStack.last().pose());
            poseStack.popPose();
            shape.add(shape1);
        }
        shape.optimize();
        return shape;
    }

    public SkinUsedCounter getUsedCounter() {
        return usedCounter;
    }

    public int getId() {
        return id;
    }

    @Override
    public abstract SkinCube getCube(int index);

    @Nullable
    public Collection<ISkinCubeType> getCubeTypes() {
        // we don't know the included cube types.
        return null;
    }

    @Override
    public String toString() {
        return ObjectUtils.makeDescription(this, "total", getCubeTotal());
    }
}
