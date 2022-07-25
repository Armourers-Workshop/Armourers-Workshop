package moe.plushie.armourers_workshop.core.skin.face;

import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeData;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import net.minecraft.core.Direction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.function.ToIntFunction;

public class SkinCuller {

    public static ArrayList<SkinCubeFace> cullFaces(SkinCubeData cubeData, Rectangle3i bounds) {
        BitSet[] flags = cullFaceFlags(cubeData, bounds);
        ArrayList<SkinCubeFace> faces = new ArrayList<>();
        for (int i = 0; i < cubeData.getCubeCount(); ++i) {
            BitSet flag = flags[i];
            for (Direction dir : Direction.values()) {
                if (flag.get(dir.get3DDataValue())) {
                    faces.add(cubeData.getCubeFace(i, dir));
                }
            }
        }
        return faces;
    }

    static BitSet[] cullFaceFlags(SkinCubeData cubeData, Rectangle3i bounds) {
        int total = cubeData.getCubeCount();
        BitSet[] flags = new BitSet[total];

        int[][][] indexes = new int[bounds.getDepth()][bounds.getHeight()][bounds.getWidth()];
        for (int i = 0; i < total; i++) {
            int x = cubeData.getCubePosX(i) - bounds.getX();
            int y = cubeData.getCubePosY(i) - bounds.getY();
            int z = cubeData.getCubePosZ(i) - bounds.getZ();
            flags[i] = new BitSet(6);
            indexes[z][y][x] = i + 1;
        }

        HashSet<Vector3i> closedSet = new HashSet<>();
        ArrayDeque<Vector3i> openList = new ArrayDeque<>();
        ToIntFunction<Vector3i> converter = (pos) -> {
            if (pos.getX() >= 0 && pos.getX() < bounds.getWidth()) {
                if (pos.getY() >= 0 && pos.getY() < bounds.getHeight()) {
                    if (pos.getZ() >= 0 && pos.getZ() < bounds.getDepth()) {
                        return indexes[pos.getZ()][pos.getY()][pos.getX()] - 1;
                    }
                }
            }
            return -1;
        };

        Rectangle3i searchArea = new Rectangle3i(-1, -1, -1, bounds.getWidth() + 2, bounds.getHeight() + 2, bounds.getDepth() + 2);
        Vector3i startPos = new Vector3i(-1, -1, -1);
        openList.add(startPos);
        closedSet.add(startPos);

        while (openList.size() > 0) {
            ArrayList<Vector3i> pendingList = new ArrayList<>();
            Vector3i pos = openList.poll();
            for (Direction advance : Direction.values()) {
                Vector3i pos1 = pos.relative(advance, 1);
                int targetIndex = converter.applyAsInt(pos1);
                if (targetIndex == -1) {
                    pendingList.add(pos1);
                    continue;
                }
                boolean isBlank = false;
                ISkinCube targetCube = cubeData.getCube(targetIndex);
                if (targetCube.isGlass()) {
                    pendingList.add(pos1);
                    // when source cube and target cube is linked glass, ignore.
                    int sourceIndex = converter.applyAsInt(pos);
                    if (sourceIndex != -1) {
                        isBlank = cubeData.getCube(sourceIndex).isGlass();
                    }
                }
                // first, when not any rotation of the cube, it's always facing north.
                // then advance direction always opposite to cube facing direction,
                // so theory we should always use `advance.getOpposite()` get cube facing direction.
                // but actually we reset the cube origin in the indexes, which causes the relationship
                // between the forward direction and the facing is changed.
                Direction facing = advance;
                if (advance.getAxis() == Direction.Axis.Z) {
                    facing = advance.getOpposite();
                }
                flags[targetIndex].set(facing.get3DDataValue(), !isBlank);
            }
            for (Vector3i pos1 : pendingList) {
                if (searchArea.contains(pos1) && !closedSet.contains(pos1)) {
                    closedSet.add(pos1);
                    openList.add(pos1);
                }
            }
        }
        return flags;
    }
}
