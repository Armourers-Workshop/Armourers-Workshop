package moe.plushie.armourers_workshop.core.model.bake;

import moe.plushie.armourers_workshop.core.api.common.skin.ICube;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.skin.cubes.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.data.SkinCubeData;
import moe.plushie.armourers_workshop.core.utils.Rectangle3D;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;


@OnlyIn(Dist.CLIENT)
public class PackedCube {

    final Rectangle3D bounds;
    final SkinCubeData cubeData;

    private int[] totals;
    private int[][][] indexes;

    public PackedCube(Rectangle3D bounds, SkinCubeData cubeData) {
        this.bounds = bounds;
        this.cubeData = cubeData;
    }

    public PackedCubeFace getFaces() {
        //
        this.totals = new int[SkinCubes.INSTANCE.getTotalCubes()];
        this.indexes = new int[bounds.getDepth()][bounds.getHeight()][bounds.getWidth()];

        for (int i = 0; i < cubeData.getCubeCount(); i++) {
            int cubeId = cubeData.getCubeId(i);
            int x = cubeData.getCubePosX(i) - bounds.getX();
            int y = cubeData.getCubePosY(i) - bounds.getY();
            int z = cubeData.getCubePosZ(i) - bounds.getZ();
            totals[cubeId] += 1;
            indexes[z][y][x] = i + 1;
        }

        PackedCubeFace packedFace = new PackedCubeFace();
        buildFaceFlags();
        buildFaces(packedFace);

        this.indexes = null;

        return packedFace;
    }


    private void buildFaces(PackedCubeFace packedFace) {
        boolean multipassSkinRendering = SkinConfig.multipassSkinRendering;

        int lodLevels = SkinConfig.maxLodLevels;

        /* LOD Indexs
         *
         * with multipass;
         * 0 = normal
         * 1 = glowing
         * 2 = glass
         * 3 = glass glowing
         *
         * without multipass
         * 0 = normal
         * 1 = glowing
         */

//        for (int i = 0; i < renderLists.length; i++) {
//            renderLists[i] = new ArrayList<ColouredFace>();
//        }

//        float scale = 0.0625F;
        for (int i = 0; i < cubeData.getCubeCount(); ++i) {
//            byte[] loc = cubeData.getCubeLocation(i);
//                byte[] paintType = cubeData.getCubePaintType(i);
//            ICube cube = cubeData.getCube(i);
//
//
//                byte[] r = cubeData.getCubeColourR(i);
//                byte[] g = cubeData.getCubeColourG(i);
//                byte[] b = cubeData.getCubeColourB(i);
//
//                for (int j = 0; j < 6; j++) {
//                    IPaintType type = PaintTypeRegistry.getInstance().getPaintTypeFormByte(paintType[j]);
//                    if (type.hasAverageColourChannel()) {
//                        int index = type.getChannelIndex();
//                        dyeUseCount[index]++;
//                        dyeColour[0][index] += r[j] & 0xFF;
//                        dyeColour[1][index] += g[j] & 0xFF;
//                        dyeColour[2][index] += b[j] & 0xFF;
//                    }
//                }
//
//                int listIndex = 0;
//                if (multipassSkinRendering) {
//                    if (cube.isGlowing() && !cube.needsPostRender()) {
//                        listIndex = 1;
//                    }
//                    if (cube.needsPostRender() && !cube.isGlowing()) {
//                        listIndex = 2;
//                    }
//                    if (cube.isGlowing() && cube.needsPostRender()) {
//                        listIndex = 3;
//                    }
//                } else {
//                    if (cube.isGlowing()) {
//                        listIndex = 1;
//                    }
//                }

            for (Direction dir : Direction.values()) {
                if (!cubeData.getFaceFlags(i).get(dir.get3DDataValue())) {
                    continue;
                }
                ColouredFace face = cubeData.getCubeFace(i, dir);
                packedFace.add(face);
            }
        }

        packedFace.sort(Comparator.comparingInt(f -> f.direction.get3DDataValue()));
    }

    private void buildFaceFlags() {
        ArrayDeque<Vector3i> openList = new ArrayDeque<>();
        HashSet<Vector3i> closedSet = new HashSet<>();

        Vector3i startPos = new Vector3i(-1, -1, -1);
        openList.add(startPos);
        closedSet.add(startPos);

        while (openList.size() > 0) {
            ArrayList<Vector3i> pendingList = new ArrayList<>();

            Vector3i pos = openList.poll();
            for (Direction dir : Direction.values()) {
                Vector3i pos1 = pos.relative(dir, 1);

                // Add new cubes to the open list.
                ICube cube1 = getCube(pos1);
                if (cube1 == null) {
                    pendingList.add(pos1);
                    continue;
                }

                if (cube1.isGlass()) {
                    pendingList.add(pos1);
                }

                // Update the face flags if there is a block at this location.
                setFlag(pos1, dir, !isLinkedGlass(cube1, pos));
            }

            for (Vector3i pos1 : pendingList) {
                if (closedSet.contains(pos1)) {
                    continue;
                }
                closedSet.add(pos1);
                if (inSearchArea(pos1)) {
                    openList.add(pos1);
                }
            }
        }
    }

    private ICube getCube(Vector3i pos) {
        int index = getCubeIndex(pos.getX(), pos.getY(), pos.getZ());
        if (index == -1) {
            return null;
        }
        return cubeData.getCube(index);
    }

    private int getCubeIndex(int x, int y, int z) {
        if (x >= 0 && x < bounds.getWidth()) {
            if (y >= 0 && y < bounds.getHeight()) {
                if (z >= 0 && z < bounds.getDepth()) {
                    return indexes[z][y][x] - 1;
                }
            }
        }
        return -1;
    }

    private boolean isLinkedGlass(ICube cube, Vector3i pos) {
        // When cube and target cube is linked glass, ignore.
        if (cube.isGlass()) {
            ICube targetCube = getCube(pos);
            return targetCube != null && targetCube.isGlass();
        }
        return false;
    }

    private boolean inSearchArea(Vector3i pos) {
        if (pos.getX() > -2 & pos.getX() < bounds.getWidth() + 1) {
            if (pos.getY() > -2 & pos.getY() < bounds.getHeight() + 1) {
                if (pos.getZ() > -2 & pos.getZ() < bounds.getDepth() + 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setFlag(Vector3i pos, Direction dir, boolean flag) {
        int index = getCubeIndex(pos.getX(), pos.getY(), pos.getZ());
        if (index == -1) {
            return;
        }
        cubeData.getFaceFlags(index).set(dir.get3DDataValue(), flag);
    }

    public interface ICubeConsumer {
        void consume(@Nonnull ICube cube, int x, int y, int z);
    }
}
