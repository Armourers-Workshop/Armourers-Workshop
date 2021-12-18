package moe.plushie.armourers_workshop.core.model.bake;

import moe.plushie.armourers_workshop.core.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.core.api.common.skin.ICube;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.core.skin.cubes.CubeRegistry;
import moe.plushie.armourers_workshop.core.skin.data.SkinCubeData;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import moe.plushie.armourers_workshop.core.skin.type.Rectangle3D;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

public final class SkinBaker {

    public static boolean withinMaxRenderDistance(Entity entity) {
        Vector3d pos = entity.position();
        return withinMaxRenderDistance(pos.x, pos.y, pos.z);
    }

    public static boolean withinMaxRenderDistance(double x, double y, double z) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player.distanceToSqr(new Vector3d(x, y, z)) > SkinConfig.renderDistanceSkin) {
            return false;
        }
        return true;
    }

    public static int[][][] cullFacesOnEquipmentPart(SkinPart skinPart) {
        SkinCubeData cubeData = skinPart.getCubeData();
        cubeData.setupFaceFlags();
        skinPart.getClientSkinPartData().totalCubesInPart = new int[CubeRegistry.INSTANCE.getTotalCubes()];

        Rectangle3D pb = skinPart.getPartBounds();
        int[][][] cubeArray = new int[pb.getWidth()][pb.getHeight()][pb.getDepth()];

        for (int i = 0; i < cubeData.getCubeCount(); i++) {
            int cubeId = cubeData.getCubeId(i);
            byte[] cubeLoc = cubeData.getCubeLocation(i);
            skinPart.getClientSkinPartData().totalCubesInPart[cubeId] += 1;
            int x = cubeLoc[0] - pb.getX();
            int y = cubeLoc[1] - pb.getY();
            int z = cubeLoc[2] - pb.getZ();
            cubeArray[x][y][z] = i + 1;
        }

        ArrayDeque<Vector3i> openList = new ArrayDeque<Vector3i>();
        HashSet<Vector3i> closedSet = new HashSet<Vector3i>();
        Vector3i startCube = new Vector3i(-1, -1, -1);
        openList.add(startCube);
        closedSet.add(startCube);

        while (openList.size() > 0) {
            Vector3i cl = openList.poll();
            ArrayList<Vector3i> foundLocations = checkCubesAroundLocation(cubeData, cl, pb, cubeArray);
            for (int i = 0; i < foundLocations.size(); i++) {
                Vector3i foundLocation = foundLocations.get(i);
                if (!closedSet.contains(foundLocation)) {
                    closedSet.add(foundLocation);
                    if (isCubeInSearchArea(foundLocation, pb)) {
                        openList.add(foundLocation);
                    }
                }
            }
        }
        return cubeArray;
    }

    private static ArrayList<Vector3i> checkCubesAroundLocation(SkinCubeData cubeData, Vector3i cubeLocation, Rectangle3D partBounds, int[][][] cubeArray) {
        ArrayList<Vector3i> openList = new ArrayList<Vector3i>();
        Direction[] dirs = {Direction.DOWN, Direction.UP,
                Direction.SOUTH, Direction.NORTH,
                Direction.WEST, Direction.EAST };


        int index = getIndexForLocation(cubeLocation, partBounds, cubeArray);

        boolean isGlass = false;
        if (index > 0) {
            ICube cube = cubeData.getCube(index - 1);
            isGlass = cube.needsPostRender();
        }

        for (int i = 0; i < dirs.length; i++) {
            Direction dir = dirs[i];
            int x = cubeLocation.getX() + dir.getStepX();
            int y = cubeLocation.getY() + dir.getStepY();
            int z = cubeLocation.getZ() + dir.getStepZ();
            int tarIndex = getIndexForLocation(x, y, z, partBounds, cubeArray);


            //Add new cubes to the open list.
            if (tarIndex < 1) {
                openList.add(new Vector3i(x, y, z));
            } else {
                if (cubeData.getCube(tarIndex - 1).needsPostRender()) {
                    openList.add(new Vector3i(x, y, z));
                }
            }

            //Update the face flags if there is a block at this location.
            if (tarIndex > 0) {
                flagCubeFace(x, y, z, i, partBounds, cubeArray, cubeData, isGlass);
            }
        }
        return openList;
    }

    private static int getIndexForLocation(Vector3i cubeLocation, Rectangle3D partBounds, int[][][] cubeArray) {
        return getIndexForLocation(cubeLocation.getX(), cubeLocation.getY(), cubeLocation.getZ(), partBounds, cubeArray);
    }

    private static int getIndexForLocation(int x, int y, int z, Rectangle3D partBounds, int[][][] cubeArray) {
        if (x >= 0 & x < partBounds.getWidth()) {
            if (y >= 0 & y < partBounds.getHeight()) {
                if (z >= 0 & z < partBounds.getDepth()) {
                    return cubeArray[x][y][z];
                }
            }
        }
        return 0;
    }

    private static void flagCubeFace(int x, int y, int z, int face, Rectangle3D partBounds, int[][][] cubeArray, SkinCubeData cubeData, boolean isGlass) {
        int checkIndex = getIndexForLocation(x, y, z, partBounds, cubeArray);
        if (!isGlass) {
            cubeData.getFaceFlags(checkIndex - 1).set(face, true);
        } else {
            ICube cube = cubeData.getCube(checkIndex - 1);
            cubeData.getFaceFlags(checkIndex - 1).set(face, cube.needsPostRender() != isGlass);
        }
    }

    private static boolean isCubeInSearchArea(Vector3i cubeLocation, Rectangle3D partBounds) {
        if (cubeLocation.getX() > -2 & cubeLocation.getX() < partBounds.getWidth() + 1) {
            if (cubeLocation.getY() > -2 & cubeLocation.getY() < partBounds.getHeight() + 1) {
                if (cubeLocation.getZ() > -2 & cubeLocation.getZ() < partBounds.getDepth() + 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void buildPartDisplayListArray(SkinPart partData, int[][] dyeColour, int[] dyeUseCount, int[][][] cubeArray) {
//        boolean multipassSkinRendering = ClientProxy.useMultipassSkinRendering();
        boolean multipassSkinRendering = false;

        ArrayList<ColouredFace>[] renderLists;

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

        renderLists = new ArrayList[SkinConfig.getNumberOfRenderLayers() * (lodLevels + 1)];

        for (int i = 0; i < renderLists.length; i++) {
            renderLists[i] = new ArrayList<ColouredFace>();
        }

//        float scale = 0.0625F;

        SkinCubeData cubeData = partData.getCubeData();
        Rectangle3D pb = partData.getPartBounds();

        for (int ix = 0; ix < pb.getWidth(); ix++) {
            for (int iy = 0; iy < pb.getHeight(); iy++) {
                for (int iz = 0; iz < pb.getDepth(); iz++) {
                    int i = getIndexForLocation(ix, iy, iz, pb, cubeArray) - 1;
                    if (i != -1) {
                        byte[] loc = cubeData.getCubeLocation(i);
                        byte[] paintType = cubeData.getCubePaintType(i);
                        ICube cube = partData.getCubeData().getCube(i);

                        byte a = (byte) 255;
                        if (cube.needsPostRender()) {
                            a = (byte) 127;
                        }

                        byte[] r = cubeData.getCubeColourR(i);
                        byte[] g = cubeData.getCubeColourG(i);
                        byte[] b = cubeData.getCubeColourB(i);

                        for (int j = 0; j < 6; j++) {
                            IPaintType type = PaintTypeRegistry.getInstance().getPaintTypeFormByte(paintType[j]);
                            if (type.hasAverageColourChannel()) {
                                int index = type.getChannelIndex();
                                dyeUseCount[index]++;
                                dyeColour[0][index] += r[j] & 0xFF;
                                dyeColour[1][index] += g[j] & 0xFF;
                                dyeColour[2][index] += b[j] & 0xFF;
                            }
                        }

                        int listIndex = 0;
                        if (multipassSkinRendering) {
                            if (cube.isGlowing() && !cube.needsPostRender()) {
                                listIndex = 1;
                            }
                            if (cube.needsPostRender() && !cube.isGlowing()) {
                                listIndex = 2;
                            }
                            if (cube.isGlowing() && cube.needsPostRender()) {
                                listIndex = 3;
                            }
                        } else {
                            if (cube.isGlowing()) {
                                listIndex = 1;
                            }
                        }

                        for (int j = 0; j < 6; j++) {
                            if (cubeData.getFaceFlags(i).get(j)) {
                                ColouredFace ver = new ColouredFace(loc[0], loc[1], loc[2], r[j], g[j], b[j], a, paintType[j], (byte) j, (byte) (1));
                                renderLists[listIndex].add(ver);
                            }
                        }

                    }

                    // Create model LODs
                    for (int lod = 1; lod < lodLevels + 1; lod++) {
                        byte lodLevel = (byte) Math.pow(2, lod);
                        if ((ix) % lodLevel == 0 & (iy) % lodLevel == 0 & (iz) % lodLevel == 0) {

                            for (int j = 0; j < 6; j++) {
                                boolean showFace = getAverageFaceFlags(ix, iy, iz, lodLevel, cubeArray, cubeData, pb, j);
                                if (showFace) {
                                    byte[] avegC = getAverageRGBAT(ix, iy, iz, lodLevel, cubeArray, cubeData, pb, j);

                                    ICube cube = CubeRegistry.INSTANCE.getCubeFormId(avegC[5]);

                                    int listIndex = 0;
                                    if (multipassSkinRendering) {
                                        if (cube.isGlowing() && !cube.needsPostRender()) {
                                            listIndex = 1;
                                        }
                                        if (cube.needsPostRender() && !cube.isGlowing()) {
                                            listIndex = 2;
                                        }
                                        if (cube.isGlowing() && cube.needsPostRender()) {
                                            listIndex = 3;
                                        }
                                    } else {
                                        if (cube.isGlowing()) {
                                            listIndex = 1;
                                        }
                                    }
                                    int lodIndex = ((lod) * SkinConfig.getNumberOfRenderLayers()) + listIndex;

                                    ColouredFace ver = new ColouredFace((byte) (ix + pb.getX()), (byte) (iy + pb.getY()), (byte) (iz + pb.getZ()), avegC[0], avegC[1], avegC[2], avegC[3], avegC[4], (byte) j, lodLevel);
                                    renderLists[lodIndex].add(ver);
                                }
                            }
                        }
                    }


                }
            }
        }

        partData.getClientSkinPartData().setVertexLists(renderLists);
    }

    private static boolean getAverageFaceFlags(int x, int y, int z, byte lodLevel, int[][][] cubeArray, SkinCubeData cubeData, Rectangle3D partBounds, int face) {
        for (int ix = 0; ix < lodLevel; ix++) {
            for (int iy = 0; iy < lodLevel; iy++) {
                for (int iz = 0; iz < lodLevel; iz++) {
                    int index = getIndexForLocation(ix + x, iy + y, iz + z, partBounds, cubeArray) - 1;
                    if (index != -1) {
                        if (cubeData.getFaceFlags(index).get(face)) {
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    private static byte[] getAverageRGBAT(int x, int y, int z, byte lodLevel, int[][][] cubeArray, SkinCubeData cubeData, Rectangle3D partBounds, int face) {
        int count = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        int a = 0;
        int[] paintTypes = new int[256];
        int[] cubeTypes = new int[256];
        for (int ix = 0; ix < lodLevel; ix++) {
            for (int iy = 0; iy < lodLevel; iy++) {
                for (int iz = 0; iz < lodLevel; iz++) {
                    int index = getIndexForLocation(ix + x, iy + y, iz + z, partBounds, cubeArray) - 1;
                    if (index != -1) {
                        if (cubeData.getFaceFlags(index).get(face)) {
                            count += 1;
                            r += (cubeData.getCubeColourR(index)[face] & 0xFF);
                            g += (cubeData.getCubeColourG(index)[face] & 0xFF);
                            b += (cubeData.getCubeColourB(index)[face] & 0xFF);
                            if (cubeData.getCube(index).needsPostRender()) {
                                a += 127;
                            } else {
                                a += 255;
                            }
                            paintTypes[cubeData.getCubePaintType(index)[face] & 0xFF] += 1;
                            cubeTypes[cubeData.getCubeId(index) & 0xFF] += 1;
                        }
                    }
                }
            }
        }
        byte[] rgbat = new byte[6];
        if (count != 0) {
            rgbat[0] = (byte) (r / count);
            rgbat[1] = (byte) (g / count);
            rgbat[2] = (byte) (b / count);
            rgbat[3] = (byte) (a / count);

            int commonPaintTypeIndex = 0;
            int mostPaintTypes = 0;
            for (int i = 0; i < paintTypes.length; i++) {
                if (paintTypes[i] > mostPaintTypes) {
                    mostPaintTypes = paintTypes[i];
                    commonPaintTypeIndex = i;
                }
            }
            rgbat[4] = (byte) commonPaintTypeIndex;

            int commonCubeTypesIndex = 0;
            int mostCubeTypes = 0;
            for (int i = 0; i < cubeTypes.length; i++) {
                if (cubeTypes[i] > mostCubeTypes) {
                    mostCubeTypes = cubeTypes[i];
                    commonCubeTypesIndex = i;
                }
            }
            rgbat[5] = (byte) commonCubeTypesIndex;

        }
        return rgbat;
    }
}
