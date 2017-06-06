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
import riskyken.armourersWorkshop.common.exception.SkinSaveException;
import riskyken.armourersWorkshop.common.exception.SkinSaveException.SkinSaveExceptionType;
import riskyken.armourersWorkshop.common.skin.cubes.CubeColour;
import riskyken.armourersWorkshop.common.skin.cubes.CubeMarkerData;
import riskyken.armourersWorkshop.common.skin.cubes.CubeRegistry;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinCubeData;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.skin.type.block.SkinBlock;
import riskyken.armourersWorkshop.common.tileentities.TileEntityBoundingBox;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourable;
import riskyken.armourersWorkshop.utils.BlockUtils;
import riskyken.plushieWrapper.common.world.BlockLocation;
/**
 * Helper class for converting back and forth from
 * in world blocks to skin classes.
 * 
 * Note: Minecraft models are inside out, blocks are
 * flipped when loading and saving.
 * 
 * @author RiskyKen
 *
 */
public final class ArmourerWorldHelper {
    
    /**
     * Converts blocks in the world into a skin class.
     * @param world The world.
     * @param skinProps The skin properties for this skin.
     * @param skinType The type of skin to save.
     * @param paintData Paint data for this skin.
     * @param xCoord Armourers x location.
     * @param yCoord Armourers y location.
     * @param zCoord Armourers z location.
     * @param directionDirection the armourer is facing.
     * @return
     * @throws InvalidCubeTypeException
     * @throws SkinSaveException
     */
    public static Skin saveSkinFromWorld(World world, SkinProperties skinProps, ISkinType skinType, int[] paintData,
            int xCoord, int yCoord, int zCoord, ForgeDirection direction) throws SkinSaveException {
        
        ArrayList<SkinPart> parts = new ArrayList<SkinPart>();
        
        if (skinType == SkinTypeRegistry.skinBlock) {
            ISkinPartType partType = ((SkinBlock)SkinTypeRegistry.skinBlock).partBase;
            if (skinProps.getPropertyBoolean(Skin.KEY_BLOCK_MULTIBLOCK, false)) {
                partType = ((SkinBlock)SkinTypeRegistry.skinBlock).partMultiblock;
            }
            saveArmourPart(world, parts, partType, xCoord, yCoord, zCoord, direction);
        } else {
            for (int i = 0; i < skinType.getSkinParts().size(); i++) {
                ISkinPartType partType = skinType.getSkinParts().get(i);
                saveArmourPart(world, parts, partType, xCoord, yCoord, zCoord, direction);
            }
        }
        
        if (paintData != null) {
            paintData = paintData.clone();
        }
        
        Skin skin = new Skin(skinProps, skinType, paintData, parts);
        
        //Check if there are any blocks in the build guides.
        if (skin.getParts().size() == 0 && !skin.hasPaintData()) {
            throw new SkinSaveException("Nothing to save.", SkinSaveExceptionType.NO_DATA);
        }
        
        //Check if the skin has all needed parts.
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType partType = skinType.getSkinParts().get(i);
            if (partType.isPartRequired()) {
                boolean havePart = false;
                for (int j = 0; j < skin.getPartCount(); j++) {
                    if (partType == skin.getParts().get(j).getPartType()) {
                        havePart = true;
                        break;
                    }
                }
                if (!havePart) {
                    throw new SkinSaveException("Skin is missing part " + partType.getPartName(), SkinSaveExceptionType.MISSING_PARTS);
                }
            }
        }
        
        //Check if the skin is not a seat and a bed.
        if (skinProps.getPropertyBoolean(Skin.KEY_BLOCK_BED, false) & skinProps.getPropertyBoolean(Skin.KEY_BLOCK_SEAT, false)) {
            throw new SkinSaveException("Skin can not be a bed and a seat.", SkinSaveExceptionType.BED_AND_SEAT);
        }
        
