package moe.plushie.armourers_workshop.builder.world;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.api.skin.*;
import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeData;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.data.serialize.SkinSerializer;
import moe.plushie.armourers_workshop.core.skin.exception.SkinSaveException;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.utils.OptionalDirection;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import moe.plushie.armourers_workshop.utils.SkinPaintData;
import moe.plushie.armourers_workshop.utils.SkyBox;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Helper class for converting back and forth from
 * in world blocks to skin classes.
 * <p>
 * Note: Minecraft models are inside out, blocks are
 * flipped when loading and saving.
 *
 * @author RiskyKen
 */
public final class WorldUtils {

    /**
     * Converts blocks in the world into a skin class.
     *
     * @param world     The world.
     * @param skinProps The skin properties for this skin.
     * @param skinType  The type of skin to save.
     * @param paintData Paint data for this skin.
     * @param pos       the armourer location.
     * @param direction the armourer is facing.
     * @return
     * @throws SkinSaveException
     */
    public static Skin saveSkinFromWorld(World world, SkinProperties skinProps, ISkinType skinType, SkinPaintData paintData, BlockPos pos, Direction direction) throws SkinSaveException {

        ArrayList<SkinPart> parts = new ArrayList<>();

        if (skinType == SkinTypes.BLOCK) {
            ISkinPartType partType = SkinPartTypes.BLOCK;
            if (skinProps.get(SkinProperty.BLOCK_MULTIBLOCK)) {
                partType = SkinPartTypes.BLOCK_MULTI;
            }
            SkinPart skinPart = saveArmourPart(world, partType, pos, direction, true);
            if (skinPart != null) {
                parts.add(skinPart);
            }
        } else {
            for (ISkinPartType partType : skinType.getParts()) {
                SkinPart skinPart = saveArmourPart(world, partType, pos, direction, true);
                if (skinPart != null) {
                    parts.add(skinPart);
                }
            }
        }

        if (paintData != null) {
            SkinPaintData resolvedPaintData = SkinPaintData.v1();
            resolvedPaintData.copyFrom(paintData);
            paintData = resolvedPaintData;
        }

        Skin skin = SkinSerializer.makeSkin(skinType, skinProps, paintData, parts);

        // check if there are any blocks in the build guides.
        if (skin.getParts().size() == 0 && skin.getPaintData() == null) {
            throw new SkinSaveException("Nothing to save.", SkinSaveException.SkinSaveExceptionType.NO_DATA);
        }

        // check if the skin has all needed parts.
        for (ISkinPartType partType : skinType.getParts()) {
            if (partType.isPartRequired()) {
                boolean havePart = false;
                for (ISkinPart part : skin.getParts()) {
                    if (partType == part.getType()) {
                        havePart = true;
                        break;
                    }
                }
                if (!havePart) {
                    throw new SkinSaveException("Skin is missing part " + partType.getRegistryName(), SkinSaveException.SkinSaveExceptionType.MISSING_PARTS);
                }
            }
        }

        // check if the skin is not a seat and a bed.
        if (skinProps.get(SkinProperty.BLOCK_BED) && skinProps.get(SkinProperty.BLOCK_SEAT)) {
            throw new SkinSaveException("Skin can not be a bed and a seat.", SkinSaveException.SkinSaveExceptionType.BED_AND_SEAT);
        }

        // check if multi-block is valid.
        if (skinType == SkinTypes.BLOCK && skinProps.get(SkinProperty.BLOCK_MULTIBLOCK)) {
            SkinPart testPart = saveArmourPart(world, SkinPartTypes.BLOCK, pos, direction, true);
            if (testPart == null) {
                throw new SkinSaveException("Multiblock has no blocks in the yellow area.", SkinSaveException.SkinSaveExceptionType.INVALID_MULTIBLOCK);
            }
        }

        return skin;
    }

