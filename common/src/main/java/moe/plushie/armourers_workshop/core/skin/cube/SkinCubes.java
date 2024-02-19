package moe.plushie.armourers_workshop.core.skin.cube;

import moe.plushie.armourers_workshop.api.skin.ISkinCubeProvider;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinUsedCounter;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.OpenPoseStack;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

public abstract class SkinCubes implements ISkinCubeProvider {

    protected final OpenPoseStack poseStack = new OpenPoseStack();
    protected final SkinUsedCounter usedCounter = new SkinUsedCounter();

    public void forEach(Consumer<SkinCube> consumer) {
        int count = getCubeTotal();
        for (int i = 0; i < count; ++i) {
            consumer.accept(getCube(i));
        }
    }

    public OpenVoxelShape getShape() {
        OpenVoxelShape shape = OpenVoxelShape.empty();
        int total = getCubeTotal();
        if (total == 0) {
            return shape;
        }
        for (int i = 0; i < total; ++i) {
            SkinCube cube = getCube(i);
            Rectangle3f rect = cube.getShape();
            SkinTransform transform = cube.getTransform();
            if (transform.isIdentity()) {
                shape.add(rect);
                continue;
            }
            poseStack.pushPose();
            OpenVoxelShape shape1 = OpenVoxelShape.box(rect);
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
