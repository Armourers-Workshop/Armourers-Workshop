package moe.plushie.armourers_workshop.client.model.bake;

import java.util.ArrayList;
import java.util.HashSet;

import moe.plushie.armourers_workshop.api.common.skin.Rectangle3D;
import moe.plushie.armourers_workshop.common.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.common.skin.cubes.CubeRegistry;
import moe.plushie.armourers_workshop.common.skin.cubes.ICube;
import moe.plushie.armourers_workshop.common.skin.data.SkinCubeData;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;

public final class SkinBaker {
    
    public static boolean withinMaxRenderDistance(Entity entity) {
        return withinMaxRenderDistance(entity.posX, entity.posY, entity.posZ);
    }
    
    public static boolean withinMaxRenderDistance(double x, double y, double z) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player.getDistance(x, y, z) > ConfigHandlerClient.maxSkinRenderDistance) {
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
        
        int updates = 0;
        
        for (int i = 0; i < cubeData.getCubeCount(); i++) {
            int cubeId = cubeData.getCubeId(i);
            byte[] cubeLoc = cubeData.getCubeLocation(i);
            skinPart.getClientSkinPartData().totalCubesInPart[cubeId] += 1;
            int x = (int)cubeLoc[0] - pb.getX();
            int y = (int)cubeLoc[1] - pb.getY();
            int z = (int)cubeLoc[2] - pb.getZ();
            cubeArray[x][y][z] = i + 1;
            if (ConfigHandlerClient.slowModelBaking) {
                updates++;
                if (updates > 40) {
                    try {
                        Thread.sleep(1);
                        updates = 0;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        ArrayList<CubeLocation> openList = new ArrayList<CubeLocation>();
        HashSet<Integer> closedSet = new HashSet<Integer>();
        CubeLocation startCube = new CubeLocation(-1, -1, -1);
        openList.add(startCube);
        closedSet.add(startCube.hashCode());
        
        while (openList.size() > 0) {
            CubeLocation cl = openList.get(openList.size() - 1);
            openList.remove(openList.size() - 1);
            ArrayList<CubeLocation> foundLocations = checkCubesAroundLocation(cubeData, cl, pb, cubeArray);
            for (int i = 0; i < foundLocations.size(); i++) {
                CubeLocation foundLocation = foundLocations.get(i);
                if (!closedSet.contains(foundLocation.hashCode())) {
                    closedSet.add(foundLocation.hashCode());
                    if (isCubeInSearchArea(foundLocation, pb)) {
                        openList.add(foundLocation);
                    }
                }
            }
            if (ConfigHandlerClient.slowModelBaking) {
                updates++;
                if (updates > 40) {
                    try {
                        Thread.sleep(1);
                        updates = 0;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return cubeArray;
    }
    
    private static ArrayList<CubeLocation> checkCubesAroundLocation(SkinCubeData cubeData, CubeLocation cubeLocation, Rectangle3D partBounds, int[][][] cubeArray) {
        ArrayList<CubeLocation> openList = new ArrayList<SkinBaker.CubeLocation>();
        EnumFacing[] dirs = {EnumFacing.DOWN, EnumFacing.UP,
                EnumFacing.SOUTH, EnumFacing.NORTH,
                EnumFacing.WEST, EnumFacing.EAST };
        
        int index = getIndexForLocation(cubeLocation, partBounds, cubeArray);
        
        boolean isGlass = false;
        if (index > 0) {
            ICube cube = cubeData.getCube(index - 1);
            isGlass = cube.needsPostRender();
        }
        
        for (int i = 0; i < dirs.length; i++) {
            EnumFacing dir = dirs[i];
            int x = cubeLocation.x + dir.getXOffset();
            int y = cubeLocation.y + dir.getYOffset();
            int z = cubeLocation.z + dir.getZOffset();
            int tarIndex = getIndexForLocation(x, y, z, partBounds, cubeArray);
            
            
            //Add new cubes to the open list.
            if (tarIndex < 1) {
                openList.add(new CubeLocation(x, y, z));
            } else {
                if (cubeData.getCube(tarIndex - 1).needsPostRender()) {
                    openList.add(new CubeLocation(x, y, z));
                }
            }
            
            //Update the face flags if there is a block at this location.
            if (tarIndex > 0) {
                flagCubeFace(x, y, z, i, partBounds, cubeArray, cubeData, isGlass);
            }
        }
        return openList;
    }
    
    private static int getIndexForLocation(CubeLocation cubeLocation, Rectangle3D partBounds, int[][][] cubeArray) {
        return getIndexForLocation(cubeLocation.x, cubeLocation.y, cubeLocation.z, partBounds, cubeArray);
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
    
    private static boolean isCubeInSearchArea(CubeLocation cubeLocation, Rectangle3D partBounds) {
        if (cubeLocation.x > -2 & cubeLocation.x < partBounds.getWidth() + 1) {
            if (cubeLocation.y > -2 & cubeLocation.y < partBounds.getHeight() + 1) {
                if (cubeLocation.z > -2 & cubeLocation.z < partBounds.getDepth() + 1) {
                    return true;
                }   
            }
        }
        return false;
    }
    
    public static void buildPartDisplayListArray(SkinPart partData, int[][] dyeColour, int[] dyeUseCount, int[][][] cubeArray) {
        boolean multipassSkinRendering = ClientProxy.useMultipassSkinRendering();
        
        ArrayList<ColouredFace>[] renderLists;
        
        int lodLevels = ConfigHandlerClient.maxLodLevels;
        
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
        
        renderLists = (ArrayList<ColouredFace>[]) new ArrayList[ClientProxy.getNumberOfRenderLayers() * (lodLevels + 1)];
        
        for (int i = 0; i < renderLists.length; i++) {
            renderLists[i] = new ArrayList<ColouredFace>();
        }
        
        float scale = 0.0625F;
        
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
                            int paint = paintType[j] & 0xFF;
                            if (paint >= 1 && paint <= 8 && cubeData.getFaceFlags(i).get(j)) {
                                dyeUseCount[paint - 1]++;
                                dyeColour[0][paint - 1] += r[j]  & 0xFF;
                                dyeColour[1][paint - 1] += g[j]  & 0xFF;
                                dyeColour[2][paint - 1] += b[j]  & 0xFF;
                            }
                            if (paint == 253) {
                                dyeUseCount[8]++;
                                dyeColour[0][8] += r[j]  & 0xFF;
                                dyeColour[1][8] += g[j]  & 0xFF;
                                dyeColour[2][8] += b[j]  & 0xFF;
                            }
                            if (paint == 254) {
                                dyeUseCount[9]++;
                                dyeColour[0][9] += r[j]  & 0xFF;
                                dyeColour[1][9] += g[j]  & 0xFF;
                                dyeColour[2][9] += b[j]  & 0xFF;
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
                                ColouredFace ver = new ColouredFace(
                                        loc[0], loc[1], loc[2],
                                        r[j], g[j], b[j],
                                        a, paintType[j], (byte)j, (byte)(1));
                                renderLists[listIndex].add(ver);
                            }
                        }
                        

                        
                    }
                    
                    //Create model LODs
                    for (int lod = 1; lod < lodLevels + 1; lod++) {
                        byte lodLevel = (byte)Math.pow(2, lod);
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
                                    int lodIndex = ((lod) * ClientProxy.getNumberOfRenderLayers()) + listIndex;
                                    
                                    ColouredFace ver = new ColouredFace(
                                            (byte)(ix + pb.getX()), (byte)(iy + pb.getY()), (byte)(iz + pb.getZ()),
                                            avegC[0], avegC[1], avegC[2],
                                            avegC[3], avegC[4], (byte)j, lodLevel);
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
    
    private static class CubeLocation {
        public final int x;
        public final int y;
        public final int z;
        
        public CubeLocation(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }



        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CubeLocation other = (CubeLocation) obj;
            if (x != other.x)
                return false;
            if (y != other.y)
                return false;
            if (z != other.z)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "CubeLocation [x=" + x + ", y=" + y + ", z=" + z + "]";
        }
    }
}