    private static SkinPart saveArmourPart(World world, ISkinPartType skinPart, BlockPos pos, Direction direction, boolean markerCheck) throws SkinSaveException {

        int cubeCount = getNumberOfCubesInPart(world, pos, skinPart);
        if (cubeCount < 1) {
            return null;
        }
        SkinCubeData cubeData = new SkinCubeData();
        cubeData.setCubeCount(cubeCount);

        ArrayList<SkinMarker> markerBlocks = new ArrayList<>();

        Rectangle3i buildSpace = skinPart.getBuildingSpace();
        Vector3i offset = skinPart.getOffset();

        int i = 0;
        for (int ix = 0; ix < buildSpace.getWidth(); ix++) {
            for (int iy = 0; iy < buildSpace.getHeight(); iy++) {
                for (int iz = 0; iz < buildSpace.getDepth(); iz++) {
                    BlockPos target = pos.offset(
                            ix + -offset.getX() + buildSpace.getX(),
                            iy + -offset.getY(),
                            iz + offset.getZ() + buildSpace.getZ());

//                    BlockPos origin = new BlockPos(-ix + -buildSpace.getX(), -iy + -buildSpace.getY(), -iz + -buildSpace.getZ());

                    int xOrigin = -ix + -buildSpace.getX();
                    int yOrigin = -iy + -buildSpace.getY();
                    int zOrigin = -iz + -buildSpace.getZ();

                    BlockState targetState = world.getBlockState(target);
                    if (targetState.getBlock() instanceof SkinCubeBlock) {
                        saveArmourBlockToList(world, target,
                                xOrigin - 1,
                                yOrigin - 1,
                                -zOrigin,
                                cubeData.at(i), markerBlocks, direction);
                        i++;
                    }
                }
            }
        }

        if (markerCheck) {
            if (skinPart.getMinimumMarkersNeeded() > markerBlocks.size()) {
                throw new SkinSaveException("Missing marker for part " + skinPart.getRegistryName(), SkinSaveException.SkinSaveExceptionType.MARKER_ERROR);
            }

            if (markerBlocks.size() > skinPart.getMaximumMarkersNeeded()) {
                throw new SkinSaveException("Too many markers for part " + skinPart.getRegistryName(), SkinSaveException.SkinSaveExceptionType.MARKER_ERROR);
            }
        }

        return new SkinPart(skinPart, markerBlocks, cubeData);
    }

    private static void saveArmourBlockToList(World world, BlockPos pos, int ix, int iy, int iz, SkinCubeData.BufferSlice slice, ArrayList<SkinMarker> markerBlocks, Direction direction) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (!(tileEntity instanceof IPaintable)) {
            return;
        }
        BlockState blockState = tileEntity.getBlockState();
        IPaintable target = (IPaintable) tileEntity;
        ISkinCube cube = SkinCubes.byBlock(blockState.getBlock());

        OptionalDirection marker = blockState.getValue(SkinCubeBlock.MARKER);

        slice.setId((byte) cube.getId());
        slice.setX((byte) ix);
        slice.setY((byte) iy);
        slice.setZ((byte) iz);

        for (Direction dir : Direction.values()) {
            IPaintColor color = target.getColor(dir);
            slice.setRGB(dir.ordinal(), color.getRGB());
            slice.setPaintType(dir.ordinal(), (byte) color.getPaintType().getId());
        }
        if (marker != OptionalDirection.NONE) {
            markerBlocks.add(new SkinMarker((byte) ix, (byte) iy, (byte) iz, (byte) marker.ordinal()));
        }
    }

    /**
     * Converts a skin class into blocks in the world.
     *
     * @param world     The world.
     * @param pos       the armourer location.
     * @param skin      The skin to load.
     * @param direction The direction the armourer is facing.
     */
    public static void loadSkinIntoWorld(World world, BlockPos pos, Skin skin, Direction direction) {
        for (SkinPart part : skin.getParts()) {
            loadSkinPartIntoWorld(world, part, pos, direction, false);
        }
    }

    private static void loadSkinPartIntoWorld(World world, SkinPart partData, BlockPos pos, Direction direction, boolean mirror) {
        ISkinPartType skinPart = partData.getType();
        Rectangle3i buildSpace = skinPart.getBuildingSpace();
        Vector3i offset = skinPart.getOffset();
        SkinCubeData cubeData = partData.getCubeData();

        for (int i = 0; i < cubeData.getCubeCount(); i++) {
            SkinCubeData.BufferSlice slice = cubeData.at(i);
            Vector3i cubePos = slice.getPos();
            ISkinCube blockData = SkinCubes.byId(slice.getId());
            OptionalDirection markerFacing = OptionalDirection.NONE;
            for (ISkinMarker marker : partData.getMarkers()) {
                if (cubePos.equals(marker.getPosition())) {
                    markerFacing = OptionalDirection.of(marker.getDirection());
                    break;
                }
            }
            BlockPos origin = new BlockPos(-offset.getX(), -offset.getY() + -buildSpace.getY(), offset.getZ());
            loadSkinBlockIntoWorld(world, pos, origin, blockData, cubePos, direction, markerFacing, slice, mirror);
        }
    }

    private static void loadSkinBlockIntoWorld(World world, BlockPos pos, BlockPos origin, ISkinCube blockData, Vector3i cubePos, Direction direction, OptionalDirection markerFacing, SkinCubeData.BufferSlice slice, boolean mirror) {
        int shiftX = -cubePos.getX() - 1;
        int shiftY = cubePos.getY() + 1;
        int shiftZ = cubePos.getZ();
        if (mirror) {
            shiftX = cubePos.getX();
        }

        BlockPos target = pos.offset(shiftX + origin.getX(), origin.getY() - shiftY, shiftZ + origin.getZ());

        if (world.getBlockState(target).is(ModBlocks.BOUNDING_BOX)) {
            WorldUpdater.getInstance().submit(new WorldBlockUpdateTask(world, target, Blocks.AIR.defaultBlockState()));
            //world.setBlockToAir(target);
            //world.removeTileEntity(target);
        }

        Block targetBlock = blockData.getBlock();
        BlockState targetState = targetBlock.defaultBlockState().setValue(SkinCubeBlock.MARKER, markerFacing);

        HashMap<Direction, IPaintColor> colors = new HashMap<>();
        for (Direction dir : Direction.values()) {
            int rgb = slice.getRGB(dir.ordinal());
            int type = slice.getPaintType(dir.ordinal());
            PaintColor color = PaintColor.of(rgb, SkinPaintTypes.byId(type));
            if (color.getPaintType() == SkinPaintTypes.NONE) {
                continue;
            }
            colors.put(dir, color);
        }

        WorldBlockUpdateTask task = new WorldBlockUpdateTask(world, target, targetState);
        task.modifier = state -> {
            TileEntity tileEntity = world.getBlockEntity(target);
            if (tileEntity instanceof IPaintable) {
                ((IPaintable) tileEntity).setColors(colors);
            }
        };
//        .setOnlyReplaceable(true)
//        .setDelay(index / 5)
        WorldUpdater.getInstance().submit(task);
    }


