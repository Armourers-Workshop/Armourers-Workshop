package moe.plushie.armourers_workshop.core.skin.cube;

import moe.plushie.armourers_workshop.api.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeProvider;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinUsedCounter;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import moe.plushie.armourers_workshop.utils.math.OpenAABB;
import moe.plushie.armourers_workshop.utils.math.OpenPoseStack;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class SkinCubes implements ISkinCubeProvider {

    protected final OpenPoseStack poseStack = new OpenPoseStack();
    protected final SkinUsedCounter usedCounter = new SkinUsedCounter();

    public void forEach(ICubeConsumer consumer) {
        int count = getCubeTotal();
        for (int i = 0; i < count; ++i) {
            Vector3i pos = getCube(i).getPosition();
            consumer.apply(i, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public OpenVoxelShape getRenderShape() {
        OpenVoxelShape shape = OpenVoxelShape.empty();
        int total = getCubeTotal();
        if (total == 0) {
            return shape;
        }
        for (int i = 0; i < total; ++i) {
            ISkinCube cube = getCube(i);
            IRectangle3f rect = cube.getShape();
            ITransformf transform = cube.getTransform();
            if (transform.isIdentity()) {
                shape.add(rect);
                continue;
            }
            OpenVoxelShape shape1 = OpenVoxelShape.box(rect);
            TrigUtils.apply(transform, poseStack);
            shape1.mul(poseStack.lastPose());
            shape.add(shape1);
            poseStack.setIdentity();
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

    public interface ICubeConsumer {
        void apply(int i, int x, int y, int z);
    }
}
