package moe.plushie.armourers_workshop.core.skin.cube;

import moe.plushie.armourers_workshop.api.skin.ISkinCubeProvider;
import moe.plushie.armourers_workshop.core.skin.data.SkinUsedCounter;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Vector3i;

import java.util.ArrayList;

public class SkinCubes implements ISkinCubeProvider {

    protected int cubeCount = 0;
    protected final ArrayList<SkinCube> cubes = new ArrayList<>();
    protected final SkinUsedCounter usedCounter = new SkinUsedCounter();

    public void ensureCapacity(int size) {
        cubes.ensureCapacity(size);
        cubeCount = size;
        usedCounter.reset();
    }

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
    public int getCubeCount() {
        return cubeCount;
    }

    public void setCube(int index, SkinCube cube) {
        if (index < cubes.size()) {
            cubes.set(index, cube);
        } else {
            cubes.add(cube);
        }
    }

    @Override
    public SkinCube getCube(int index) {
        return cubes.get(index);
    }

    public interface ICubeConsumer {
        void apply(int i, int x, int y, int z);
    }
}
