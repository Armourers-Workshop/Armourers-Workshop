package riskyken.armourersWorkshop.client.model.bake;

import java.util.ArrayList;
import java.util.HashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.client.render.EquipmentPartRenderer;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.skin.cubes.CubeRegistry;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;
import riskyken.armourersWorkshop.common.skin.data.SkinCubeData;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.proxies.ClientProxy;

public final class SkinBaker {
    
    public static boolean withinMaxRenderDistance(double x, double y, double z) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (player.getDistance(x, y, z) > ConfigHandler.maxSkinRenderDistance) {
            return false;
        }
        return true;
    }
    
    public static void cullFacesOnEquipmentPart(SkinPart skinPart) {
        SkinCubeData cubeData = skinPart.getCubeData();
        cubeData.setupFaceFlags();
        skinPart.getClientSkinPartData().totalCubesInPart = new int[CubeRegistry.INSTANCE.getTotalCubes()];
        
        Rectangle3D pb = skinPart.getPartBounds();
        int[][][] cubeArray = new int[pb.getWidth()][pb.getHeight()][pb.getDepth()];
        
        for (int i = 0; i < cubeData.getCubeCount(); i++) {
            int cubeId = cubeData.getCubeId(i);
            byte[] cubeLoc = cubeData.getCubeLocation(i);
            skinPart.getClientSkinPartData().totalCubesInPart[cubeId] += 1;
            int x = (int)cubeLoc[0] - pb.getX();
            int y = (int)cubeLoc[1] - pb.getY();
            int z = (int)cubeLoc[2] - pb.getZ();
            cubeArray[x][y][z] = i + 1;
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
        }
    }
    
    private static ArrayList<CubeLocation> checkCubesAroundLocation(SkinCubeData cubeData, CubeLocation cubeLocation, Rectangle3D partBounds, int[][][] cubeArray) {
        ArrayList<CubeLocation> openList = new ArrayList<SkinBaker.CubeLocation>();
        ForgeDirection[] dirs = {ForgeDirection.DOWN, ForgeDirection.UP,
                ForgeDirection.SOUTH, ForgeDirection.NORTH,
                ForgeDirection.WEST, ForgeDirection.EAST };
        
        int index = getIndexForLocation(cubeLocation, partBounds, cubeArray);
        
        boolean isGlass = false;
        if (index > 0) {
            ICube cube = cubeData.getCube(index - 1);
            isGlass = cube.needsPostRender();
        }
        
        for (int i = 0; i < dirs.length; i++) {
            ForgeDirection dir = dirs[i];
            int x = cubeLocation.x + dir.offsetX;
            int y = cubeLocation.y + dir.offsetY;
            int z = cubeLocation.z + dir.offsetZ;
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
    
    public static void buildPartDisplayListArray(SkinPart partData) {
        boolean multipassSkinRendering = ClientProxy.useMultipassSkinRendering();
        
        ArrayList<ColouredVertexWithUV>[] renderLists;
        int[] pie;
        
        pie = new int[3];
        
        if (multipassSkinRendering) {
            renderLists = (ArrayList<ColouredVertexWithUV>[]) new ArrayList[4];
        } else {
            renderLists = (ArrayList<ColouredVertexWithUV>[]) new ArrayList[2];
        }
        
        for (int i = 0; i < renderLists.length; i++) {
            renderLists[i] = new ArrayList<ColouredVertexWithUV>();
        }
        
        float scale = 0.0625F;
        
        for (int i = 0; i < partData.getCubeData().getCubeCount(); i++) {
            SkinCubeData cubeData = partData.getCubeData();
            
            byte[] loc = cubeData.getCubeLocation(i);
            byte[] paintType = cubeData.getCubePaintType(i);
            ICube cube = partData.getCubeData().getCube(i);
            
            byte a = (byte) 255;
            if (cube.needsPostRender()) {
                a = (byte) 127;
            }
            
            if (multipassSkinRendering) {
                int listIndex = 0;
                if (cube.isGlowing() && !cube.needsPostRender()) {
                    listIndex = 1;
                }
                if (cube.needsPostRender() && !cube.isGlowing()) {
                    listIndex = 2;
                }
                if (cube.isGlowing() && cube.needsPostRender()) {
                    listIndex = 3;
                }
                EquipmentPartRenderer.INSTANCE.main.buildDisplayListArray(renderLists[listIndex],
                        scale, cubeData.getFaceFlags(i), loc[0], loc[1], loc[2],
                        cubeData.getCubeColourR(i), cubeData.getCubeColourG(i),
                        cubeData.getCubeColourB(i), a, paintType);
            } else {
                if (cube.isGlowing()) {
                    EquipmentPartRenderer.INSTANCE.main.buildDisplayListArray(renderLists[1],
                            scale, cubeData.getFaceFlags(i), loc[0], loc[1], loc[2],
                            cubeData.getCubeColourR(i), cubeData.getCubeColourG(i),
                            cubeData.getCubeColourB(i), a, paintType);
                } else {
                    EquipmentPartRenderer.INSTANCE.main.buildDisplayListArray(renderLists[0],
                            scale, cubeData.getFaceFlags(i), loc[0], loc[1], loc[2],
                            cubeData.getCubeColourR(i), cubeData.getCubeColourG(i),
                            cubeData.getCubeColourB(i), a, paintType);
                }
            }
        }
        
        partData.clearCubeData();
        partData.getClientSkinPartData().setVertexLists(renderLists);
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