//    public static void apply(ISkinType skinType, @Nullable Predicate<ISkinPartType> predicate, BiFunction<BlockPos, Vector3i, WorldBlockUpdateTask> transform) {
//        for (ISkinPartType partType : skinType.getParts()) {
//            // when part rejected, we don't have to apply it.
//            if (predicate != null && !predicate.test(partType)) {
//                continue;
//            }
//            Vector3i offset = partType.getOffset();
//            Rectangle3i buildRect = partType.getBuildingSpace();
//            Rectangle3i guideRect = partType.getGuideSpace();
//            for (int ix = 0; ix < guideRect.getWidth(); ix++) {
//                for (int iy = 0; iy < guideRect.getHeight(); iy++) {
//                    for (int iz = 0; iz < guideRect.getDepth(); iz++) {
//                        int tx = ix - offset.getX() + guideRect.getX();
//                        int ty = iy - offset.getY() + guideRect.getY() - buildRect.getY();
//                        int tz = iz + offset.getZ() + guideRect.getZ();
//                        WorldBlockUpdateTask task = transform.apply(new BlockPos(tx, ty, tz), new Vector3i(ix, iy, iz));
//                        if (task != null) {
//                            WorldUpdater.getInstance().submit(task);
//                        }
//                    }
//                }
//            }
//        }
//    }

