package riskyken.armourersWorkshop.common.custom.equipment;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourPart;
import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourType;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourPartData;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomEquipmentBlockData;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourable;
import riskyken.armourersWorkshop.utils.UtilBlocks;

public final class ArmourerWorldHelper {
    
    public static CustomArmourItemData saveArmourItem(World world, ArmourType type, String authorName, String customName, int xCoord, int yCoord, int zCoord) {
        ArrayList<CustomArmourPartData> parts = new ArrayList<CustomArmourPartData>();
        
        for (int i = 0; i < type.getParts().length; i++) {
            saveArmourPart(world, parts, type.getParts()[i], xCoord, yCoord, zCoord);
        }
        
        if (parts.size() > 0) {
            return new CustomArmourItemData(authorName, customName, type, parts);
        } else {
            return null;
        }
    }
    
    private static void saveArmourPart(World world, ArrayList<CustomArmourPartData> armourData, ArmourPart part, int xCoord, int yCoord, int zCoord) {
        ArrayList<CustomEquipmentBlockData> armourBlockData = new ArrayList<CustomEquipmentBlockData>();
        
        for (int ix = 0; ix < part.xSize; ix++) {
            for (int iy = 0; iy < part.ySize; iy++) {
                for (int iz = 0; iz < part.zSize; iz++) {
                    
                    int x = xCoord + ix + part.xOffset;
                    int y = yCoord + iy + part.yOffset;
                    int z = zCoord + iz + part.zOffset;
                    
                    int xOrigin = xCoord + part.xOrigin;
                    int yOrigin = yCoord + part.yOrigin;
                    int zOrigin = zCoord + part.zOrigin;
                    
                    saveArmourBlockToList(world, x, y, z,
                            -(x - xOrigin) - 1,
                            -(y - yOrigin) - 1,
                            z - zOrigin,
                            armourBlockData);
                }
            }
        }
        
        if (armourBlockData.size() > 0) {
            armourData.add(new CustomArmourPartData(armourBlockData, part));
        }
    }
    
    private static void saveArmourBlockToList(World world, int x, int y, int z, int ix, int iy, int iz, ArrayList<CustomEquipmentBlockData> list) {
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
    
    public static void loadArmourItem(World world, int x, int y, int z, CustomArmourItemData armourData) {
        ArrayList<CustomArmourPartData> parts = armourData.getParts();
        
        for (int i = 0; i < parts.size(); i++) {
            loadArmourPart(world, parts.get(i), x, y, z);
        }
    }
    
    private static void loadArmourPart(World world, CustomArmourPartData partData, int xCoord, int yCoord, int zCoord) {
        for (int i = 0; i < partData.getArmourData().size(); i++) {
            CustomEquipmentBlockData blockData = partData.getArmourData().get(i);
            int xOrigin = xCoord + partData.getArmourPart().xOrigin;
            int yOrigin = yCoord + partData.getArmourPart().yOrigin;
            int zOrigin = zCoord + partData.getArmourPart().zOrigin;
            loadArmourBlock(world, xOrigin, yOrigin, zOrigin, blockData);
        }
    }
    
    private static void loadArmourBlock(World world, int x, int y, int z, CustomEquipmentBlockData blockData) {
        int targetX = x - blockData.x - 1;
        int targetY = y - blockData.y - 1;
        int targetZ = z + blockData.z;
        
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
