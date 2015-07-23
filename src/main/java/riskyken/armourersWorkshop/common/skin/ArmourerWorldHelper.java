package riskyken.armourersWorkshop.common.skin;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.IPoint3D;
import riskyken.armourersWorkshop.api.common.IRectangle3D;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.exception.InvalidCubeTypeException;
import riskyken.armourersWorkshop.common.skin.cubes.CubeFactory;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.tileentities.TileEntityBoundingBox;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourable;
import riskyken.armourersWorkshop.utils.UtilBlocks;
/**
 * Helper class for converting back and forth from
 * in world blocks to skin classes.
 * 
 * @author RiskyKen
 *
 */
public final class ArmourerWorldHelper {
    
    public static Skin saveSkinFromWorld(World world, ISkinType skinType,
            String authorName, String customName, String tags,
            int xCoord, int yCoord, int zCoord, ForgeDirection direction) throws InvalidCubeTypeException {
        
        ArrayList<SkinPart> parts = new ArrayList<SkinPart>();
        
        
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            saveArmourPart(world, parts, skinType.getSkinParts().get(i), xCoord, yCoord, zCoord, direction);
        }
        
        if (parts.size() > 0) {
            return new Skin(authorName, customName, tags, skinType, parts);
        } else {
            return null;
        }
    }
    
    private static void saveArmourPart(World world, ArrayList<SkinPart> armourData,
            ISkinPartType skinPart, int xCoord, int yCoord, int zCoord, ForgeDirection direction) throws InvalidCubeTypeException {
        ArrayList<ICube> armourBlockData = new ArrayList<ICube>();
        
        IRectangle3D buildSpace = skinPart.getBuildingSpace();
        IPoint3D offset = skinPart.getOffset();
        
        for (int ix = 0; ix < buildSpace.getWidth(); ix++) {
            for (int iy = 0; iy < buildSpace.getHeight(); iy++) {
                for (int iz = 0; iz < buildSpace.getDepth(); iz++) {
                    
                    int x = xCoord + ix + -offset.getX() + buildSpace.getX();
                    int y = yCoord + iy + -offset.getY();
                    int z = zCoord + iz + offset.getZ() + buildSpace.getZ();
                    
                    int xOrigin = -ix + -buildSpace.getX();
                    int yOrigin = -iy + -buildSpace.getY();
                    int zOrigin = -iz + -buildSpace.getZ();
                    
                    saveArmourBlockToList(world, x, y, z,
                            xOrigin - 1,
                            yOrigin - 1,
                            -zOrigin,
                            armourBlockData, direction);
                }
            }
        }
        
        if (armourBlockData.size() > 0) {
            armourData.add(new SkinPart(armourBlockData, skinPart));
        }
    }
    
    private static void saveArmourBlockToList(World world, int x, int y, int z, int ix, int iy, int iz,
            ArrayList<ICube> list, ForgeDirection direction) throws InvalidCubeTypeException {
        if (world.isAirBlock(x, y, z)) {
            return;
        }
        
        Block block = world.getBlock(x, y, z);
        if (block == ModBlocks.colourable | block == ModBlocks.colourableGlowing | block == ModBlocks.colourableGlass | block == ModBlocks.colourableGlassGlowing) {
            
            
            ICubeColour colour = UtilBlocks.getColourFromTileEntity(world, x, y, z);
            
            byte blockType = 0;
            if (block == ModBlocks.colourableGlowing) {
                blockType = 1;
            }
            if (block == ModBlocks.colourableGlass) {
                blockType = 2;
            }
            if (block == ModBlocks.colourableGlassGlowing) {
                blockType = 3;
            }
            ICube blockData = CubeFactory.INSTANCE.getCubeInstanceFormId(blockType);
            
            blockData.setX((byte) ix);
            blockData.setY((byte) iy);
            blockData.setZ((byte) iz);
            blockData.setColour(colour);
            
            list.add(blockData);
        }
    }
    
    public static void loadSkinIntoWorld(World world, int x, int y, int z, Skin armourData, ForgeDirection direction) {
        ArrayList<SkinPart> parts = armourData.getParts();
        
        for (int i = 0; i < parts.size(); i++) {
            loadSkinPartIntoWorld(world, parts.get(i), x, y, z, direction);
        }
    }
    
    private static void loadSkinPartIntoWorld(World world, SkinPart partData, int xCoord, int yCoord, int zCoord, ForgeDirection direction) {
        ISkinPartType skinPart = partData.getPartType();
        IRectangle3D buildSpace = skinPart.getBuildingSpace();
        IPoint3D offset = skinPart.getOffset();
        
        for (int i = 0; i < partData.getArmourData().size(); i++) {
            ICube blockData = partData.getArmourData().get(i);
            
            int xOrigin = -offset.getX();
            int yOrigin = -offset.getY() + -buildSpace.getY();
            int zOrigin = offset.getZ();
            
            loadSkinBlockIntoWorld(world, xCoord, yCoord, zCoord, xOrigin, yOrigin, zOrigin, blockData, direction);
        }
    }
    
    private static void loadSkinBlockIntoWorld(World world, int x, int y, int z, int xOrigin, int yOrigin, int zOrigin,
            ICube blockData, ForgeDirection direction) {
        
        int shiftX = -blockData.getX() - 1;
        int shiftY = blockData.getY() + 1;
        int shiftZ = blockData.getZ();
        
        int targetX = x + shiftX + xOrigin;
        int targetY = y + yOrigin - shiftY;
        int targetZ = z + shiftZ + zOrigin;
        
        if (world.isAirBlock(targetX, targetY, targetZ)) {
            Block targetBlock = ModBlocks.colourable;
            if (blockData.getId() == 1) {
                targetBlock = ModBlocks.colourableGlowing;
            }
            if (blockData.getId() == 2) {
                targetBlock = ModBlocks.colourableGlass;
            }
            if (blockData.getId() == 3) {
                targetBlock = ModBlocks.colourableGlassGlowing;
            }
            world.setBlock(targetX, targetY, targetZ, targetBlock);
            TileEntity te = world.getTileEntity(targetX, targetY, targetZ);
            if (te != null && te instanceof TileEntityColourable) {
                ((TileEntityColourable)te).setColour(blockData.getCubeColour());
            }
        }
    }
    
    public static void createBoundingBoxes(World world, int x, int y, int z, int parentX, int parentY, int parentZ, ISkinType skinType) {
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType skinPart = skinType.getSkinParts().get(i);
            createBoundingBoxesForSkinPart(world, x, y, z, parentX, parentY, parentZ, skinPart);
        }
    }
    
    private static void createBoundingBoxesForSkinPart(World world, int x, int y, int z, int parentX, int parentY, int parentZ, ISkinPartType skinPart) {
        IRectangle3D buildSpace = skinPart.getBuildingSpace();
        IRectangle3D guideSpace = skinPart.getGuideSpace();
        IPoint3D offset = skinPart.getOffset();
        
        if (guideSpace == null) {
            return;
        }
        
        for (int ix = 0; ix < guideSpace.getWidth(); ix++) {
            for (int iy = 0; iy < guideSpace.getHeight(); iy++) {
                for (int iz = 0; iz < guideSpace.getDepth(); iz++) {
                    int xTar = x + ix + -offset.getX() + guideSpace.getX();
                    int yTar = y + iy + -offset.getY() + guideSpace.getY() - buildSpace.getY();
                    int zTar = z + iz + offset.getZ() + guideSpace.getZ();
                    
                    //TODO Set skinPart to left and right legs for skirt.
                    ISkinPartType guidePart = skinPart;
                    byte guideX = (byte) ix;
                    byte guideY = (byte) iy;
                    byte guideZ = (byte) iz;
                    
                    if (world.isAirBlock(xTar, yTar, zTar)) {
                        world.setBlock(xTar, yTar, zTar, ModBlocks.boundingBox);
                        TileEntity te = null;
                        te = world.getTileEntity(xTar, yTar, zTar);
                        if (te != null && te instanceof TileEntityBoundingBox) {
                            ((TileEntityBoundingBox)te).setParent(parentX, parentY, parentZ,
                                    guideX, guideY, guideZ, guidePart);
                        } else {
                            te = new TileEntityBoundingBox(parentX, parentY, parentZ,
                                    guideX, guideY, guideZ, guidePart);
                            world.setTileEntity(xTar, yTar, zTar, te);
                        }
                    }
                    
                }
            }
        }
    }
    
    public static void removeBoundingBoxes(World world, int x, int y, int z, ISkinType skinType) {
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType skinPart = skinType.getSkinParts().get(i);
            removeBoundingBoxesForSkinPart(world, x, y, z, skinPart);
        }
    }
    
    private static void removeBoundingBoxesForSkinPart(World world, int x, int y, int z, ISkinPartType skinPart) {
        IRectangle3D buildSpace = skinPart.getBuildingSpace();
        IRectangle3D guideSpace = skinPart.getGuideSpace();
        IPoint3D offset = skinPart.getOffset();
        
        if (guideSpace == null) {
            return;
        }
        
        for (int ix = 0; ix < guideSpace.getWidth(); ix++) {
            for (int iy = 0; iy < guideSpace.getHeight(); iy++) {
                for (int iz = 0; iz < guideSpace.getDepth(); iz++) {
                    int xTar = x + ix + -offset.getX() + guideSpace.getX();
                    int yTar = y + iy + -offset.getY() + guideSpace.getY() - buildSpace.getY();
                    int zTar = z + iz + offset.getZ() + guideSpace.getZ();
                    
                    if (world.blockExists(xTar, yTar, zTar)) {
                        if (world.getBlock(xTar, yTar, zTar) == ModBlocks.boundingBox) {
                            world.setBlockToAir(xTar, yTar, zTar);
                        }
                    }
                    
                }
            }
        }
    }
    
    public static int clearEquipmentCubes(World world, int x, int y, int z, ISkinType skinType) {
        int blockCount = 0;
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType skinPart = skinType.getSkinParts().get(i);
            blockCount += clearEquipmentCubesForSkinPart(world, x, y, z, skinPart);
        }
        return blockCount;
    }
    
    private static int clearEquipmentCubesForSkinPart(World world, int x, int y, int z, ISkinPartType skinPart) {
        IRectangle3D buildSpace = skinPart.getBuildingSpace();
        IPoint3D offset = skinPart.getOffset();
        int blockCount = 0;
        
        for (int ix = 0; ix < buildSpace.getWidth(); ix++) {
            for (int iy = 0; iy < buildSpace.getHeight(); iy++) {
                for (int iz = 0; iz < buildSpace.getDepth(); iz++) {
                    int xTar = x + ix + -offset.getX() + buildSpace.getX();
                    int yTar = y + iy + -offset.getY();
                    int zTar = z + iz + offset.getZ() + buildSpace.getZ();
                    
                    if (world.blockExists(xTar, yTar, zTar)) {
                        Block block = world.getBlock(xTar, yTar, zTar);
                        if (
                            block == ModBlocks.colourable |
                            block == ModBlocks.colourableGlowing |
                            block == ModBlocks.colourableGlass |
                            block == ModBlocks.colourableGlassGlowing
                            ) {
                            world.setBlockToAir(xTar, yTar, zTar);
                            blockCount++;
                        }
                    }
                    
                }
            }
        }
        return blockCount;
    }
}
