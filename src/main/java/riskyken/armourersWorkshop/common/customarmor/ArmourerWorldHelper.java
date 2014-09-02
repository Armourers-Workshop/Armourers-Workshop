package riskyken.armourersWorkshop.common.customarmor;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.UtilBlocks;

public final class ArmourerWorldHelper {
    
    public static ArrayList<CustomArmourData> buildArmourItem(World world, ArmourerType type, EntityPlayer player, int xCoord, int yCoord, int zCoord) {
        
        ArrayList<CustomArmourData> armourData = new ArrayList<CustomArmourData>();
        
        switch (type) {
        case HEAD:
            buildArmourPart(world, type, armourData, ArmourPart.HEAD, xCoord, yCoord, zCoord);
            break;
        case CHEST:
            buildArmourPart(world, type, armourData, ArmourPart.CHEST, xCoord, yCoord, zCoord);
            buildArmourPart(world, type, armourData, ArmourPart.LEFT_ARM, xCoord, yCoord, zCoord);
            buildArmourPart(world, type, armourData, ArmourPart.RIGHT_ARM, xCoord, yCoord, zCoord);
            break;
        case LEGS:
            buildArmourPart(world, type, armourData, ArmourPart.LEFT_LEG, xCoord, yCoord, zCoord);
            buildArmourPart(world, type, armourData, ArmourPart.RIGHT_LEG, xCoord, yCoord, zCoord);
            break;
        case SKIRT:
            buildArmourPart(world, type, armourData, ArmourPart.SKIRT, xCoord, yCoord, zCoord);
            break;
        case FEET:
            buildArmourPart(world, type, armourData, ArmourPart.LEFT_FOOT, xCoord, yCoord, zCoord);
            buildArmourPart(world, type, armourData, ArmourPart.RIGHT_FOOT, xCoord, yCoord, zCoord);
            break;
        default:
            ModLogger.log(Level.WARN, "TileEntityArmourerBrain at X:" + xCoord + " Y:" + yCoord +
                    " Z:" + zCoord + " has an invalid armour type.");
            break;
        }
        
        return armourData;
    }
    
    private static void buildArmourPart(World world, ArmourerType type, ArrayList<CustomArmourData> armourData, ArmourPart part, int xCoord, int yCoord, int zCoord) {
        ArrayList<ArmourBlockData> armourBlockData = new ArrayList<ArmourBlockData>();
        
        for (int ix = 0; ix < part.getXSize(); ix++) {
            for (int iy = 0; iy < part.getYSize(); iy++) {
                for (int iz = 0; iz < part.getZSize(); iz++) {
                    
                    int x = xCoord + ix + part.getXOffset();
                    int y = yCoord + iy + part.getYOffset();
                    int z = zCoord + iz + part.getZOffset();
                    
                    int xOrigin = xCoord + part.getXOrigin();
                    int yOrigin = yCoord + part.getYOrigin();
                    int zOrigin = zCoord + part.getZOrigin();
                    
                    addArmourToList(world, x, y, z,
                            -(x - xOrigin) - 1,
                            -(y - yOrigin) - 1,
                            z - zOrigin,
                            armourBlockData, part);
                }
            }
        }
        
        if (armourBlockData.size() > 0) {
            armourData.add(new CustomArmourData(armourBlockData, type, part));
        }
    }
    
    private static void addArmourToList(World world, int x, int y, int z, int ix, int iy, int iz, ArrayList<ArmourBlockData> list, ArmourPart armourPart) {
        if (world.isAirBlock(x, y, z)) { return; }
        Block block = world.getBlock(x, y, z);
        if (block == ModBlocks.colourable | block == ModBlocks.colourableGlowing) {
            int colour = UtilBlocks.getColourFromTileEntity(world ,x, y, z);
            ArmourBlockData blockData = new ArmourBlockData(ix, iy, iz,
                    colour, block == ModBlocks.colourableGlowing);
            list.add(blockData);
        }
    }
    
    /*
    public void buildArmourItem(EntityPlayer player) {
        switch (type) {
        case HEAD:
            buildArmourPart(player, ArmourPart.HEAD);
            break;
        case CHEST:
            buildArmourPart(player, ArmourPart.CHEST);
            buildArmourPart(player, ArmourPart.LEFT_ARM);
            buildArmourPart(player, ArmourPart.RIGHT_ARM);
            break;
        case LEGS:
            buildArmourPart(player, ArmourPart.LEFT_LEG);
            buildArmourPart(player, ArmourPart.RIGHT_LEG);
            CustomArmourManager.removeCustomArmour(player, type, ArmourPart.SKIRT);
            break;
        case SKIRT:
            buildArmourPart(player, ArmourPart.SKIRT);
            CustomArmourManager.removeCustomArmour(player, type, ArmourPart.LEFT_LEG);
            CustomArmourManager.removeCustomArmour(player, type, ArmourPart.RIGHT_LEG);
            break;
        case FEET:
            buildArmourPart(player, ArmourPart.LEFT_FOOT);
            buildArmourPart(player, ArmourPart.RIGHT_FOOT);
            break;
        default:
            ModLogger.log(Level.WARN, "TileEntityArmourerBrain at X:" + xCoord + " Y:" + yCoord +
                    " Z:" + zCoord + " has an invalid armour type.");
            break;
        }
    }
    
    private void buildArmourPart(EntityPlayer player, ArmourPart part) {
        ArrayList<ArmourBlockData> armourBlockData = new ArrayList<ArmourBlockData>();
        
        for (int ix = 0; ix < part.getXSize(); ix++) {
            for (int iy = 0; iy < part.getYSize(); iy++) {
                for (int iz = 0; iz < part.getZSize(); iz++) {
                    
                    int x = xCoord + xOffset + ix + part.getXOffset();
                    int y = yCoord + iy + 1 + part.getYOffset();
                    int z = zCoord + zOffset + iz + part.getZOffset();
                    
                    int xOrigin = xCoord + xOffset + part.getXOrigin();
                    int yOrigin = yCoord + 1 + part.getYOrigin();
                    int zOrigin = zCoord + zOffset + part.getZOrigin();
                    
                    addArmourToList(x, y, z,
                            -(x - xOrigin) - 1,
                            -(y - yOrigin) - 1,
                            z - zOrigin,
                            armourBlockData, part);
                }
            }
        }
        
        if (armourBlockData.size() > 0) {
            CustomArmourData armourData = new CustomArmourData(armourBlockData, type, part);
            CustomArmourManager.addCustomArmour(player, armourData);
        } else {
            CustomArmourManager.removeCustomArmour(player, type, part);
        }
    }
    
    private void addArmourToList(int x, int y, int z, int ix, int iy, int iz, ArrayList<ArmourBlockData> list, ArmourPart armourPart) {
        if (worldObj.isAirBlock(x, y, z)) { return; }
        Block block = worldObj.getBlock(x, y, z);
        if (block == ModBlocks.colourable | block == ModBlocks.colourableGlowing) {
            int colour = UtilBlocks.getColourFromTileEntity(worldObj ,x, y, z);
            ArmourBlockData blockData = new ArmourBlockData(ix, iy, iz,
                    colour, block == ModBlocks.colourableGlowing);
            list.add(blockData);
        }
    }
    */
}