//    public static void createBoundingBoxes(World world, BlockPos pos, BlockPos parentPos, ISkinType skinType, SkinProperties skinProps) {
//        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
//            ISkinPartType skinPart = skinType.getSkinParts().get(i);
//            createBoundingBoxesForSkinPart(world, pos, parentPos, skinPart, skinProps);
//        }
//    }
//
//    private static void createBoundingBoxesForSkinPart(World world, BlockPos pos, BlockPos parentPos, ISkinPartType skinPart, SkinProperties skinProps) {
//        if (skinPart.isModelOverridden(skinProps)) {
//            return;
//        }
//        IRectangle3D buildSpace = skinPart.getBuildingSpace();
//        IRectangle3D guideSpace = skinPart.getGuideSpace();
//        IPoint3D offset = skinPart.getOffset();
//
//        if (guideSpace == null) {
//            return;
//        }
//
//        for (int ix = 0; ix < guideSpace.getWidth(); ix++) {
//            for (int iy = 0; iy < guideSpace.getHeight(); iy++) {
//                for (int iz = 0; iz < guideSpace.getDepth(); iz++) {
//                    BlockPos target = pos.add(
//                            ix + -offset.getX() + guideSpace.getX(),
//                            iy + -offset.getY() + guideSpace.getY() - buildSpace.getY(),
//                            iz + offset.getZ() + guideSpace.getZ());
//
//                    //TODO Set skinPart to left and right legs for skirt.
//                    ISkinPartType guidePart = skinPart;
//                    byte guideX = (byte) ix;
//                    byte guideY = (byte) iy;
//                    byte guideZ = (byte) iz;
//
//                    TileEntity te = new TileEntityBoundingBox(parentPos, guideX, guideY, guideZ, guidePart);
//                    SyncWorldUpdater.addWorldUpdate(new AsyncWorldUpdateBlock(ModBlocks.BOUNDING_BOX.getDefaultState(), target, world).setTileEntity(te).setOnlyReplaceable(true));
//                }
//            }
//        }
//    }
//
//    public static void removeBoundingBoxes(World world, BlockPos pos, ISkinType skinType) {
//        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
//            ISkinPartType skinPart = skinType.getSkinParts().get(i);
//            removeBoundingBoxesForSkinPart(world, pos, skinPart);
//        }
//    }
//
//    private static void removeBoundingBoxesForSkinPart(World world, BlockPos pos, ISkinPartType skinPart) {
//        IRectangle3D buildSpace = skinPart.getBuildingSpace();
//        IRectangle3D guideSpace = skinPart.getGuideSpace();
//        IPoint3D offset = skinPart.getOffset();
//
//        if (guideSpace == null) {
//            return;
//        }
//
//        for (int ix = 0; ix < guideSpace.getWidth(); ix++) {
//            for (int iy = 0; iy < guideSpace.getHeight(); iy++) {
//                for (int iz = 0; iz < guideSpace.getDepth(); iz++) {
//                    BlockPos target = pos.add(
//                            ix + -offset.getX() + guideSpace.getX(),
//                            iy + -offset.getY() + guideSpace.getY() - buildSpace.getY(),
//                            iz + offset.getZ() + guideSpace.getZ());
//
//                    if (world.isValid(pos)) {
//                        if (world.getBlockState(target).getBlock() == ModBlocks.BOUNDING_BOX) {
//                            SyncWorldUpdater.addWorldUpdate(new AsyncWorldUpdateBlock(Blocks.AIR.getDefaultState(), target, world));
//                            //world.setBlockToAir(target);
//                        }
//                    }
//
//                }
//            }
//        }
//    }

    public static void copyPaintData(SkinPaintData paintData, SkyBox srcBox, SkyBox destBox, boolean isMirrorX) {
        int srcX = srcBox.getBounds().getX();
        int srcY = srcBox.getBounds().getY();
        int srcZ = srcBox.getBounds().getZ();
        int destX = destBox.getBounds().getX();
        int destY = destBox.getBounds().getY();
        int destZ = destBox.getBounds().getZ();
        int destWidth = destBox.getBounds().getWidth();
        HashMap<Point, Integer> colors = new HashMap<>();
        srcBox.forEach((texture, x, y, z, dir) -> {
            int ix = x - srcX;
            int iy = y - srcY;
            int iz = z - srcZ;
            if (isMirrorX) {
                ix = destWidth - ix - 1;
                if (dir.getAxis() == Direction.Axis.X) {
                    dir = dir.getOpposite();
                }
            }
            Point newTexture = destBox.get(ix + destX, iy + destY, iz + destZ, dir);
            if (newTexture == null) {
                return;
            }
            int color = paintData.getColor(texture);
            if (PaintColor.isOpaque(color)) {
                // a special case is to use the mirror to swap the part texture,
                // we will copy the color to the map and then applying it when read finish.
                colors.put(newTexture, color);
            }
        });
        colors.forEach(paintData::setColor);
    }

    public static void clearPaintData(SkinPaintData paintData, SkyBox srcBox) {
        srcBox.forEach((texture, x, y, z, dir) -> paintData.setColor(texture.x, texture.y, 0));
    }

    public static void copyCubes(World world, BlockPos pos, ISkinType skinType, SkinProperties skinProps, Direction direction, ISkinPartType srcPart, ISkinPartType destPart, boolean mirror) throws SkinSaveException {
        SkinPart skinPart = saveArmourPart(world, srcPart, pos, null, false);
        if (skinPart != null) {
            skinPart.setSkinPart(destPart);
            loadSkinPartIntoWorld(world, skinPart, pos, null, mirror);
        }
    }

    public static int clearMarkers(World world, BlockPos pos, ISkinType skinType, SkinProperties skinProps, Direction direction, ISkinPartType partType) {
        int blockCount = 0;
        for (ISkinPartType skinPart : skinType.getParts()) {
            if (partType != SkinPartTypes.UNKNOWN) {
                if (partType != skinPart) {
                    continue;
                }
            }
            if (skinType == SkinTypes.BLOCK) {
                boolean multiblock = skinProps.get(SkinProperty.BLOCK_MULTIBLOCK);
                if (skinPart == SkinPartTypes.BLOCK && !multiblock) {
                    blockCount += clearMarkersForSkinPart(world, pos, skinPart);
                }
                if (skinPart == SkinPartTypes.BLOCK_MULTI && multiblock) {
                    blockCount += clearMarkersForSkinPart(world, pos, skinPart);
                }
            } else {
                blockCount += clearMarkersForSkinPart(world, pos, skinPart);
            }
        }
        return blockCount;
    }

    private static int clearMarkersForSkinPart(World world, BlockPos pos, ISkinPartType skinPart) {
        int blockCount = 0;
        for (Vector3i offset : getResolvedBuildingSpace(skinPart)) {
            BlockPos target = pos.offset(offset);
            BlockState targetState = world.getBlockState(target);
            if (targetState.hasProperty(SkinCubeBlock.MARKER) && targetState.getValue(SkinCubeBlock.MARKER) != OptionalDirection.NONE) {
                BlockState newState = targetState.setValue(SkinCubeBlock.MARKER, OptionalDirection.NONE);
                WorldBlockUpdateTask task = new WorldBlockUpdateTask(world, target, newState);
                // task.setOnlyReplaceable(true)
                WorldUpdater.getInstance().submit(task);
                blockCount++;
            }
        }
        return blockCount;
    }

    public static int clearCubes(World world, BlockPos pos, ISkinType skinType, SkinProperties skinProps, Direction direction, ISkinPartType partType) {
        int blockCount = 0;
        for (ISkinPartType skinPart : skinType.getParts()) {
            if (partType != SkinPartTypes.UNKNOWN) {
                if (partType != skinPart) {
                    continue;
                }
            }
            if (skinType == SkinTypes.BLOCK) {
                boolean multiblock = skinProps.get(SkinProperty.BLOCK_MULTIBLOCK);
                if (skinPart == SkinPartTypes.BLOCK && !multiblock) {
                    blockCount += clearEquipmentCubesForSkinPart(world, pos, skinPart);
                }
                if (skinPart == SkinPartTypes.BLOCK_MULTI && multiblock) {
                    blockCount += clearEquipmentCubesForSkinPart(world, pos, skinPart);
                }
            } else {
                blockCount += clearEquipmentCubesForSkinPart(world, pos, skinPart);
            }
        }
        return blockCount;
    }

    private static int clearEquipmentCubesForSkinPart(World world, BlockPos pos, ISkinPartType skinPart) {
        int blockCount = 0;
        for (Vector3i offset : getResolvedBuildingSpace(skinPart)) {
            BlockPos target = pos.offset(offset);
            BlockState targetState = world.getBlockState(target);
            if (targetState.getBlock() instanceof SkinCubeBlock) {
                WorldUpdater.getInstance().submit(new WorldBlockUpdateTask(world, target, Blocks.AIR.defaultBlockState()));
                //world.setBlockToAir(target);
                //world.removeTileEntity(target);
                blockCount++;
            }
        }
        return blockCount;
    }

    private static Iterable<Vector3i> getResolvedBuildingSpace(ISkinPartType skinPart) {
        Vector3i origin = skinPart.getOffset();
        Rectangle3i buildSpace = skinPart.getBuildingSpace();
        int dx = -origin.getX() + buildSpace.getX();
        int dy = -origin.getY();
        int dz = origin.getZ() + buildSpace.getZ();
        return new Rectangle3i(dx, dy, dz, buildSpace.getWidth(), buildSpace.getHeight(), buildSpace.getDepth()).enumerateZYX();
    }



    //    public static ArrayList<BlockPos> getListOfPaintableCubes(World world, BlockPos pos, ISkinType skinType) {
//        ArrayList<BlockPos> blList = new ArrayList<BlockPos>();
//        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
//            ISkinPartType skinPart = skinType.getSkinParts().get(i);
//            getBuildingCubesForPart(world, pos, skinPart, blList);
//        }
//        return blList;
//    }

    private static int getNumberOfCubesInPart(World world, BlockPos pos, ISkinPartType skinPart) {
        int cubeCount = 0;
        for (Vector3i offset : getResolvedBuildingSpace(skinPart)) {
            BlockPos target = pos.offset(offset);
            BlockState blockState = world.getBlockState(target);
            if (blockState.getBlock() instanceof SkinCubeBlock) {
                cubeCount++;
            }
        }
        return cubeCount;
    }
}
