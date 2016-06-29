package riskyken.armourersWorkshop.common.skin;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
import riskyken.armourersWorkshop.common.tileentities.TileEntityBoundingBox;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourable;
import riskyken.armourersWorkshop.utils.UtilBlocks;
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
     * @param skinType The type of skin to save.
     * @param authorName Author name for this skin.
     * @param customName Custom display name for this skin.
     * @param tags Custom search tags for this skin.
     * @param xCoord Armourers x location.
     * @param yCoord Armourers y location.
     * @param zCoord Armourers z location.
     * @param direction Direction the armourer is facing.
     * @return
     * @throws InvalidCubeTypeException
     * @throws SkinSaveException 
     */
    public static Skin saveSkinFromWorld(World world, EntityPlayerMP player, ISkinType skinType,
            String authorName, String customName, String tags, int[] paintData,
            BlockPos pos, EnumFacing direction) throws InvalidCubeTypeException, SkinSaveException {
        
        ArrayList<SkinPart> parts = new ArrayList<SkinPart>();
        
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType partType = skinType.getSkinParts().get(i);
            saveArmourPart(world, parts, partType, pos, direction);
        }
        
        if (paintData != null) {
            paintData = paintData.clone();
        }
        Skin skin = new Skin(authorName, customName, tags, skinType, paintData, parts);
        
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
        
        return skin;
    }
    
    private static void saveArmourPart(World world, ArrayList<SkinPart> armourData,
            ISkinPartType skinPart, BlockPos pos, EnumFacing direction) throws InvalidCubeTypeException, SkinSaveException {
        
        int cubeCount = getNumberOfCubesInPart(world, pos, skinPart);
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
                    
                    int x = pos.getX() + ix + -offset.getX() + buildSpace.getX();
                    int y = pos.getY() + iy + -offset.getY();
                    int z = pos.getZ() + iz + offset.getZ() + buildSpace.getZ();
                    
                    int xOrigin = -ix + -buildSpace.getX();
                    int yOrigin = -iy + -buildSpace.getY();
                    int zOrigin = -iz + -buildSpace.getZ();
                    
                    BlockPos newPos = new BlockPos(x, y, z);
                    
                    if (!world.isAirBlock(newPos)) {
                        Block block = world.getBlockState(newPos).getBlock();
                        if (CubeRegistry.INSTANCE.isBuildingBlock(block)) {
                            saveArmourBlockToList(world, newPos,
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
    
    private static void saveArmourBlockToList(World world, BlockPos pos, int ix, int iy, int iz,
            SkinCubeData cubeData, int index, ArrayList<CubeMarkerData> markerBlocks, EnumFacing direction) {
        Block block = world.getBlockState(pos).getBlock();
        if (!CubeRegistry.INSTANCE.isBuildingBlock(block)) {
            return;
        }
            
        //int meta = world.getBlockMetadata(x, y, z);
        ICubeColour c = UtilBlocks.getColourFromTileEntity(world, pos);
        byte cubeType = CubeRegistry.INSTANCE.getCubeFromBlock(block).getId();
        
        cubeData.setCubeId(index, cubeType);
        cubeData.setCubeLocation(index, (byte) ix, (byte) iy, (byte) iz);
        for (int i = 0; i < 6; i++) {
            EnumFacing face = EnumFacing.values()[i];
            cubeData.setCubeColour(index, i, c.getRed(face), c.getGreen(face), c.getBlue(face));
            cubeData.setCubePaintType(index, i, c.getPaintType(face));
        }
        /*
        if (meta > 0) {
            markerBlocks.add(new CubeMarkerData((byte)ix, (byte)iy, (byte)iz, (byte)meta));
        }
        */
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
    public static void loadSkinIntoWorld(World world, BlockPos pos, Skin skin, EnumFacing direction) {
        ArrayList<SkinPart> parts = skin.getParts();
        
        for (int i = 0; i < parts.size(); i++) {
            loadSkinPartIntoWorld(world, parts.get(i), pos, direction);
        }
    }
    
    private static void loadSkinPartIntoWorld(World world, SkinPart partData, BlockPos pos, EnumFacing direction) {
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
            
            loadSkinBlockIntoWorld(world, pos, xOrigin, yOrigin, zOrigin, blockData, direction, meta, cubeData, i);
        }
        
    }
    
    private static void loadSkinBlockIntoWorld(World world, BlockPos pos,
            int xOrigin, int yOrigin, int zOrigin, ICube blockData,
            EnumFacing direction, int meta, SkinCubeData cubeData, int index) {
        
        byte[] loc = cubeData.getCubeLocation(index);
        
        int shiftX = -loc[0] - 1;
        int shiftY = loc[1] + 1;
        int shiftZ = loc[2];
        
        int targetX = pos.getX() + shiftX + xOrigin;
        int targetY = pos.getY() + yOrigin - shiftY;
        int targetZ = pos.getZ() + shiftZ + zOrigin;
        
        BlockPos targetPos = new BlockPos(targetX, targetY, targetZ);
        
        if (world.getBlockState(targetPos).getBlock() == ModBlocks.boundingBox) {
            world.setBlockToAir(targetPos);
            world.removeTileEntity(targetPos);
        }
        
        if (world.isAirBlock(targetPos)) {
            Block targetBlock = blockData.getMinecraftBlock();
            world.setBlockState(targetPos, targetBlock.getDefaultState());
            //world.setBlockMetadataWithNotify(targetX, targetY, targetZ, meta, 2);
            TileEntity te = world.getTileEntity(targetPos);
            if (te != null && te instanceof TileEntityColourable) {
                CubeColour cc = new CubeColour();
                for (int i = 0; i < 6; i++) {
                    EnumFacing face = EnumFacing.values()[i];
                    byte[] c = cubeData.getCubeColour(index, i);
                    byte paintType = cubeData.getCubePaintType(index, i);
                    cc.setRed(c[0], face);
                    cc.setGreen(c[1], face);
                    cc.setBlue(c[2], face);
                    cc.setPaintType(paintType, face);
                }
                ((TileEntityColourable)te).setColour(cc);
            }
        }
    }
    
    public static void createBoundingBoxes(World world, BlockPos pos, BlockPos parentPos, ISkinType skinType) {
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType skinPart = skinType.getSkinParts().get(i);
            createBoundingBoxesForSkinPart(world, pos, parentPos, skinPart);
        }
    }
    
    private static void createBoundingBoxesForSkinPart(World world, BlockPos pos, BlockPos parentPos, ISkinPartType skinPart) {
        IRectangle3D buildSpace = skinPart.getBuildingSpace();
        IRectangle3D guideSpace = skinPart.getGuideSpace();
        IPoint3D offset = skinPart.getOffset();
        
        if (guideSpace == null) {
            return;
        }
        
        for (int ix = 0; ix < guideSpace.getWidth(); ix++) {
            for (int iy = 0; iy < guideSpace.getHeight(); iy++) {
                for (int iz = 0; iz < guideSpace.getDepth(); iz++) {
                    
                    int xTar = pos.getX() + ix + -offset.getX() + guideSpace.getX();
                    int yTar = pos.getY() + iy + -offset.getY() + guideSpace.getY() - buildSpace.getY();
                    int zTar = pos.getZ() + iz + offset.getZ() + guideSpace.getZ();
                    
                    BlockPos targetPos = new BlockPos(xTar, yTar, zTar);
                    
                    //TODO Set skinPart to left and right legs for skirt.
                    ISkinPartType guidePart = skinPart;
                    byte guideX = (byte) ix;
                    byte guideY = (byte) iy;
                    byte guideZ = (byte) iz;
                    
                    if (world.isAirBlock(targetPos)) {
                        world.setBlockState(targetPos, ModBlocks.boundingBox.getDefaultState());
                        TileEntity te = null;
                        te = world.getTileEntity(targetPos);
                        if (te != null && te instanceof TileEntityBoundingBox) {
                            ((TileEntityBoundingBox)te).setParent(parentPos,
                                    guideX, guideY, guideZ, guidePart);
                        } else {
                            te = new TileEntityBoundingBox(parentPos,
                                    guideX, guideY, guideZ, guidePart);
                            world.setTileEntity(targetPos, te);
                        }
                    }
                    
                }
            }
        }
    }
    
    public static void removeBoundingBoxes(World world, BlockPos pos, ISkinType skinType) {
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType skinPart = skinType.getSkinParts().get(i);
            removeBoundingBoxesForSkinPart(world, pos, skinPart);
        }
    }
    
    private static void removeBoundingBoxesForSkinPart(World world, BlockPos pos, ISkinPartType skinPart) {
        IRectangle3D buildSpace = skinPart.getBuildingSpace();
        IRectangle3D guideSpace = skinPart.getGuideSpace();
        IPoint3D offset = skinPart.getOffset();
        
        if (guideSpace == null) {
            return;
        }
        
        for (int ix = 0; ix < guideSpace.getWidth(); ix++) {
            for (int iy = 0; iy < guideSpace.getHeight(); iy++) {
                for (int iz = 0; iz < guideSpace.getDepth(); iz++) {
                    int xTar = pos.getX() + ix + -offset.getX() + guideSpace.getX();
                    int yTar = pos.getY() + iy + -offset.getY() + guideSpace.getY() - buildSpace.getY();
                    int zTar = pos.getZ() + iz + offset.getZ() + guideSpace.getZ();
                    
                    BlockPos targetPos = new BlockPos(xTar, yTar, zTar);
                    
                    if (!world.isAirBlock(targetPos)) {
                        if (world.getBlockState(targetPos).getBlock() == ModBlocks.boundingBox) {
                            world.setBlockToAir(targetPos);
                        }
                    }
                    
                }
            }
        }
    }
    
    public static int clearEquipmentCubes(World world, BlockPos pos, ISkinType skinType) {
        int blockCount = 0;
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType skinPart = skinType.getSkinParts().get(i);
            blockCount += clearEquipmentCubesForSkinPart(world, pos, skinPart);
        }
        return blockCount;
    }
    
    private static int clearEquipmentCubesForSkinPart(World world, BlockPos pos, ISkinPartType skinPart) {
        IRectangle3D buildSpace = skinPart.getBuildingSpace();
        IPoint3D offset = skinPart.getOffset();
        int blockCount = 0;
        
        for (int ix = 0; ix < buildSpace.getWidth(); ix++) {
            for (int iy = 0; iy < buildSpace.getHeight(); iy++) {
                for (int iz = 0; iz < buildSpace.getDepth(); iz++) {
                    int xTar = pos.getX() + ix + -offset.getX() + buildSpace.getX();
                    int yTar = pos.getY() + iy + -offset.getY();
                    int zTar = pos.getZ() + iz + offset.getZ() + buildSpace.getZ();
                    
                    BlockPos targetPos = new BlockPos(xTar, yTar, zTar);
                    
                    if (!world.isAirBlock(targetPos)) {
                        Block block = world.getBlockState(targetPos).getBlock();
                        //TODO use CubeFactory to check cube.
                        if (
                            block == ModBlocks.colourable |
                            block == ModBlocks.colourableGlowing |
                            block == ModBlocks.colourableGlass |
                            block == ModBlocks.colourableGlassGlowing
                            ) {
                            world.setBlockToAir(targetPos);
                            world.removeTileEntity(targetPos);
                            blockCount++;
                        }
                    }
                    
                }
            }
        }
        return blockCount;
    }

    public static ArrayList<BlockPos> getListOfPaintableCubes(World world, BlockPos pos, ISkinType skinType) {
        ArrayList<BlockPos> blList = new ArrayList<BlockPos>();
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType skinPart = skinType.getSkinParts().get(i);
            getBuildingCubesForPart(world, pos, skinPart, blList);
        }
        return blList;
    }
    
    private static void getBuildingCubesForPart(World world, BlockPos pos, ISkinPartType skinPart, ArrayList<BlockPos> blList) {
        IRectangle3D buildSpace = skinPart.getBuildingSpace();
        IPoint3D offset = skinPart.getOffset();
        
        for (int ix = 0; ix < buildSpace.getWidth(); ix++) {
            for (int iy = 0; iy < buildSpace.getHeight(); iy++) {
                for (int iz = 0; iz < buildSpace.getDepth(); iz++) {
                    int xTar = pos.getX() + ix + -offset.getX() + buildSpace.getX();
                    int yTar = pos.getY() + iy + -offset.getY();
                    int zTar = pos.getZ() + iz + offset.getZ() + buildSpace.getZ();
                    
                    BlockPos targetPos = new BlockPos(xTar, yTar, zTar);
                    
                    if (!world.isAirBlock(targetPos)) {
                        Block block = world.getBlockState(targetPos).getBlock();
                        if (CubeRegistry.INSTANCE.isBuildingBlock(block)) {
                            blList.add(new BlockPos(xTar, yTar, zTar));
                        }
                    }
                }
            }
        }
    }
    
    private static int getNumberOfCubesInPart(World world, BlockPos pos, ISkinPartType skinPart) {
        IRectangle3D buildSpace = skinPart.getBuildingSpace();
        IPoint3D offset = skinPart.getOffset();
        int cubeCount = 0;
        for (int ix = 0; ix < buildSpace.getWidth(); ix++) {
            for (int iy = 0; iy < buildSpace.getHeight(); iy++) {
                for (int iz = 0; iz < buildSpace.getDepth(); iz++) {
                    int xTar = pos.getX() + ix + -offset.getX() + buildSpace.getX();
                    int yTar = pos.getY() + iy + -offset.getY();
                    int zTar = pos.getZ() + iz + offset.getZ() + buildSpace.getZ();
                    
                    BlockPos targetPos = new BlockPos(xTar, yTar, zTar);
                    
                    if (!world.isAirBlock(targetPos)) {
                        Block block = world.getBlockState(targetPos).getBlock();
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
