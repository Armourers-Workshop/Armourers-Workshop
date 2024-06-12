package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.api.math.IRectangle3i;
import moe.plushie.armourers_workshop.api.math.ITexturePos;
import moe.plushie.armourers_workshop.api.math.IVector3i;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeProvider;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.api.skin.ISkinPart;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.core.data.OptionalDirection;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCube;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeTypes;
import moe.plushie.armourers_workshop.core.skin.cube.impl.SkinCubesV0;
import moe.plushie.armourers_workshop.core.skin.exception.SkinSaveException;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.ModBlocks;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;
import moe.plushie.armourers_workshop.utils.texture.SkyBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

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
     * @param level     The world.
     * @param transform the armourer transform.
     * @param skinProps The skin properties for this skin.
     * @param skinType  The type of skin to save.
     * @param paintData Paint data for this skin.
     */
    public static Skin saveSkinFromWorld(Level level, CubeTransform transform, SkinProperties skinProps, ISkinType skinType, SkinPaintData paintData) throws SkinSaveException {

        ArrayList<SkinPart> parts = new ArrayList<>();

        if (skinType == SkinTypes.BLOCK) {
            ISkinPartType partType = SkinPartTypes.BLOCK;
            if (skinProps.get(SkinProperty.BLOCK_MULTIBLOCK)) {
                partType = SkinPartTypes.BLOCK_MULTI;
            }
            SkinPart skinPart = saveArmourPart(level, transform, partType, true);
            if (skinPart != null) {
                parts.add(skinPart);
            }
        } else {
            for (ISkinPartType partType : skinType.getParts()) {
                SkinPart skinPart = saveArmourPart(level, transform, partType, true);
                if (skinPart != null) {
                    parts.add(skinPart);
                }
            }
        }
        // TODO: support v2 texture
        // because old skin not support v2 texture format,
        // so downgrade v2 to v1 texture format.
        if (paintData != null) {
            SkinPaintData resolvedPaintData = SkinPaintData.v1();
            resolvedPaintData.copyFrom(paintData);
            paintData = resolvedPaintData;
        }

        Skin.Builder builder = new Skin.Builder(skinType);
        builder.properties(skinProps);
        builder.paintData(paintData);
        builder.parts(parts);
        Skin skin = builder.build();

        // check if there are any blocks in the build guides.
        if (skin.getParts().isEmpty() && skin.getPaintData() == null) {
            throw SkinSaveException.Type.NO_DATA.build("noting");
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
                    throw SkinSaveException.Type.MISSING_PARTS.build("missingPart", TranslateUtils.Name.of(partType));
                }
            }
        }

        // check if the skin is not a seat and a bed.
        if (skinProps.get(SkinProperty.BLOCK_BED) && skinProps.get(SkinProperty.BLOCK_SEAT)) {
            throw SkinSaveException.Type.BED_AND_SEAT.build("conflictBedSeat");
        }

        // check if multi-block is valid.
        if (skinType == SkinTypes.BLOCK && skinProps.get(SkinProperty.BLOCK_MULTIBLOCK)) {
            SkinPart testPart = saveArmourPart(level, transform, SkinPartTypes.BLOCK, true);
            if (testPart == null) {
                throw SkinSaveException.Type.INVALID_MULTIBLOCK.build("missingMainBlock");
            }
        }

        return skin;
    }

    private static SkinPart saveArmourPart(Level level, CubeTransform transform, ISkinPartType partType, boolean markerCheck) throws SkinSaveException {

        int cubeCount = getNumberOfCubesInPart(level, transform, partType);
        if (cubeCount < 1) {
            return null;
        }
        SkinCubesV0 cubeData = new SkinCubesV0(cubeCount);
        ArrayList<SkinMarker> markerBlocks = new ArrayList<>();

        IRectangle3i buildSpace = partType.getBuildingSpace();
        IVector3i offset = partType.getOffset();

        int i = 0;
        for (int ix = 0; ix < buildSpace.getWidth(); ix++) {
            for (int iy = 0; iy < buildSpace.getHeight(); iy++) {
                for (int iz = 0; iz < buildSpace.getDepth(); iz++) {
                    BlockPos target = transform.mul(
                            ix + -offset.getX() + buildSpace.getX(),
                            iy + -offset.getY(),
                            iz + offset.getZ() + buildSpace.getZ());

//                    BlockPos origin = new BlockPos(-ix + -buildSpace.getX(), -iy + -buildSpace.getY(), -iz + -buildSpace.getZ());

                    int xOrigin = -ix + -buildSpace.getX();
                    int yOrigin = -iy + -buildSpace.getY();
                    int zOrigin = -iz + -buildSpace.getZ();

                    BlockState targetState = level.getBlockState(target);
                    if (targetState.getBlock() instanceof SkinCubeBlock) {
                        saveArmourBlockToList(level, transform, target,
                                xOrigin - 1,
                                yOrigin - 1,
                                -zOrigin,
                                cubeData.getCube(i), markerBlocks);
                        i++;
                    }
                }
            }
        }

        if (markerCheck) {
            if (partType.getMinimumMarkersNeeded() > markerBlocks.size()) {
                throw SkinSaveException.Type.MARKER_ERROR.build("missingMarker", TranslateUtils.Name.of(partType));
            }

            if (markerBlocks.size() > partType.getMaximumMarkersNeeded()) {
                throw SkinSaveException.Type.MARKER_ERROR.build("tooManyMarkers", TranslateUtils.Name.of(partType));
            }
        }

        SkinPart.Builder builder = new SkinPart.Builder(partType);
        builder.cubes(cubeData);
        builder.markers(markerBlocks);
        return builder.build();
    }

    private static void saveArmourBlockToList(Level level, CubeTransform transform, BlockPos pos, int ix, int iy, int iz, SkinCube cube, ArrayList<SkinMarker> markerBlocks) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof IPaintable target)) {
            return;
        }
        BlockState blockState = blockEntity.getBlockState();
        OptionalDirection marker = SkinCubeBlock.getMarker(blockState);

        cube.setType(SkinCubeTypes.byBlock(blockState.getBlock()));
        cube.setPosition(new Vector3i(ix, iy, iz));
        for (Direction dir : Direction.values()) {
            IPaintColor paintColor = target.getColor(dir);
            Direction resolvedDir = transform.invRotate(dir);
            cube.setPaintColor(resolvedDir, paintColor);
        }
        if (marker != OptionalDirection.NONE) {
            OptionalDirection resolvedMarker = OptionalDirection.of(transform.invRotate(marker.getDirection()));
            markerBlocks.add(new SkinMarker((byte) ix, (byte) iy, (byte) iz, (byte) resolvedMarker.ordinal()));
        }
    }

    /**
     * Converts a skin class into blocks in the world.
     *
     * @param collector The world collector.
     * @param transform The armourer transform.
     * @param skin      The skin to load.
     */
    public static void loadSkinIntoWorld(CubeChangesCollector collector, CubeTransform transform, Skin skin) {
        for (SkinPart part : skin.getParts()) {
            loadSkinPartIntoWorld(collector, transform, part, false);
        }
    }

    private static void loadSkinPartIntoWorld(CubeChangesCollector collector, CubeTransform transform, SkinPart partData, boolean mirror) {
        ISkinPartType skinPart = partData.getType();
        IRectangle3i buildSpace = skinPart.getBuildingSpace();
        IVector3i offset = skinPart.getOffset();
        ISkinCubeProvider cubeData = partData.getCubeData();

        for (int i = 0; i < cubeData.getCubeTotal(); i++) {
            var cube = cubeData.getCube(i);
            var cubePos = cube.getPosition();
            var blockData = cube.getType();
            var markerFacing = OptionalDirection.NONE;
            for (var marker : partData.getMarkers()) {
                if (cubePos.equals(marker.getPosition())) {
                    var resolvedMarker = getResolvedDirection(marker.getDirection(), mirror);
                    markerFacing = OptionalDirection.of(transform.rotate(resolvedMarker));
                    break;
                }
            }
            var origin = new BlockPos(-offset.getX(), -offset.getY() + -buildSpace.getY(), offset.getZ());
            loadSkinBlockIntoWorld(collector, transform, origin, blockData, cubePos, markerFacing, cube, mirror);
        }
    }

    private static void loadSkinBlockIntoWorld(CubeChangesCollector collector, CubeTransform transform, BlockPos origin, ISkinCubeType blockData, IVector3i cubePos, OptionalDirection markerFacing, ISkinCube cube, boolean mirror) {
        var shiftX = -cubePos.getX() - 1;
        var shiftY = cubePos.getY() + 1;
        var shiftZ = cubePos.getZ();
        if (mirror) {
            shiftX = cubePos.getX();
        }

        var target = transform.mul(shiftX + origin.getX(), origin.getY() - shiftY, shiftZ + origin.getZ());
        var targetCube = collector.getCube(target);

        if (targetCube.is(ModBlocks.BOUNDING_BOX.get())) {
            targetCube.setBlockStateAndTag(Blocks.AIR.defaultBlockState(), null);
        }

        var targetBlock = blockData.getBlock();
        var targetState = SkinCubeBlock.setMarker(targetBlock.defaultBlockState(), markerFacing);

        HashMap<Direction, IPaintColor> colors = new HashMap<>();
        for (var dir : Direction.values()) {
            var paintColor = cube.getPaintColor(dir);
            var resolvedDir = getResolvedDirection(dir, mirror);
            colors.put(transform.rotate(resolvedDir), paintColor);
        }

        targetCube.setBlockStateAndColors(targetState, colors);
    }

    public static void copyPaintData(SkinPaintData paintData, SkyBox srcBox, SkyBox destBox, boolean isMirrorX) {
        int srcX = srcBox.getBounds().getX();
        int srcY = srcBox.getBounds().getY();
        int srcZ = srcBox.getBounds().getZ();
        int destX = destBox.getBounds().getX();
        int destY = destBox.getBounds().getY();
        int destZ = destBox.getBounds().getZ();
        int destWidth = destBox.getBounds().getWidth();
        HashMap<ITexturePos, Integer> colors = new HashMap<>();
        srcBox.forEach((texture, x, y, z, dir) -> {
            int ix = x - srcX;
            int iy = y - srcY;
            int iz = z - srcZ;
            if (isMirrorX) {
                ix = destWidth - ix - 1;
                dir = getResolvedDirection(dir, true);
            }
            var newTexture = destBox.get(ix + destX, iy + destY, iz + destZ, dir);
            if (newTexture == null) {
                return;
            }
            var color = paintData.getColor(texture);
            if (PaintColor.isOpaque(color)) {
                // a special case is to use the mirror to swap the part texture,
                // we will copy the color to the map and then applying it when read finish.
                colors.put(newTexture, color);
            }
        });
        colors.forEach(paintData::setColor);
    }

    public static void clearPaintData(SkinPaintData paintData, SkyBox srcBox) {
        srcBox.forEach((texturePos, x, y, z, dir) -> paintData.setColor(texturePos, 0));
    }

    public static void replaceCubes(CubeChangesCollector collector, CubeTransform transform, ISkinType skinType, SkinProperties skinProps, CubeReplacingEvent event) {
        for (var skinPart : skinType.getParts()) {
            for (Vector3i offset : getResolvedBuildingSpace2(skinPart)) {
                replaceCube(collector, transform.mul(offset), event);
            }
        }
    }

    public static void replaceCube(CubeChangesCollector collector, BlockPos pos, CubeReplacingEvent event) {
        var cube = collector.getCube(pos);
        if (event.accept(cube)) {
            event.apply(cube);
        }
    }

    public static void copyCubes(CubeChangesCollector collector, CubeTransform transform, ISkinType skinType, SkinProperties skinProps, ISkinPartType srcPart, ISkinPartType destPart, boolean mirror) throws SkinSaveException {
        SkinPart skinPart = saveArmourPart(collector.getLevel(), transform, srcPart, false);
        if (skinPart != null) {
            skinPart.setSkinPart(destPart);
            loadSkinPartIntoWorld(collector, transform, skinPart, mirror);
        }
    }

    public static int clearMarkers(CubeChangesCollector collector, CubeTransform transform, ISkinType skinType, SkinProperties skinProps, ISkinPartType partType) {
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
                    blockCount += clearMarkersForSkinPart(collector, transform, skinPart);
                }
                if (skinPart == SkinPartTypes.BLOCK_MULTI && multiblock) {
                    blockCount += clearMarkersForSkinPart(collector, transform, skinPart);
                }
            } else {
                blockCount += clearMarkersForSkinPart(collector, transform, skinPart);
            }
        }
        return blockCount;
    }

    private static int clearMarkersForSkinPart(CubeChangesCollector collector, CubeTransform transform, ISkinPartType skinPart) {
        int blockCount = 0;
        for (Vector3i offset : getResolvedBuildingSpace2(skinPart)) {
            var cube = collector.getCube(transform.mul(offset));
            var targetState = cube.getBlockState();
            if (targetState.hasProperty(SkinCubeBlock.MARKER) && SkinCubeBlock.getMarker(targetState) != OptionalDirection.NONE) {
                cube.setBlockState(SkinCubeBlock.setMarker(targetState, OptionalDirection.NONE));
                blockCount++;
            }
        }
        return blockCount;
    }

    public static int clearCubes(CubeChangesCollector collector, CubeTransform transform, ISkinType skinType, SkinProperties skinProps, ISkinPartType partType) {
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
                    blockCount += clearEquipmentCubesForSkinPart(collector, transform, skinPart);
                }
                if (skinPart == SkinPartTypes.BLOCK_MULTI && multiblock) {
                    blockCount += clearEquipmentCubesForSkinPart(collector, transform, skinPart);
                }
            } else {
                blockCount += clearEquipmentCubesForSkinPart(collector, transform, skinPart);
            }
        }
        return blockCount;
    }

    private static int clearEquipmentCubesForSkinPart(CubeChangesCollector collector, CubeTransform transform, ISkinPartType skinPart) {
        int blockCount = 0;
        for (Vector3i offset : getResolvedBuildingSpace2(skinPart)) {
            var cube = collector.getCube(transform.mul(offset));
            if (cube.is(SkinCubeBlock.class)) {
                cube.setBlockStateAndTag(Blocks.AIR.defaultBlockState(), (CompoundTag) null);
                blockCount++;
            }
        }
        return blockCount;
    }

    public static Rectangle3i getResolvedBuildingSpace(ISkinPartType skinPart) {
        IVector3i origin = skinPart.getOffset();
        IRectangle3i buildSpace = skinPart.getBuildingSpace();
        int dx = -origin.getX() + buildSpace.getX();
        int dy = -origin.getY();
        int dz = origin.getZ() + buildSpace.getZ();
        return new Rectangle3i(dx, dy, dz, buildSpace.getWidth(), buildSpace.getHeight(), buildSpace.getDepth());
    }

    private static Iterable<Vector3i> getResolvedBuildingSpace2(ISkinPartType skinPart) {
        return getResolvedBuildingSpace(skinPart).enumerateZYX();
    }

    private static int getNumberOfCubesInPart(Level level, CubeTransform transform, ISkinPartType skinPart) {
        int cubeCount = 0;
        for (Vector3i offset : getResolvedBuildingSpace2(skinPart)) {
            BlockState blockState = level.getBlockState(transform.mul(offset));
            if (blockState.getBlock() instanceof SkinCubeBlock) {
                cubeCount++;
            }
        }
        return cubeCount;
    }

    private static Direction getResolvedDirection(Direction dir, boolean mirror) {
        // we're just mirroring the x-axis when if it needs.
        if (mirror && dir.getAxis() == Direction.Axis.X) {
            return dir.getOpposite();
        }
        return dir;
    }
}

