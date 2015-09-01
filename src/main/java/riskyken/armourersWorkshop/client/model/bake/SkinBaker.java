package riskyken.armourersWorkshop.client.model.bake;

import java.util.ArrayList;
import java.util.BitSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraftforge.common.util.ForgeDirection;
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
    
    public static void cullFacesOnEquipmentPart(SkinPart partData) {
        SkinCubeData cubeData = partData.getCubeData();
        cubeData.setupFaceFlags();
        partData.getClientSkinPartData().totalCubesInPart = new int[CubeRegistry.INSTANCE.getTotalCubes()];
        for (int i = 0; i < cubeData.getCubeCount(); i++) {
            int cubeId = cubeData.getCubeId(i);
            partData.getClientSkinPartData().totalCubesInPart[cubeId] += 1;
            setBlockFaceFlags(cubeData, i);
        }
    }
    
    private static void setBlockFaceFlags(SkinCubeData cubeData, int cubeIndex) {
        cubeData.setFaceFlags(cubeIndex, new BitSet(6));
        for (int j = 0; j < cubeData.getCubeCount(); j++) {
            checkFaces(cubeData, cubeIndex, j);
        }
    }
    
    private static void checkFaces(SkinCubeData cubeData, int cubeIndex, int checkIndex) {
        ForgeDirection[] dirs = {ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST };
        byte[] cubeLoc = cubeData.getCubeLocation(cubeIndex);
        byte[] checkLoc = cubeData.getCubeLocation(checkIndex);
        for (int i = 0; i < dirs.length; i++) {
            ForgeDirection dir = dirs[i];
            if (cubeLoc[0] + dir.offsetX == checkLoc[0]) {
                if (cubeLoc[1] + dir.offsetY == checkLoc[1]) {
                    if (cubeLoc[2] + dir.offsetZ == checkLoc[2]) {
                        //if (block.needsPostRender() == checkBlock.needsPostRender()) {
                            cubeData.getFaceFlags(cubeIndex).set(i, true);
                        //}
                    }
                }
            }
        }

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
                        cubeData.getCubeColourR(i), cubeData.getCubeColourG(i), cubeData.getCubeColourB(i), a);
            } else {
                if (cube.isGlowing()) {
                    EquipmentPartRenderer.INSTANCE.main.buildDisplayListArray(renderLists[1],
                            scale, cubeData.getFaceFlags(i), loc[0], loc[1], loc[2],
                            cubeData.getCubeColourR(i), cubeData.getCubeColourG(i), cubeData.getCubeColourB(i), a);
                } else {
                    EquipmentPartRenderer.INSTANCE.main.buildDisplayListArray(renderLists[0],
                            scale, cubeData.getFaceFlags(i), loc[0], loc[1], loc[2],
                            cubeData.getCubeColourR(i), cubeData.getCubeColourG(i), cubeData.getCubeColourB(i), a);
                }
            }
        }
        
        partData.clearCubeData();
        
        partData.getClientSkinPartData().setVertexLists(renderLists);
    }
    
    /*
    private static void checkBlockFaceIntersectsBodyPart(EnumBodyPart bodyPart, ICube block) {
        ForgeDirection[] dirs = { ForgeDirection.EAST, ForgeDirection.WEST,  ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH };
        for (int i = 0; i < dirs.length; i++) {
            ForgeDirection dir = dirs[i];
            if (cordsIntersectsBodyPart(bodyPart, (block.getX() + dir.offsetX), -(block.getY() + dir.offsetY + 1), (block.getZ() + dir.offsetZ))) {
                block.getFaceFlags().set(i, true);
            }
        }
    }
    
    private static boolean cordsIntersectsBodyPart(EnumBodyPart bodyPart, int x, int y, int z) {
        int xCen = x + bodyPart.xOrigin;
        int yCen = y + 1 + bodyPart.yOrigin;
        int zCen = z + bodyPart.zOrigin;
        
        if (bodyPart.xSize > xCen & 0 <= xCen) {
            if (bodyPart.zSize > zCen & 0 <= zCen) {
                if (bodyPart.ySize > yCen & 0 <= yCen) {
                    return true;
                }
            }
        }
        return false;
    }
    */
}
