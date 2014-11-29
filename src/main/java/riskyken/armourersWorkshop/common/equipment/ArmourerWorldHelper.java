package riskyken.armourersWorkshop.common.equipment;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentPart;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentBlockData;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentPartData;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourable;
import riskyken.armourersWorkshop.utils.UtilBlocks;

public final class ArmourerWorldHelper {
    
    public static CustomEquipmentItemData saveArmourItem(World world, EnumEquipmentType type,
            String authorName, String customName, int xCoord, int yCoord, int zCoord, ForgeDirection direction) {
        ArrayList<CustomEquipmentPartData> parts = new ArrayList<CustomEquipmentPartData>();
        
        for (int i = 0; i < type.getParts().length; i++) {
            saveArmourPart(world, parts, type.getParts()[i], xCoord, yCoord, zCoord, direction);
        }
        
        if (parts.size() > 0) {
            return new CustomEquipmentItemData(authorName, customName, type, parts);
        } else {
            return null;
        }
    }
    
    private static void saveArmourPart(World world, ArrayList<CustomEquipmentPartData> armourData,
            EnumEquipmentPart part, int xCoord, int yCoord, int zCoord, ForgeDirection direction) {
        ArrayList<CustomEquipmentBlockData> armourBlockData = new ArrayList<CustomEquipmentBlockData>();
        
        
        for (int ix = 0; ix < part.getTotalXSize(); ix++) {
            for (int iy = 0; iy < part.getTotalYSize(); iy++) {
                for (int iz = 0; iz < part.getTotalZSize(); iz++) {
                    
                    int x = xCoord + part.getStartX() - part.xLocation + ix;
                    int y = yCoord + part.getStartY() + part.yLocation + iy;
                    int z = zCoord + part.getStartZ() + part.zLocation + iz;
                    
                    int xOrigin = xCoord - part.xLocation - x + ((part.xSize / 2) + part.xOrigin);
                    int yOrigin = iy + part.yOrigin - part.getBuildSpaceForDirection(ForgeDirection.DOWN);
                    int zOrigin = zCoord + part.zLocation - z;
                    
                    saveArmourBlockToList(world, x, y, z,
                            xOrigin - 1,
                            -yOrigin - 1,
                            -zOrigin,
                            armourBlockData, direction);
                    
                }
            }
        }
        
        if (armourBlockData.size() > 0) {
            armourData.add(new CustomEquipmentPartData(armourBlockData, part));
        }
    }
    
    private static void saveArmourBlockToList(World world, int x, int y, int z, int ix, int iy, int iz,
            ArrayList<CustomEquipmentBlockData> list, ForgeDirection direction) {
        if (world.isAirBlock(x, y, z)) { return; }
        Block block = world.getBlock(x, y, z);
        if (block == ModBlocks.colourable | block == ModBlocks.colourableGlowing) {
            int colour = UtilBlocks.getColourFromTileEntity(world ,x, y, z);
            byte blockType = 0;
            if (block == ModBlocks.colourableGlowing) {
                blockType = 1;
            }
            CustomEquipmentBlockData blockData = new CustomEquipmentBlockData(ix, iy, iz,
                    colour, blockType);
            
            list.add(blockData);
        }
    }
    
    public static void loadArmourItem(World world, int x, int y, int z, CustomEquipmentItemData armourData,
            ForgeDirection direction) {
        ArrayList<CustomEquipmentPartData> parts = armourData.getParts();
        
        for (int i = 0; i < parts.size(); i++) {
            loadArmourPart(world, parts.get(i), x, y, z, direction);
        }
    }
    
    private static void loadArmourPart(World world, CustomEquipmentPartData partData, int xCoord, int yCoord, int zCoord,
            ForgeDirection direction) {
        for (int i = 0; i < partData.getArmourData().size(); i++) {
            CustomEquipmentBlockData blockData = partData.getArmourData().get(i);
            
            int xOrigin = -partData.getArmourPart().xLocation  + ((partData.getArmourPart().xSize / 2) + partData.getArmourPart().xOrigin);
            int yOrigin = -partData.getArmourPart().yOrigin + partData.getArmourPart().yLocation;
            int zOrigin = partData.getArmourPart().zLocation;
            loadArmourBlock(world, xCoord, yCoord, zCoord, xOrigin, yOrigin, zOrigin, blockData, direction);
        }
    }
    
    private static void loadArmourBlock(World world, int x, int y, int z, int xOrigin, int yOrigin, int zOrigin,
            CustomEquipmentBlockData blockData, ForgeDirection direction) {
        
        
        int shiftX = -blockData.x - 1;
        int shiftY = blockData.y + 1;
        int shiftZ = blockData.z;
        /*
        switch (direction) {
        case SOUTH:
            shiftZ += 1;
            shiftX += 1;
            break;
        case EAST:
            shiftZ += 1;
            break;
        case WEST:
            shiftX += 1;
            break;
        default:
            break;
        }
        
        int targetX = x + (shiftX * -direction.offsetZ) + (shiftZ * -direction.offsetX);
        int targetY = y + shiftY;
        int targetZ = z + (shiftZ * -direction.offsetZ) + (shiftX * direction.offsetX);
        */
        int targetX = x + shiftX + xOrigin;
        int targetY = y + yOrigin - shiftY;
        int targetZ = z + shiftZ + zOrigin;
        
        //ModLogger.log(targetX);
        //ModLogger.log(targetZ);
        
        if (world.isAirBlock(targetX, targetY, targetZ)) {
            Block targetBlock = ModBlocks.colourable;
            if (blockData.isGlowing()) {
                targetBlock = ModBlocks.colourableGlowing;
            }
            world.setBlock(targetX, targetY, targetZ, targetBlock);
            TileEntity te = world.getTileEntity(targetX, targetY, targetZ);
            if (te != null && te instanceof TileEntityColourable) {
                ((TileEntityColourable)te).setColour(blockData.colour);
            }
        }
    }
}
