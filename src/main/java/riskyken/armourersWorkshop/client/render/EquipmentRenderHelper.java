package riskyken.armourersWorkshop.client.render;

import java.util.ArrayList;
import java.util.BitSet;

import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.equipment.EnumBodyPart;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentPartData;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentBlockData;

public final class EquipmentRenderHelper {

    public static void cullFacesOnEquipmentPart(CustomEquipmentPartData partData) {
        ArrayList<CustomEquipmentBlockData> blocks = partData.getArmourData();
        for (int i = 0; i < blocks.size(); i++) {
            CustomEquipmentBlockData blockData = blocks.get(i);
            setBlockFaceFlags(blocks, blockData, partData.getArmourPart().bodyPart);
            partData.facesBuild = true;
        }
    }
    
    private static void setBlockFaceFlags(ArrayList<CustomEquipmentBlockData> partBlocks, CustomEquipmentBlockData block, EnumBodyPart bodyPart) {
        block.faceFlags = new BitSet(6);
        for (int j = 0; j < partBlocks.size(); j++) {
            CustomEquipmentBlockData checkBlock = partBlocks.get(j);
            checkFaces(block, checkBlock);
        }
        
        if (bodyPart != null) {
            //checkBlockFaceIntersectsBodyPart(bodyPart, block);
        }
        
    }
    
    private static void checkFaces(CustomEquipmentBlockData block, CustomEquipmentBlockData checkBlock) {
        ForgeDirection[] dirs = { ForgeDirection.EAST, ForgeDirection.WEST,  ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH };
        for (int i = 0; i < dirs.length; i++) {
            ForgeDirection dir = dirs[i];
            if (block.x + dir.offsetX == checkBlock.x) {
                if (block.y + dir.offsetY == checkBlock.y) {
                    if (block.z + dir.offsetZ == checkBlock.z) {
                        block.faceFlags.set(i, true); 
                    }
                }
            }
        }

    }
    
    private static void checkBlockFaceIntersectsBodyPart(EnumBodyPart bodyPart, CustomEquipmentBlockData block) {
        ForgeDirection[] dirs = { ForgeDirection.EAST, ForgeDirection.WEST,  ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH };
        for (int i = 0; i < dirs.length; i++) {
            ForgeDirection dir = dirs[i];
            if (cordsIntersectsBodyPart(bodyPart, (block.x + dir.offsetX), -(block.y + dir.offsetY + 1), (block.z + dir.offsetZ))) {
                block.faceFlags.set(i, true);
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
}
