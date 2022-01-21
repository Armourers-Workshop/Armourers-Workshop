package moe.plushie.armourers_workshop.core.model.bake;

import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
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

//        public static PackedCubeFace cullFacesOnEquipmentPart(SkinPart skinPart) {
//        SkinCubeData cubeData = skinPart.getCubeData();
//        cubeData.setupFaceFlags();
//
//        PackedCube packedCube = new PackedCube(skinPart.getPartBounds(), cubeData);
//        return packedCube.getFaces();
//
//        skinPart.getClientSkinPartData().totalCubesInPart = new int[CubeRegistry.INSTANCE.getTotalCubes()];
//        int[][][] cubeArray = new int[pb.getWidth()][pb.getHeight()][pb.getDepth()];
//        for (int i = 0; i < cubeData.getCubeCount(); i++) {
//            int cubeId = cubeData.getCubeId(i);
//            byte[] cubeLoc = cubeData.getCubeLocation(i);
//            skinPart.getClientSkinPartData().totalCubesInPart[cubeId] += 1;
//            int x = cubeLoc[0] - pb.getX();
//            int y = cubeLoc[1] - pb.getY();
//            int z = cubeLoc[2] - pb.getZ();
//            cubeArray[x][y][z] = i + 1;
//        }
//        ArrayDeque<Vector3i> openList = new ArrayDeque<Vector3i>();
//        HashSet<Vector3i> closedSet = new HashSet<Vector3i>();
//        Vector3i startCube = new Vector3i(-1, -1, -1);
//        openList.add(startCube);
//        closedSet.add(startCube);
//
//        while (openList.size() > 0) {
//            Vector3i cl = openList.poll();
//            ArrayList<Vector3i> foundLocations = checkCubesAroundLocation(cubeData, cl, pb, cubeArray);
//            for (int i = 0; i < foundLocations.size(); i++) {
//                Vector3i foundLocation = foundLocations.get(i);
//                if (!closedSet.contains(foundLocation)) {
//                    closedSet.add(foundLocation);
//                    if (isCubeInSearchArea(foundLocation, pb)) {
//                        openList.add(foundLocation);
//                    }
//                }
//            }
//        }
//
//        buildPartDisplayListArray(skinPart, null, null, cubeArray);
//        return skinPart.getClientSkinPartData().vertexLists;
//    }


    public static void buildPartDisplayListArray(SkinPart partData, int[][] dyeColour, int[] dyeUseCount, int[][][] cubeArray) {
//        boolean multipassSkinRendering = ClientProxy.useMultipassSkinRendering();
        boolean multipassSkinRendering = false;

        ArrayList<ColouredFace> renderLists;

        int lodLevels = SkinConfig.maxLodLevels;

//        renderLists = new ArrayList<>();//[SkinConfig.getNumberOfRenderLayers() * (lodLevels + 1)];
//        for (int i = 0; i < renderLists.length; i++) {
//            renderLists[i] = new ArrayList<ColouredFace>();
//        }
//
//        float scale = 0.0625F;
//
//        SkinCubeData cubeData = partData.getCubeData();
//        Rectangle3D pb = partData.getPartBounds();
//
//        for (int ix = 0; ix < pb.getWidth(); ix++) {
//            for (int iy = 0; iy < pb.getHeight(); iy++) {
//                for (int iz = 0; iz < pb.getDepth(); iz++) {
//                    int i = getIndexForLocation(ix, iy, iz, pb, cubeArray) - 1;
//                    if (i != -1) {
//                        byte[] loc = cubeData.getCubeLocation(i);
//                        byte[] paintType = cubeData.getCubePaintType(i);
//                        ICube cube = partData.getCubeData().getCube(i);
//
//                        byte a = (byte) 255;
//                        if (cube.needsPostRender()) {
//                            a = (byte) 127;
//                        }
//
//                        byte[] r = cubeData.getCubeColourR(i);
//                        byte[] g = cubeData.getCubeColourG(i);
//                        byte[] b = cubeData.getCubeColourB(i);
//
//                        for (int j = 0; j < 6; j++) {
//                            IPaintType type = PaintTypeRegistry.getInstance().getPaintTypeFormByte(paintType[j]);
//                            if (type.hasAverageColourChannel()) {
//                                int index = type.getChannelIndex();
//                                dyeUseCount[index]++;
//                                dyeColour[0][index] += r[j] & 0xFF;
//                                dyeColour[1][index] += g[j] & 0xFF;
//                                dyeColour[2][index] += b[j] & 0xFF;
//                            }
//                        }
//
//                        int listIndex = 0;
//                        if (multipassSkinRendering) {
//                            if (cube.isGlowing() && !cube.needsPostRender()) {
//                                listIndex = 1;
//                            }
//                            if (cube.needsPostRender() && !cube.isGlowing()) {
//                                listIndex = 2;
//                            }
//                            if (cube.isGlowing() && cube.needsPostRender()) {
//                                listIndex = 3;
//                            }
//                        } else {
//                            if (cube.isGlowing()) {
//                                listIndex = 1;
//                            }
//                        }
//
//                        for (int j = 0; j < 6; j++) {
//                            if (cubeData.getFaceFlags(i).get(j)) {
//                                ColouredFace ver = new ColouredFace(loc[0], loc[1], loc[2], r[j], g[j], b[j], a, paintType[j], (byte) j, (byte) (1));
//                                renderLists.add(ver);
//                            }
//                        }
//
//                    }
//
//                    // Create model LODs
//                    for (int lod = 1; lod < lodLevels + 1; lod++) {
//                        byte lodLevel = (byte) Math.pow(2, lod);
//                        if ((ix) % lodLevel == 0 & (iy) % lodLevel == 0 & (iz) % lodLevel == 0) {
//
//                            for (int j = 0; j < 6; j++) {
//                                boolean showFace = getAverageFaceFlags(ix, iy, iz, lodLevel, cubeArray, cubeData, pb, j);
//                                if (showFace) {
//                                    byte[] avegC = getAverageRGBAT(ix, iy, iz, lodLevel, cubeArray, cubeData, pb, j);
//
//                                    ICube cube = CubeRegistry.INSTANCE.getCubeFormId(avegC[5]);
//
//                                    int listIndex = 0;
//                                    if (multipassSkinRendering) {
//                                        if (cube.isGlowing() && !cube.needsPostRender()) {
//                                            listIndex = 1;
//                                        }
//                                        if (cube.needsPostRender() && !cube.isGlowing()) {
//                                            listIndex = 2;
//                                        }
//                                        if (cube.isGlowing() && cube.needsPostRender()) {
//                                            listIndex = 3;
//                                        }
//                                    } else {
//                                        if (cube.isGlowing()) {
//                                            listIndex = 1;
//                                        }
//                                    }
//                                    int lodIndex = ((lod) * SkinConfig.getNumberOfRenderLayers()) + listIndex;
//
//                                    ColouredFace ver = new ColouredFace((byte) (ix + pb.getX()), (byte) (iy + pb.getY()), (byte) (iz + pb.getZ()), avegC[0], avegC[1], avegC[2], avegC[3], avegC[4], (byte) j, lodLevel);
//                                    renderLists[lodIndex].add(ver);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        partData.getClientSkinPartData().setVertexLists(renderLists);
    }

