package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.api.common.IPaintable;
import moe.plushie.armourers_workshop.api.core.math.IVector3i;
import moe.plushie.armourers_workshop.api.skin.texture.ISkinPaintColor;
import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.compatibility.core.AbstractDirection;
import moe.plushie.armourers_workshop.core.data.OptionalDirection;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector2i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.skin.geometry.collection.SkinGeometrySetV1;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCube;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.serializer.exception.SkinSaveException;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureModel;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintData;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

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
    public static Skin saveSkinFromWorld(Level level, CubeTransform transform, SkinProperties skinProps, SkinType skinType, SkinPaintData paintData) throws SkinSaveException {
        var parts = new ArrayList<SkinPart>();

        if (skinType == SkinTypes.BLOCK) {
            var partType = SkinPartTypes.BLOCK;
            if (skinProps.get(SkinProperty.BLOCK_MULTIBLOCK)) {
                partType = SkinPartTypes.BLOCK_MULTI;
            }
            var skinPart = saveArmourPart(level, transform, partType, true);
            if (skinPart != null) {
                parts.add(skinPart);
            }
        } else {
            for (var partType : skinType.getParts()) {
                var skinPart = saveArmourPart(level, transform, partType, true);
                if (skinPart != null) {
                    parts.add(skinPart);
                }
            }
        }
        // TODO: support v2 texture
        // because old skin not support v2 texture format,
        // so downgrade v2 to v1 texture format.
        if (paintData != null) {
            var resolvedPaintData = SkinPaintData.v1();
            resolvedPaintData.copyFrom(paintData);
            paintData = resolvedPaintData;
        }

        var builder = new Skin.Builder(skinType);
        builder.properties(skinProps);
        builder.paintData(paintData);
        builder.parts(parts);
        var skin = builder.build();

        // check if there are any blocks in the build guides.
        if (skin.getParts().isEmpty() && skin.getPaintData() == null) {
            throw SkinSaveException.Type.NO_DATA.build("noting");
        }

        // check if the skin has all needed parts.
        for (var partType : skinType.getParts()) {
            if (partType.isPartRequired()) {
                boolean havePart = false;
                for (var part : skin.getParts()) {
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
            var testPart = saveArmourPart(level, transform, SkinPartTypes.BLOCK, true);
            if (testPart == null) {
                throw SkinSaveException.Type.INVALID_MULTIBLOCK.build("missingMainBlock");
            }
        }

        return skin;
    }

    private static SkinPart saveArmourPart(Level level, CubeTransform transform, SkinPartType partType, boolean markerCheck) throws SkinSaveException {
        var cubeCount = getNumberOfCubesInPart(level, transform, partType);
        if (cubeCount < 1) {
            return null;
        }
        var geometries = new SkinGeometrySetV1(cubeCount);
        var markerBlocks = new ArrayList<SkinMarker>();

        var buildSpace = partType.getBuildingSpace();
        var offset = partType.getOffset();

        int i = 0;
        for (int ix = 0; ix < buildSpace.width(); ix++) {
            for (int iy = 0; iy < buildSpace.height(); iy++) {
                for (int iz = 0; iz < buildSpace.depth(); iz++) {
                    var target = transform.mul(
                            ix + -offset.x() + buildSpace.x(),
                            iy + -offset.y(),
                            iz + offset.z() + buildSpace.z());

//                    BlockPos origin = new BlockPos(-ix + -buildSpace.getX(), -iy + -buildSpace.getY(), -iz + -buildSpace.getZ());

                    int xOrigin = -ix + -buildSpace.x();
                    int yOrigin = -iy + -buildSpace.y();
                    int zOrigin = -iz + -buildSpace.z();

                    var targetState = level.getBlockState(target);
                    if (targetState.getBlock() instanceof SkinCubeBlock) {
                        saveArmourBlockToList(level, transform, target,
                                xOrigin - 1,
                                yOrigin - 1,
                                -zOrigin,
                                geometries.get(i), markerBlocks);
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

        var builder = new SkinPart.Builder(partType);
        builder.geometries(geometries);
        builder.markers(markerBlocks);
        return builder.build();
    }

    private static void saveArmourBlockToList(Level level, CubeTransform transform, BlockPos pos, int ix, int iy, int iz, SkinCube cube, ArrayList<SkinMarker> markerBlocks) {
        var blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof IPaintable target)) {
            return;
        }
        var blockState = blockEntity.getBlockState();
        var marker = SkinCubeBlock.getMarker(blockState);

        cube.setType(SkinGeometryTypes.byBlock(blockState.getBlock()));
        cube.setBoundingBox(new OpenRectangle3f(ix, iy, iz, 1, 1, 1));
        for (var dir : Direction.values()) {
            var paintColor = target.getColor(dir);
            var resolvedDir = transform.invRotate(dir);
            cube.setPaintColor(AbstractDirection.wrap(resolvedDir), SkinPaintColor.of(paintColor));
        }
        if (marker != OptionalDirection.NONE) {
            var markFacing = transform.invRotate(marker.getDirection());
            var resolvedMarker = OptionalDirection.of(markFacing);
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
        for (var part : skin.getParts()) {
            loadSkinPartIntoWorld(collector, transform, part, false);
        }
    }

    private static void loadSkinPartIntoWorld(CubeChangesCollector collector, CubeTransform transform, SkinPart partData, boolean mirror) {
        var skinPart = partData.getType();
        var buildSpace = skinPart.getBuildingSpace();
        var offset = skinPart.getOffset();
        // only support vanilla cube.
        for (var cube : Collections.collect(partData.getGeometries(), SkinCube.class)) {
            var blockPos = cube.getBlockPos();
            var geometryType = cube.getType();
            var markerFacing = OptionalDirection.NONE;
            for (var marker : partData.getMarkers()) {
                var dir = marker.getDirection();
                if (dir != null && blockPos.equals(marker.getPosition())) {
                    var resolvedMarker = OptionalDirection.of(getResolvedDirection(dir, mirror));
                    markerFacing = OptionalDirection.of(transform.rotate(resolvedMarker.getDirection()));
                    break;
                }
            }
            var origin = new BlockPos(-offset.x(), -offset.y() + -buildSpace.y(), offset.z());
            loadSkinBlockIntoWorld(collector, transform, origin, geometryType, blockPos, markerFacing, cube, mirror);
        }
    }

    private static void loadSkinBlockIntoWorld(CubeChangesCollector collector, CubeTransform transform, BlockPos origin, SkinGeometryType geometryType, IVector3i cubePos, OptionalDirection markerFacing, SkinCube cube, boolean mirror) {
        var shiftX = -cubePos.x() - 1;
        var shiftY = cubePos.y() + 1;
        var shiftZ = cubePos.z();
        if (mirror) {
            shiftX = cubePos.x();
        }

        var target = transform.mul(shiftX + origin.getX(), origin.getY() - shiftY, shiftZ + origin.getZ());
        var targetCube = collector.getCube(target);

        if (targetCube.is(ModBlocks.BOUNDING_BOX.get())) {
            targetCube.setBlockStateAndTag(Blocks.AIR.defaultBlockState(), null);
        }

        var targetBlock = geometryType.getBlock();
        var targetState = SkinCubeBlock.setMarker(targetBlock.defaultBlockState(), markerFacing);

        var colors = new HashMap<Direction, ISkinPaintColor>();
        for (var dir : OpenDirection.values()) {
            var paintColor = cube.getPaintColor(dir);
            var resolvedDir = OptionalDirection.of(getResolvedDirection(dir, mirror));
            colors.put(transform.rotate(resolvedDir.getDirection()), paintColor);
        }

        targetCube.setBlockStateAndColors(targetState, colors);
    }

    public static void copyPaintData(SkinPaintData paintData, EntityTextureModel.Box srcBox, EntityTextureModel.Box destBox, boolean isMirrorX) {
        var srcX = srcBox.getBounds().x();
        var srcY = srcBox.getBounds().y();
        var srcZ = srcBox.getBounds().z();
        var destX = destBox.getBounds().x();
        var destY = destBox.getBounds().y();
        var destZ = destBox.getBounds().z();
        var destWidth = destBox.getBounds().width();
        var colors = new HashMap<OpenVector2i, Integer>();
        srcBox.forEach((texture, x, y, z, dir) -> {
            var ix = x - srcX;
            var iy = y - srcY;
            var iz = z - srcZ;
            if (isMirrorX) {
                ix = destWidth - ix - 1;
                dir = getResolvedDirection(dir, true);
            }
            var newTexture = destBox.get(ix + destX, iy + destY, iz + destZ, dir);
            if (newTexture == null) {
                return;
            }
            var color = paintData.getColor(texture);
            if (SkinPaintColor.isOpaque(color)) {
                // a special case is to use the mirror to swap the part texture,
                // we will copy the color to the map and then applying it when read finish.
                colors.put(newTexture, color);
            }
        });
        colors.forEach(paintData::setColor);
    }

    public static void clearPaintData(SkinPaintData paintData, EntityTextureModel.Box srcBox) {
        srcBox.forEach((texturePos, x, y, z, dir) -> paintData.setColor(texturePos, 0));
    }

    public static void replaceCubes(CubeChangesCollector collector, CubeTransform transform, SkinType skinType, SkinProperties skinProps, CubeReplacingEvent event) {
        for (var skinPart : skinType.getParts()) {
            for (var offset : getResolvedBuildingSpace2(skinPart)) {
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

    public static void copyCubes(CubeChangesCollector collector, CubeTransform transform, SkinType skinType, SkinProperties skinProps, SkinPartType srcType, SkinPartType destType, boolean mirror) throws SkinSaveException {
        var skinPart = saveArmourPart(collector.getLevel(), transform, srcType, false);
        if (skinPart != null) {
            var builder = new SkinPart.Builder(destType);
            builder.name(skinPart.getName());
            builder.transform(skinPart.getTransform());
            builder.geometries(skinPart.getGeometries());
            builder.markers(skinPart.getMarkers());
            builder.children(skinPart.getChildren());
            loadSkinPartIntoWorld(collector, transform, builder.build(), mirror);
        }
    }

    public static int clearMarkers(CubeChangesCollector collector, CubeTransform transform, SkinType skinType, SkinProperties skinProps, SkinPartType partType) {
        int blockCount = 0;
        for (var skinPart : skinType.getParts()) {
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

    private static int clearMarkersForSkinPart(CubeChangesCollector collector, CubeTransform transform, SkinPartType skinPart) {
        int blockCount = 0;
        for (var offset : getResolvedBuildingSpace2(skinPart)) {
            var cube = collector.getCube(transform.mul(offset));
            var targetState = cube.getBlockState();
            if (targetState.hasProperty(SkinCubeBlock.MARKER) && SkinCubeBlock.getMarker(targetState) != OptionalDirection.NONE) {
                cube.setBlockState(SkinCubeBlock.setMarker(targetState, OptionalDirection.NONE));
                blockCount++;
            }
        }
        return blockCount;
    }

    public static int clearCubes(CubeChangesCollector collector, CubeTransform transform, SkinType skinType, SkinProperties skinProps, SkinPartType partType) {
        int blockCount = 0;
        for (var skinPart : skinType.getParts()) {
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

    private static int clearEquipmentCubesForSkinPart(CubeChangesCollector collector, CubeTransform transform, SkinPartType skinPart) {
        int blockCount = 0;
        for (var offset : getResolvedBuildingSpace2(skinPart)) {
            var cube = collector.getCube(transform.mul(offset));
            if (cube.is(SkinCubeBlock.class)) {
                cube.setBlockStateAndTag(Blocks.AIR.defaultBlockState(), null);
                blockCount++;
            }
        }
        return blockCount;
    }

    public static OpenRectangle3i getResolvedBuildingSpace(SkinPartType skinPart) {
        var origin = skinPart.getOffset();
        var buildSpace = skinPart.getBuildingSpace();
        int dx = -origin.x() + buildSpace.x();
        int dy = -origin.y();
        int dz = origin.z() + buildSpace.z();
        return new OpenRectangle3i(dx, dy, dz, buildSpace.width(), buildSpace.height(), buildSpace.depth());
    }

    private static Iterable<OpenVector3i> getResolvedBuildingSpace2(SkinPartType skinPart) {
        return getResolvedBuildingSpace(skinPart).enumerateZYX();
    }

    private static int getNumberOfCubesInPart(Level level, CubeTransform transform, SkinPartType skinPart) {
        int cubeCount = 0;
        for (var offset : getResolvedBuildingSpace2(skinPart)) {
            var blockState = level.getBlockState(transform.mul(offset));
            if (blockState.getBlock() instanceof SkinCubeBlock) {
                cubeCount++;
            }
        }
        return cubeCount;
    }

    private static OpenDirection getResolvedDirection(OpenDirection dir, boolean mirror) {
        // we're just mirroring the x-axis when if it needs.
        if (mirror && dir.getAxis() == OpenDirection.Axis.X) {
            return dir.getOpposite();
        }
        return dir;
    }
}

