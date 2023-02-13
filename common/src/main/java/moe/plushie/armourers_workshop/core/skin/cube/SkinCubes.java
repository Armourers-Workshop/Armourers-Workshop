package moe.plushie.armourers_workshop.core.skin.cube;

import moe.plushie.armourers_workshop.api.skin.ISkinCubeProvider;
import moe.plushie.armourers_workshop.core.skin.data.SkinUsedCounter;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Vector3i;

public abstract class SkinCubes implements ISkinCubeProvider {

    protected final SkinUsedCounter usedCounter = new SkinUsedCounter();

    public void forEach(ICubeConsumer consumer) {
        int count = getCubeCount();
        for (int i = 0; i < count; ++i) {
            Vector3i pos = getCube(i).getPos();
            consumer.apply(i, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public OpenVoxelShape getRenderShape() {
        OpenVoxelShape shape = OpenVoxelShape.empty();
        int count = getCubeCount();
        if (count == 0) {
            return shape;
        }
        for (int i = 0; i < count; ++i) {
            Vector3i pos = getCube(i).getPos();
            shape.add(pos.getX(), pos.getY(), pos.getZ(), 1, 1, 1);
        }
        shape.optimize();
        return shape;
    }

    public SkinUsedCounter getUsedCounter() {
        return usedCounter;
    }

    @Override
    public abstract SkinCube getCube(int index);

    public interface ICubeConsumer {
        void apply(int i, int x, int y, int z);
    }
}