        //Check if multiblock is valid.
        if (skinType == SkinTypeRegistry.skinBlock & skinProps.getPropertyBoolean(Skin.KEY_BLOCK_MULTIBLOCK, false)) {
            ArrayList<SkinPart> testPart = new ArrayList<SkinPart>();
            saveArmourPart(world, testPart, ((SkinBlock)SkinTypeRegistry.skinBlock).partBase, xCoord, yCoord, zCoord, direction); 
            if (testPart.isEmpty()) {
                throw new SkinSaveException("Multiblock has no blocks in the yellow area.", SkinSaveExceptionType.INVALID_MULTIBLOCK);
            }
        }
        
        return skin;
    }
    
    private static void saveArmourPart(World world, ArrayList<SkinPart> armourData,
            ISkinPartType skinPart, int xCoord, int yCoord, int zCoord, ForgeDirection direction) throws SkinSaveException {
        
        int cubeCount = getNumberOfCubesInPart(world, xCoord, yCoord, zCoord, skinPart);
        if (cubeCount < 1) {
            return;
        }
        SkinCubeData cubeData = new SkinCubeData();
        cubeData.setCubeCount(cubeCount);
        
        ArrayList<CubeMarkerData> markerBlocks = new ArrayList<CubeMarkerData>();
        
        IRectangle3D buildSpace = skinPart.getBuildingSpace();
        IPoint3D offset = skinPart.getOffset();
        
        int i = 0;
        for (int ix = 0; ix < buildSpace.getWidth(); ix++) {
            for (int iy = 0; iy < buildSpace.getHeight(); iy++) {
                for (int iz = 0; iz < buildSpace.getDepth(); iz++) {
                    
                    int x = xCoord + ix + -offset.getX() + buildSpace.getX();
                    int y = yCoord + iy + -offset.getY();
                    int z = zCoord + iz + offset.getZ() + buildSpace.getZ();
                    
                    int xOrigin = -ix + -buildSpace.getX();
                    int yOrigin = -iy + -buildSpace.getY();
                    int zOrigin = -iz + -buildSpace.getZ();
                    
                    if (!world.isAirBlock(x, y, z)) {
                        Block block = world.getBlock(x, y, z);
                        if (CubeRegistry.INSTANCE.isBuildingBlock(block)) {
                            saveArmourBlockToList(world, x, y, z,
                                    xOrigin - 1,
                                    yOrigin - 1,
                                    -zOrigin,
                                    cubeData, i, markerBlocks, direction);
                            i++;
                        }
                    }
                }
            }
        }
        
        if (skinPart.getMinimumMarkersNeeded() > markerBlocks.size()) {
            throw new SkinSaveException("Missing marker for part " + skinPart.getPartName(), SkinSaveExceptionType.MARKER_ERROR);
        }
        
        if (markerBlocks.size() > skinPart.getMaximumMarkersNeeded()) {
            throw new SkinSaveException("Too many markers for part " + skinPart.getPartName(), SkinSaveExceptionType.MARKER_ERROR);
        }
        
        armourData.add(new SkinPart(cubeData, skinPart, markerBlocks));
    }
    
    private static void saveArmourBlockToList(World world, int x, int y, int z, int ix, int iy, int iz,
            SkinCubeData cubeData, int index, ArrayList<CubeMarkerData> markerBlocks, ForgeDirection direction) {
        Block block = world.getBlock(x, y, z);
        if (!CubeRegistry.INSTANCE.isBuildingBlock(block)) {
            return;
        }
            
        int meta = world.getBlockMetadata(x, y, z);
        ICubeColour c = BlockUtils.getColourFromTileEntity(world, x, y, z);
        byte cubeType = CubeRegistry.INSTANCE.getCubeFromBlock(block).getId();
        
        cubeData.setCubeId(index, cubeType);
        cubeData.setCubeLocation(index, (byte) ix, (byte) iy, (byte) iz);
        for (int i = 0; i < 6; i++) {
            cubeData.setCubeColour(index, i, c.getRed(i), c.getGreen(i), c.getBlue(i));
            cubeData.setCubePaintType(index, i, c.getPaintType(i));
        }
        if (meta > 0) {
            markerBlocks.add(new CubeMarkerData((byte)ix, (byte)iy, (byte)iz, (byte)meta));
        }
    }
    
    /**
     * Converts a skin class into blocks in the world.
     * @param world The world.
     * @param x Armourers x location.
     * @param y Armourers y location.
     * @param z Armourers z location.
     * @param skin The skin to load.
     * @param direction The direction the armourer is facing.
     */
    public static void loadSkinIntoWorld(World world, int x, int y, int z, Skin skin, ForgeDirection direction) {
        ArrayList<SkinPart> parts = skin.getParts();
        
        for (int i = 0; i < parts.size(); i++) {
            loadSkinPartIntoWorld(world, parts.get(i), x, y, z, direction);
        }
    }
    
    private static void loadSkinPartIntoWorld(World world, SkinPart partData, int xCoord, int yCoord, int zCoord, ForgeDirection direction) {
        ISkinPartType skinPart = partData.getPartType();
        IRectangle3D buildSpace = skinPart.getBuildingSpace();
        IPoint3D offset = skinPart.getOffset();
        SkinCubeData cubeData = partData.getCubeData();
        
        for (int i = 0; i < cubeData.getCubeCount(); i++) {
            ICube blockData = cubeData.getCube(i);
            int meta = 0;
            for (int j = 0; j < partData.getMarkerBlocks().size(); j++) {
                CubeMarkerData cmd = partData.getMarkerBlocks().get(j);
                byte[] loc = cubeData.getCubeLocation(i);
                if (cmd.x == loc[0] & cmd.y == loc[1] & cmd.z == loc[2]) {
                    meta = cmd.meta;
                    break;
                }
            }
            
            int xOrigin = -offset.getX();
            int yOrigin = -offset.getY() + -buildSpace.getY();
            int zOrigin = offset.getZ();
            
            loadSkinBlockIntoWorld(world, xCoord, yCoord, zCoord, xOrigin, yOrigin, zOrigin, blockData, direction, meta, cubeData, i);
        }
        
    }
    
    private static void loadSkinBlockIntoWorld(World world, int x, int y, int z,
            int xOrigin, int yOrigin, int zOrigin, ICube blockData,
            ForgeDirection direction, int meta, SkinCubeData cubeData, int index) {
        
        byte[] loc = cubeData.getCubeLocation(index);
        
        int shiftX = -loc[0] - 1;
        int shiftY = loc[1] + 1;
        int shiftZ = loc[2];
        
        int targetX = x + shiftX + xOrigin;
        int targetY = y + yOrigin - shiftY;
        int targetZ = z + shiftZ + zOrigin;
        
        if (world.getBlock(targetX, targetY, targetZ) == ModBlocks.boundingBox) {
            world.setBlockToAir(targetX, targetY, targetZ);
            world.removeTileEntity(targetX, targetY, targetZ);
        }
        
        if (world.isAirBlock(targetX, targetY, targetZ)) {
            Block targetBlock = blockData.getMinecraftBlock();
            world.setBlock(targetX, targetY, targetZ, targetBlock);
            world.setBlockMetadataWithNotify(targetX, targetY, targetZ, meta, 2);
            TileEntity te = world.getTileEntity(targetX, targetY, targetZ);
            if (te != null && te instanceof TileEntityColourable) {
                CubeColour cc = new CubeColour();
                for (int i = 0; i < 6; i++) {
                    byte[] c = cubeData.getCubeColour(index, i);
                    byte paintType = cubeData.getCubePaintType(index, i);
                    cc.setRed(c[0], i);
                    cc.setGreen(c[1], i);
                    cc.setBlue(c[2], i);
                    cc.setPaintType(paintType, i);
                }
                ((TileEntityColourable)te).setColour(cc);
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
    
    public static int clearEquipmentCubes(World world, int x, int y, int z, ISkinType skinType, SkinProperties skinProps) {
        int blockCount = 0;
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType skinPart = skinType.getSkinParts().get(i);
            if (skinType == SkinTypeRegistry.skinBlock) {
                boolean multiblock = skinProps.getPropertyBoolean(Skin.KEY_BLOCK_MULTIBLOCK, false);
                if (skinPart == ((SkinBlock)SkinTypeRegistry.skinBlock).partBase & !multiblock) {
                    blockCount += clearEquipmentCubesForSkinPart(world, x, y, z, skinPart);
                }
                if (skinPart == ((SkinBlock)SkinTypeRegistry.skinBlock).partMultiblock & multiblock) {
                    blockCount += clearEquipmentCubesForSkinPart(world, x, y, z, skinPart);
                }
            } else {
                blockCount += clearEquipmentCubesForSkinPart(world, x, y, z, skinPart);
            }
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
                        if (CubeRegistry.INSTANCE.isBuildingBlock(block)) {
                            world.setBlockToAir(xTar, yTar, zTar);
                            world.removeTileEntity(xTar, yTar, zTar);
                            blockCount++;
                        }
                    }
                    
                }
            }
        }
        return blockCount;
    }

    public static ArrayList<BlockLocation> getListOfPaintableCubes(World world, int x, int y, int z, ISkinType skinType) {
        ArrayList<BlockLocation> blList = new ArrayList<BlockLocation>();
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType skinPart = skinType.getSkinParts().get(i);
            getBuildingCubesForPart(world, x, y, z, skinPart, blList);
        }
        return blList;
    }
    
    private static void getBuildingCubesForPart(World world, int x, int y, int z, ISkinPartType skinPart, ArrayList<BlockLocation> blList) {
        IRectangle3D buildSpace = skinPart.getBuildingSpace();
        IPoint3D offset = skinPart.getOffset();
        
        for (int ix = 0; ix < buildSpace.getWidth(); ix++) {
            for (int iy = 0; iy < buildSpace.getHeight(); iy++) {
                for (int iz = 0; iz < buildSpace.getDepth(); iz++) {
                    int xTar = x + ix + -offset.getX() + buildSpace.getX();
                    int yTar = y + iy + -offset.getY();
                    int zTar = z + iz + offset.getZ() + buildSpace.getZ();
                    
                    if (world.blockExists(xTar, yTar, zTar)) {
                        Block block = world.getBlock(xTar, yTar, zTar);
                        if (CubeRegistry.INSTANCE.isBuildingBlock(block)) {
                            blList.add(new BlockLocation(xTar, yTar, zTar));
                        }
                    }
                }
            }
        }
    }
    
    private static int getNumberOfCubesInPart(World world, int x, int y, int z, ISkinPartType skinPart) {
        IRectangle3D buildSpace = skinPart.getBuildingSpace();
        IPoint3D offset = skinPart.getOffset();
        int cubeCount = 0;
        for (int ix = 0; ix < buildSpace.getWidth(); ix++) {
            for (int iy = 0; iy < buildSpace.getHeight(); iy++) {
                for (int iz = 0; iz < buildSpace.getDepth(); iz++) {
                    int xTar = x + ix + -offset.getX() + buildSpace.getX();
                    int yTar = y + iy + -offset.getY();
                    int zTar = z + iz + offset.getZ() + buildSpace.getZ();
                    
                    if (world.blockExists(xTar, yTar, zTar)) {
                        Block block = world.getBlock(xTar, yTar, zTar);
                        if (CubeRegistry.INSTANCE.isBuildingBlock(block)) {
                            cubeCount++;
                        }
                    }
                }
            }
        }
        return cubeCount;
    }
}
