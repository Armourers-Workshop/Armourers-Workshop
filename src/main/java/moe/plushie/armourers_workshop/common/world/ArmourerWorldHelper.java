package moe.plushie.armourers_workshop.common.world;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.IPoint3D;
import moe.plushie.armourers_workshop.api.common.IRectangle3D;
import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.common.exception.SkinSaveException;
import moe.plushie.armourers_workshop.common.exception.SkinSaveException.SkinSaveExceptionType;
import moe.plushie.armourers_workshop.common.init.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.skin.cubes.CubeColour;
import moe.plushie.armourers_workshop.common.skin.cubes.CubeMarkerData;
import moe.plushie.armourers_workshop.common.skin.cubes.CubeRegistry;
import moe.plushie.armourers_workshop.common.skin.cubes.ICube;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinCubeData;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.skin.type.block.SkinBlock;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityBoundingBox;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityColourable;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    public static Skin saveSkinFromWorld(World world, SkinProperties skinProps, ISkinType skinType, int[] paintData, BlockPos pos, EnumFacing direction) throws SkinSaveException {
        
        ArrayList<SkinPart> parts = new ArrayList<SkinPart>();
        
        if (skinType == SkinTypeRegistry.skinBlock) {
            ISkinPartType partType = ((SkinBlock)SkinTypeRegistry.skinBlock).partBase;
            if (SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(skinProps)) {
                partType = ((SkinBlock)SkinTypeRegistry.skinBlock).partMultiblock;
            }
            
            SkinPart skinPart = saveArmourPart(world, partType, pos, direction, true);
            if (skinPart != null) {
                parts.add(skinPart);
            }
        } else {
            for (int i = 0; i < skinType.getSkinParts().size(); i++) {
                ISkinPartType partType = skinType.getSkinParts().get(i);
                SkinPart skinPart = saveArmourPart(world, partType, pos, direction, true);
                if (skinPart != null) {
                    parts.add(skinPart);
                }
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
        if (SkinProperties.PROP_BLOCK_BED.getValue(skinProps) & SkinProperties.PROP_BLOCK_SEAT.getValue(skinProps)) {
            throw new SkinSaveException("Skin can not be a bed and a seat.", SkinSaveExceptionType.BED_AND_SEAT);
        }
        
        //Check if multiblock is valid.
        if (skinType == SkinTypeRegistry.skinBlock & SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(skinProps)) {
            SkinPart testPart = saveArmourPart(world, ((SkinBlock)SkinTypeRegistry.skinBlock).partBase, pos, direction, true);
            if (testPart == null) {
                throw new SkinSaveException("Multiblock has no blocks in the yellow area.", SkinSaveExceptionType.INVALID_MULTIBLOCK);
            }
        }
        
        return skin;
    }
    
    private static SkinPart saveArmourPart(World world, ISkinPartType skinPart, BlockPos pos, EnumFacing direction, boolean markerCheck) throws SkinSaveException {
        
        int cubeCount = getNumberOfCubesInPart(world, pos, skinPart);
        if (cubeCount < 1) {
            return null;
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
                    BlockPos target = pos.add(ix + -offset.getX() + buildSpace.getX(), iy + -offset.getY(), iz + offset.getZ() + buildSpace.getZ());
                    
                    BlockPos origin = new BlockPos(-ix + -buildSpace.getX(), -iy + -buildSpace.getY(), -iz + -buildSpace.getZ());
                    
                    int xOrigin = -ix + -buildSpace.getX();
                    int yOrigin = -iy + -buildSpace.getY();
                    int zOrigin = -iz + -buildSpace.getZ();
                    
                    if (!world.isAirBlock(target)) {
                        IBlockState blockState = world.getBlockState(target);
                        if (CubeRegistry.INSTANCE.isBuildingBlock(blockState.getBlock())) {
                            saveArmourBlockToList(world, target,
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
        
        if (markerCheck) {
            if (skinPart.getMinimumMarkersNeeded() > markerBlocks.size()) {
                throw new SkinSaveException("Missing marker for part " + skinPart.getPartName(), SkinSaveExceptionType.MARKER_ERROR);
            }
            
            if (markerBlocks.size() > skinPart.getMaximumMarkersNeeded()) {
                throw new SkinSaveException("Too many markers for part " + skinPart.getPartName(), SkinSaveExceptionType.MARKER_ERROR);
            }
        }

        
        return new SkinPart(cubeData, skinPart, markerBlocks);
    }
    
    private static void saveArmourBlockToList(World world, BlockPos pos, int ix, int iy, int iz, SkinCubeData cubeData, int index, ArrayList<CubeMarkerData> markerBlocks, EnumFacing direction) {
        IBlockState blockState = world.getBlockState(pos);
        if (!CubeRegistry.INSTANCE.isBuildingBlock(blockState.getBlock())) {
            return;
        }
            
        int meta = blockState.getBlock().getMetaFromState(blockState);
        ICubeColour c = BlockUtils.getColourFromTileEntity(world, pos);
        byte cubeType = CubeRegistry.INSTANCE.getCubeFromBlock(blockState.getBlock()).getId();
        
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
    public static void loadSkinIntoWorld(World world, BlockPos pos, Skin skin, EnumFacing direction) {
        ArrayList<SkinPart> parts = skin.getParts();
        
        for (int i = 0; i < parts.size(); i++) {
            loadSkinPartIntoWorld(world, parts.get(i), pos, direction, false);
        }
    }
    
    private static void loadSkinPartIntoWorld(World world, SkinPart partData, BlockPos pos, EnumFacing direction, boolean mirror) {
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
            BlockPos origin = new BlockPos(-offset.getX(), -offset.getY() + -buildSpace.getY(), offset.getZ());
            
            loadSkinBlockIntoWorld(world, pos, origin, blockData, direction, meta, cubeData, i, mirror);
        }
        
    }
    
    private static void loadSkinBlockIntoWorld(World world, BlockPos pos, BlockPos origin, ICube blockData, EnumFacing direction, int meta, SkinCubeData cubeData, int index, boolean mirror) {
        byte[] loc = cubeData.getCubeLocation(index);
        
        int shiftX = -loc[0] - 1;
        int shiftY = loc[1] + 1;
        int shiftZ = loc[2];
        if (mirror) {
            shiftX = loc[0];
        }
        
        BlockPos target = pos.add(shiftX + origin.getX(), origin.getY() - shiftY, shiftZ + origin.getZ());
        
        if (world.getBlockState(target).getBlock() == ModBlocks.BOUNDING_BOX) {
            SyncWorldUpdater.addWorldUpdate(new AsyncWorldUpdateBlock(Blocks.AIR.getDefaultState(), target, world));
            //world.setBlockToAir(target);
            //world.removeTileEntity(target);
        }
        
        Block targetBlock = blockData.getMinecraftBlock();
        IBlockState targetState = targetBlock.getStateFromMeta(meta);
        
        ModLogger.log(targetState);
        
        CubeColour cc = new CubeColour();
        for (int i = 0; i < 6; i++) {
            byte[] c = cubeData.getCubeColour(index, i);
            byte paintType = cubeData.getCubePaintType(index, i);
            if (mirror) {
                if (i == 4) {
                    c = cubeData.getCubeColour(index, 5);
                    paintType = cubeData.getCubePaintType(index, 5);
                }
                if (i == 5) {
                    c = cubeData.getCubeColour(index, 4);
                    paintType = cubeData.getCubePaintType(index, 4);
                }
            }
            cc.setRed(c[0], i);
            cc.setGreen(c[1], i);
            cc.setBlue(c[2], i);
            cc.setPaintType(paintType, i);
        }
        TileEntityColourable colourable = new TileEntityColourable();
        colourable.setColour(cc);
        
        SyncWorldUpdater.addWorldUpdate(new AsyncWorldUpdateBlock(targetState, target, world).setTileEntity(colourable).setOnlyReplaceable(true).setDelay(index / 5));
        //world.setBlockState(target, targetState, 2);
    }
    
    public static void createBoundingBoxes(World world, BlockPos pos, BlockPos parentPos, ISkinType skinType, SkinProperties skinProps) {
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType skinPart = skinType.getSkinParts().get(i);
            createBoundingBoxesForSkinPart(world, pos, parentPos, skinPart, skinProps);
        }
    }
    
    private static void createBoundingBoxesForSkinPart(World world, BlockPos pos, BlockPos parentPos, ISkinPartType skinPart, SkinProperties skinProps) {
        if (skinPart.isModelOverridden(skinProps)) {
            return;
        }
        IRectangle3D buildSpace = skinPart.getBuildingSpace();
        IRectangle3D guideSpace = skinPart.getGuideSpace();
        IPoint3D offset = skinPart.getOffset();
        
        if (guideSpace == null) {
            return;
        }
        
        for (int ix = 0; ix < guideSpace.getWidth(); ix++) {
            for (int iy = 0; iy < guideSpace.getHeight(); iy++) {
                for (int iz = 0; iz < guideSpace.getDepth(); iz++) {
                    BlockPos target = pos.add(
                            ix + -offset.getX() + guideSpace.getX(),
                            iy + -offset.getY() + guideSpace.getY() - buildSpace.getY(),
                            iz + offset.getZ() + guideSpace.getZ());
                    
                    //TODO Set skinPart to left and right legs for skirt.
                    ISkinPartType guidePart = skinPart;
                    byte guideX = (byte) ix;
                    byte guideY = (byte) iy;
                    byte guideZ = (byte) iz;
                    
                    TileEntity te = new TileEntityBoundingBox(parentPos, guideX, guideY, guideZ, guidePart);
                    SyncWorldUpdater.addWorldUpdate(new AsyncWorldUpdateBlock(ModBlocks.BOUNDING_BOX.getDefaultState(), target, world).setTileEntity(te).setOnlyReplaceable(true));
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
                    BlockPos target = pos.add(
                            ix + -offset.getX() + guideSpace.getX(),
                            iy + -offset.getY() + guideSpace.getY() - buildSpace.getY(),
                            iz + offset.getZ() + guideSpace.getZ());
                    
                    if (world.isValid(pos)) {
                        if (world.getBlockState(target).getBlock() == ModBlocks.BOUNDING_BOX) {
                            SyncWorldUpdater.addWorldUpdate(new AsyncWorldUpdateBlock(Blocks.AIR.getDefaultState(), target, world));
                            //world.setBlockToAir(target);
                        }
                    }
                    
                }
            }
        }
    }
    

    public static void copySkinCubes(World world, BlockPos pos, ISkinPartType srcPart, ISkinPartType desPart, boolean mirror) throws SkinSaveException {
        SkinPart skinPart = saveArmourPart(world, srcPart, pos, null, false);
        if (skinPart != null) {
            skinPart.setSkinPart(desPart);
            loadSkinPartIntoWorld(world, skinPart, pos, null, mirror);
        }
    }
    
    public static int clearEquipmentCubes(World world, BlockPos pos, ISkinType skinType, SkinProperties skinProps) {
        return clearEquipmentCubes(world, pos, skinType, skinProps, null);
    }
    
    public static int clearMarkers(World world, BlockPos pos, ISkinType skinType, SkinProperties skinProps, ISkinPartType partType) {
        int blockCount = 0;
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType skinPart = skinType.getSkinParts().get(i);
            if (partType != null) {
                if (partType != skinPart) {
                    continue;
                }
            }
            if (skinType == SkinTypeRegistry.skinBlock) {
                boolean multiblock = SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(skinProps);
                if (skinPart == ((SkinBlock)SkinTypeRegistry.skinBlock).partBase & !multiblock) {
                    blockCount += clearMarkersForSkinPart(world, pos, skinPart);
                }
                if (skinPart == ((SkinBlock)SkinTypeRegistry.skinBlock).partMultiblock & multiblock) {
                    blockCount += clearMarkersForSkinPart(world, pos, skinPart);
                }
            } else {
                blockCount += clearMarkersForSkinPart(world, pos, skinPart);
            }
        }
        return blockCount;
    }
    
    private static int clearMarkersForSkinPart(World world, BlockPos pos, ISkinPartType skinPart) {
        IRectangle3D buildSpace = skinPart.getBuildingSpace();
        IPoint3D offset = skinPart.getOffset();
        int blockCount = 0;
        
        for (int ix = 0; ix < buildSpace.getWidth(); ix++) {
            for (int iy = 0; iy < buildSpace.getHeight(); iy++) {
                for (int iz = 0; iz < buildSpace.getDepth(); iz++) {
                    BlockPos target = pos.add(
                            ix + -offset.getX() + buildSpace.getX(),
                            iy + -offset.getY(),
                            iz + offset.getZ() + buildSpace.getZ());
                    
                    if (world.isValid(target)) {
                        IBlockState state = world.getBlockState(target);
                        if (CubeRegistry.INSTANCE.isBuildingBlock(state.getBlock())) {
                            IBlockState newState = state.getBlock().getStateFromMeta(0);
                            SyncWorldUpdater.addWorldUpdate(new AsyncWorldUpdateBlock(newState, target, world).setOnlyReplaceable(true));
                        }
                    }
                    
                }
            }
        }
        return blockCount;
    }
    
    public static int clearEquipmentCubes(World world, BlockPos pos, ISkinType skinType, SkinProperties skinProps, ISkinPartType partType) {
        int blockCount = 0;
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType skinPart = skinType.getSkinParts().get(i);
            if (partType != null) {
                if (partType != skinPart) {
                    continue;
                }
            }
            if (skinType == SkinTypeRegistry.skinBlock) {
                boolean multiblock = SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(skinProps);
                if (skinPart == ((SkinBlock)SkinTypeRegistry.skinBlock).partBase & !multiblock) {
                    blockCount += clearEquipmentCubesForSkinPart(world, pos, skinPart);
                }
                if (skinPart == ((SkinBlock)SkinTypeRegistry.skinBlock).partMultiblock & multiblock) {
                    blockCount += clearEquipmentCubesForSkinPart(world, pos, skinPart);
                }
            } else {
                blockCount += clearEquipmentCubesForSkinPart(world, pos, skinPart);
            }
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
                    BlockPos target = pos.add(
                            ix + -offset.getX() + buildSpace.getX(),
                            iy + -offset.getY(),
                            iz + offset.getZ() + buildSpace.getZ());
                    
                    if (world.isValid(target)) {
                        IBlockState state = world.getBlockState(target);
                        Block block = state.getBlock();
                        if (CubeRegistry.INSTANCE.isBuildingBlock(block)) {
                            SyncWorldUpdater.addWorldUpdate(new AsyncWorldUpdateBlock(Blocks.AIR.getDefaultState(), target, world).setDelay(blockCount / 5));
                            //world.setBlockToAir(target);
                            //world.removeTileEntity(target);
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
                    BlockPos target = pos.add(
                            ix + -offset.getX() + buildSpace.getX(),
                            iy + -offset.getY(),
                            iz + offset.getZ() + buildSpace.getZ());
                    
                    if (world.isValid(target)) {
                        IBlockState state = world.getBlockState(target);
                        if (CubeRegistry.INSTANCE.isBuildingBlock(state.getBlock())) {
                            blList.add(new BlockPos(target.getX(), target.getY(), target.getZ()));
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

                    BlockPos target = pos.add(ix + -offset.getX() + buildSpace.getX(), iy + -offset.getY(), iz + offset.getZ() + buildSpace.getZ());
                    
                    if (world.isValid(target)) {
                        IBlockState blockState = world.getBlockState(target);
                        if (CubeRegistry.INSTANCE.isBuildingBlock(blockState.getBlock())) {
                            cubeCount++;
                        }
                    }
                }
            }
        }
        return cubeCount;
    }
}