//    private static boolean getAverageFaceFlags(int x, int y, int z, byte lodLevel, int[][][] cubeArray, SkinCubeData cubeData, Rectangle3D partBounds, int face) {
//        for (int ix = 0; ix < lodLevel; ix++) {
//            for (int iy = 0; iy < lodLevel; iy++) {
//                for (int iz = 0; iz < lodLevel; iz++) {
//                    int index = getIndexForLocation(ix + x, iy + y, iz + z, partBounds, cubeArray) - 1;
//                    if (index != -1) {
//                        if (cubeData.getFaceFlags(index).get(face)) {
//                            return true;
//                        }
//                    }
//
//                }
//            }
//        }
//        return false;
//    }
//
//    private static byte[] getAverageRGBAT(int x, int y, int z, byte lodLevel, int[][][] cubeArray, SkinCubeData cubeData, Rectangle3D partBounds, int face) {
//        int count = 0;
//        int r = 0;
//        int g = 0;
//        int b = 0;
//        int a = 0;
//        int[] paintTypes = new int[256];
//        int[] cubeTypes = new int[256];
//        for (int ix = 0; ix < lodLevel; ix++) {
//            for (int iy = 0; iy < lodLevel; iy++) {
//                for (int iz = 0; iz < lodLevel; iz++) {
//                    int index = getIndexForLocation(ix + x, iy + y, iz + z, partBounds, cubeArray) - 1;
//                    if (index != -1) {
//                        if (cubeData.getFaceFlags(index).get(face)) {
//                            count += 1;
//                            r += (cubeData.getCubeColourR(index)[face] & 0xFF);
//                            g += (cubeData.getCubeColourG(index)[face] & 0xFF);
//                            b += (cubeData.getCubeColourB(index)[face] & 0xFF);
//                            if (cubeData.getCube(index).needsPostRender()) {
//                                a += 127;
//                            } else {
//                                a += 255;
//                            }
//                            paintTypes[cubeData.getCubePaintType(index)[face] & 0xFF] += 1;
//                            cubeTypes[cubeData.getCubeId(index) & 0xFF] += 1;
//                        }
//                    }
//                }
//            }
//        }
//        byte[] rgbat = new byte[6];
//        if (count != 0) {
//            rgbat[0] = (byte) (r / count);
//            rgbat[1] = (byte) (g / count);
//            rgbat[2] = (byte) (b / count);
//            rgbat[3] = (byte) (a / count);
//
//            int commonPaintTypeIndex = 0;
//            int mostPaintTypes = 0;
//            for (int i = 0; i < paintTypes.length; i++) {
//                if (paintTypes[i] > mostPaintTypes) {
//                    mostPaintTypes = paintTypes[i];
//                    commonPaintTypeIndex = i;
//                }
//            }
//            rgbat[4] = (byte) commonPaintTypeIndex;
//
//            int commonCubeTypesIndex = 0;
//            int mostCubeTypes = 0;
//            for (int i = 0; i < cubeTypes.length; i++) {
//                if (cubeTypes[i] > mostCubeTypes) {
//                    mostCubeTypes = cubeTypes[i];
//                    commonCubeTypesIndex = i;
//                }
//            }
//            rgbat[5] = (byte) commonCubeTypesIndex;
//
//        }
//        return rgbat;
//    }
//
//    static ArrayList<ColouredFace> getCullQuads(PackedCube packedCube, SkinCubeData cubeData, Rectangle3D rectangle3D) {
//
//        ArrayList<ColouredFace> partQuads = new ArrayList<>();
//
//        packedCube.forEach((cube, x, y, z) -> {
//            for (Direction dir : Direction.values()) {
//                ICube targetCube = packedCube.getCube(x - dir.getStepX(), y - dir.getStepY(), z - dir.getStepZ());
//                if (!needsRender(cube, targetCube)) {
//                    continue;
//                }
//                int index = packedCube.getCubeIndex(x, y, z);
//                ColouredFace colouredFace = cubeData.getCubeFace(index, dir);
//                partQuads.add(colouredFace);
//            }
//        });
//
//        return partQuads;
//    }

}

