package riskyken.armourersWorkshop.common.customarmor;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.customarmor.data.CustomArmourBlockData;
import riskyken.armourersWorkshop.common.customarmor.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.customarmor.data.CustomArmourPartData;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourable;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.UtilBlocks;

public final class ArmourerWorldHelper {
    
    public static CustomArmourItemData saveArmourItem(World world, ArmourerType type, EntityPlayer player, int xCoord, int yCoord, int zCoord) {
        ArrayList<CustomArmourPartData> parts = new ArrayList<CustomArmourPartData>();
        
        switch (type) {
        case HEAD:
            saveArmourPart(world, parts, ArmourPart.HEAD, xCoord, yCoord, zCoord);
            break;
        case CHEST:
            saveArmourPart(world, parts, ArmourPart.CHEST, xCoord, yCoord, zCoord);
            saveArmourPart(world, parts, ArmourPart.LEFT_ARM, xCoord, yCoord, zCoord);
            saveArmourPart(world, parts, ArmourPart.RIGHT_ARM, xCoord, yCoord, zCoord);
            break;
        case LEGS:
            saveArmourPart(world, parts, ArmourPart.LEFT_LEG, xCoord, yCoord, zCoord);
            saveArmourPart(world, parts, ArmourPart.RIGHT_LEG, xCoord, yCoord, zCoord);
            break;
        case SKIRT:
            saveArmourPart(world, parts, ArmourPart.SKIRT, xCoord, yCoord, zCoord);
            break;
        case FEET:
            saveArmourPart(world, parts, ArmourPart.LEFT_FOOT, xCoord, yCoord, zCoord);
            saveArmourPart(world, parts, ArmourPart.RIGHT_FOOT, xCoord, yCoord, zCoord);
            break;
        default:
            ModLogger.log(Level.WARN, "TileEntityArmourerBrain at X:" + xCoord + " Y:" + yCoord +
                    " Z:" + zCoord + " has an invalid armour type.");
            break;
        }
        
        return new CustomArmourItemData(type, parts);
    }
    
    private static void saveArmourPart(World world, ArrayList<CustomArmourPartData> armourData, ArmourPart part, int xCoord, int yCoord, int zCoord) {
        ArrayList<CustomArmourBlockData> armourBlockData = new ArrayList<CustomArmourBlockData>();
        
        for (int ix = 0; ix < part.getXSize(); ix++) {
            for (int iy = 0; iy < part.getYSize(); iy++) {
                for (int iz = 0; iz < part.getZSize(); iz++) {
                    
                    int x = xCoord + ix + part.getXOffset();
                    int y = yCoord + iy + part.getYOffset();
                    int z = zCoord + iz + part.getZOffset();
                    
                    int xOrigin = xCoord + part.getXOrigin();
                    int yOrigin = yCoord + part.getYOrigin();
                    int zOrigin = zCoord + part.getZOrigin();
                    
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
    
    private static void saveArmourBlockToList(World world, int x, int y, int z, int ix, int iy, int iz, ArrayList<CustomArmourBlockData> list) {
        if (world.isAirBlock(x, y, z)) { return; }
        Block block = world.getBlock(x, y, z);
        if (block == ModBlocks.colourable | block == ModBlocks.colourableGlowing) {
            int colour = UtilBlocks.getColourFromTileEntity(world ,x, y, z);
            byte blockType = 0;
            if (block == ModBlocks.colourableGlowing) {
                blockType = 1;
            }
            CustomArmourBlockData blockData = new CustomArmourBlockData(ix, iy, iz,
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
            CustomArmourBlockData blockData = partData.getArmourData().get(i);
            int xOrigin = xCoord + partData.getArmourPart().getXOrigin();
            int yOrigin = yCoord + partData.getArmourPart().getYOrigin();
            int zOrigin = zCoord + partData.getArmourPart().getZOrigin();
            loadArmourBlock(world, xOrigin, yOrigin, zOrigin, blockData);
        }
    }
    
    private static void loadArmourBlock(World world, int x, int y, int z, CustomArmourBlockData blockData) {
        int targetX = x - blockData.x - 1;
        int targetY = y - blockData.y - 1;
        int targetZ = z + blockData.z;
        
        if (world.isAirBlock(targetX, targetY, targetZ)) {
            world.setBlock(targetX, targetY, targetZ, ModBlocks.colourable);
            TileEntity te = world.getTileEntity(targetX, targetY, targetZ);
            if (te != null && te instanceof TileEntityColourable) {
                ((TileEntityColourable)te).setColour(blockData.colour);
            }
        }
    }
}
