package riskyken.armourersWorkshop.client.render;

import java.util.ArrayList;
import java.util.BitSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.skin.cubes.CubeRegistry;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;

public final class EquipmentRenderHelper {
    
    public static boolean withinMaxRenderDistance(double x, double y, double z) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (player.getDistance(x, y, z) > ConfigHandler.maxRenderDistance) {
            return false;
        }
        return true;
    }
    
    public static void cullFacesOnEquipmentPart(SkinPart partData) {
        ArrayList<ICube> blocks = partData.getArmourData();
        partData.totalCubesInPart = new int[CubeRegistry.INSTANCE.getTotalCubes()];
        for (int i = 0; i < blocks.size(); i++) {
            ICube blockData = blocks.get(i);
            int cubeId = CubeRegistry.INSTANCE.getIdForCubeClass(blockData.getClass());
            partData.totalCubesInPart[cubeId] += 1;
            setBlockFaceFlags(blocks, blockData);
            partData.facesBuild = true;
        }
    }
    
    private static void setBlockFaceFlags(ArrayList<ICube> partBlocks, ICube block) {
        block.setFaceFlags(new BitSet(6));
        for (int j = 0; j < partBlocks.size(); j++) {
            ICube checkBlock = partBlocks.get(j);
            checkFaces(block, checkBlock);
        }
    }
    
    private static void checkFaces(ICube block, ICube checkBlock) {
        ForgeDirection[] dirs = {ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST };
        //dirs = ForgeDirection.VALID_DIRECTIONS;
        for (int i = 0; i < dirs.length; i++) {
            ForgeDirection dir = dirs[i];
            if (block.getX() + dir.offsetX == checkBlock.getX()) {
                if (block.getY() + dir.offsetY == checkBlock.getY()) {
                    if (block.getZ() + dir.offsetZ == checkBlock.getZ()) {
                        block.getFaceFlags().set(i, true); 
                    }
                }
            }
        }

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
