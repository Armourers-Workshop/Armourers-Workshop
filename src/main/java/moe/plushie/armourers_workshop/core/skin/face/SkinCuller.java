package moe.plushie.armourers_workshop.core.skin.face;

import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeData;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3i;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;

public class SkinCuller {

    public static ArrayList<SkinCubeFace> cullFaces(SkinCubeData cubeData, Rectangle3i bounds) {
        BitSet[] flags = cullFaceFlags(cubeData, bounds);
        ArrayList<SkinCubeFace> faces = new ArrayList<>();
        for (int i = 0; i < cubeData.getCubeCount(); ++i) {
            BitSet flag = flags[i];
            for (Direction dir : Direction.values()) {
                if (flag.get(dir.get3DDataValue())) {
                    SkinCubeFace face = cubeData.getCubeFace(i, dir);
                    faces.add(face);
                }
            }
        }
        return faces;
    }

    static BitSet[] cullFaceFlags(SkinCubeData cubeData, Rectangle3i bounds) {
        BitSet[] flags = new BitSet[cubeData.getCubeCount()];

        int[][][] indexes = new int[bounds.getDepth()][bounds.getHeight()][bounds.getWidth()];
        for (int i = 0; i < cubeData.getCubeCount(); i++) {
            int x = cubeData.getCubePosX(i) - bounds.getX();
            int y = cubeData.getCubePosY(i) - bounds.getY();
            int z = cubeData.getCubePosZ(i) - bounds.getZ();
            flags[i] = new BitSet(6);
            indexes[z][y][x] = i + 1;
        }

        ArrayDeque<Vector3i> openList = new ArrayDeque<>();
        HashSet<Vector3i> closedSet = new HashSet<>();

        Rectangle3i searchArea = new Rectangle3i(-1, -1, -1, bounds.getWidth() + 1, bounds.getHeight() + 1, bounds.getDepth() + 1);
        Vector3i startPos = new Vector3i(-1, -1, -1);
        openList.add(startPos);
        closedSet.add(startPos);

        while (openList.size() > 0) {
            ArrayList<Vector3i> pendingList = new ArrayList<>();
            Vector3i pos = openList.poll();
            for (Direction dir : Direction.values()) {
                Vector3i pos1 = pos.relative(dir, 1);
                int targetIndex = getCubeIndex(pos1, bounds, indexes);
                if (targetIndex == -1) {
                    pendingList.add(pos1);
                    continue;
                }
                boolean isBlank = false;
                ISkinCube targetCube = cubeData.getCube(targetIndex);
                if (targetCube.isGlass()) {
                    pendingList.add(pos1);
                    // when source cube and target cube is linked glass, ignore.
                    int sourceIndex = getCubeIndex(pos, bounds, indexes);
                    if (sourceIndex != -1) {
                        isBlank = cubeData.getCube(targetIndex).isGlass();
                    }
                }
                flags[targetIndex].set(dir.get3DDataValue(), !isBlank);
            }
            for (Vector3i pos1 : pendingList) {
                if (!closedSet.contains(pos1)) {
                    closedSet.add(pos1);
                    if (searchArea.contains(pos1)) {
                        openList.add(pos1);
                    }
                }
            }
        }
        return flags;
    }

    static int getCubeIndex(Vector3i pos, Rectangle3i bounds, int[][][] indexes) {
        if (pos.getX() >= 0 && pos.getX() < bounds.getWidth()) {
            if (pos.getY() >= 0 && pos.getY() < bounds.getHeight()) {
                if (pos.getZ() >= 0 && pos.getZ() < bounds.getDepth()) {
                    return indexes[pos.getZ()][pos.getY()][pos.getX()] - 1;
                }
            }
        }
        return -1;
    }
}
