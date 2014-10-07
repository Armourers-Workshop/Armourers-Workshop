package riskyken.armourersWorkshop.client.render;

import java.util.ArrayList;
import java.util.BitSet;

import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.common.equipment.data.CustomArmourPartData;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentBlockData;

public final class EquipmentRenderHelper {

    public static void cullFacesOnEquipmentPart(CustomArmourPartData partData) {
        ArrayList<CustomEquipmentBlockData> blocks = partData.getArmourData();
        for (int i = 0; i < blocks.size(); i++) {
            CustomEquipmentBlockData blockData = blocks.get(i);
            setBlockFaceFlags(blocks, blockData);
            partData.facesBuild = true;
        }
    }
    
    private static void setBlockFaceFlags(ArrayList<CustomEquipmentBlockData> partBlocks, CustomEquipmentBlockData block) {
        block.faceFlags = new BitSet(6);
        for (int j = 0; j < partBlocks.size(); j++) {
            CustomEquipmentBlockData checkBlock = partBlocks.get(j);
            checkFaces(block, checkBlock);
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
}
