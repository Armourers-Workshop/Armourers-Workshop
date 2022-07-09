package moe.plushie.armourers_workshop.builder.world;

import com.google.common.collect.Lists;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolSelector;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SkinCubeSelector implements IPaintToolSelector {

    final BlockPos pos;
    final MatchMode mode;
    final ArrayList<Rectangle3i> rects;

    final int radius;
    final boolean isPlaneOnly;
    final boolean isApplyAllFaces;

    protected SkinCubeSelector(MatchMode mode, BlockPos pos, int radius, boolean isApplyAllFaces, boolean isPlaneOnly) {
        this.pos = pos;
        this.mode = mode;
        this.radius = Math.max(radius, 1);
        this.isApplyAllFaces = isApplyAllFaces;
        this.isPlaneOnly = isPlaneOnly;
        this.rects = new ArrayList<>();
    }

    protected SkinCubeSelector(MatchMode mode, Iterable<Rectangle3i> rects) {
        this.pos = BlockPos.ZERO;
        this.mode = mode;
        this.radius = 0;
        this.isApplyAllFaces = true;
        this.isPlaneOnly = false;
        this.rects = Lists.newArrayList(rects);
    }

    protected SkinCubeSelector(PacketBuffer buffer) {
        this.pos = buffer.readBlockPos();
        this.mode = buffer.readEnum(MatchMode.class);
        this.radius = buffer.readInt();
        this.isApplyAllFaces = buffer.readBoolean();
        this.isPlaneOnly = buffer.readBoolean();
        this.rects = new ArrayList<>();
        if (this.mode == MatchMode.ALL) {
            int size = buffer.readInt();
            for (int i = 0; i < size; ++i) {
                int x = buffer.readInt();
                int y = buffer.readInt();
                int z = buffer.readInt();
                int width = buffer.readInt();
                int height = buffer.readInt();
                int depth = buffer.readInt();
                rects.add(new Rectangle3i(x, y, z, width, height, depth));
            }
        }
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeEnum(mode);
        buffer.writeInt(radius);
        buffer.writeBoolean(isApplyAllFaces);
        buffer.writeBoolean(isPlaneOnly);
        if (mode == MatchMode.ALL) {
            buffer.writeInt(rects.size());
            for (Rectangle3i rect : rects) {
                buffer.writeInt(rect.getX());
                buffer.writeInt(rect.getY());
                buffer.writeInt(rect.getZ());
                buffer.writeInt(rect.getWidth());
                buffer.writeInt(rect.getHeight());
                buffer.writeInt(rect.getDepth());
            }
        }
    }

    public static SkinCubeSelector from(PacketBuffer buffer) {
        return new SkinCubeSelector(buffer);
    }

    public static SkinCubeSelector box(BlockPos pos, boolean isApplyAllFaces) {
        return box(pos, 1, isApplyAllFaces);
    }

    public static SkinCubeSelector box(BlockPos pos, int radius, boolean isApplyAllFaces) {
        return new SkinCubeSelector(MatchMode.SAME, pos, radius, isApplyAllFaces, false);
    }

    public static SkinCubeSelector all(Iterable<Rectangle3i> rects) {
        return new SkinCubeSelector(MatchMode.ALL, rects);
    }

    public static SkinCubeSelector plane(BlockPos pos, int radius, boolean isApplyAllFaces) {
        return new SkinCubeSelector(MatchMode.SAME, pos, radius, isApplyAllFaces, true);
    }

    public static SkinCubeSelector touching(BlockPos pos, int radius, boolean isApplyAllFaces, boolean isPlaneOnly) {
        return new SkinCubeSelector(MatchMode.TOUCHING, pos, radius, isApplyAllFaces, isPlaneOnly);
    }

    public void forEach(ItemUseContext context, BiConsumer<BlockPos, Direction> consumer) {
        World world = context.getLevel();
        Direction clickedFace = context.getClickedFace();
        Direction[] dirs = resolvedDirections(clickedFace);
        forEach(world, clickedFace, targetPos -> {
            for (Direction dir : dirs) {
                consumer.accept(targetPos, dir);
            }
        });
    }

    private void forEach(World world, Direction dir, Consumer<BlockPos> consumer) {
        switch (this.mode) {
            case ALL: {
                for (Rectangle3i rect : rects) {
                    for (Vector3i pos : rect.enumerateZYX()) {
                        consumer.accept(new BlockPos(pos));
                    }
                }
                break;
            }
            case SAME: {
                Object info = resolvedBlockInfo(world, pos);
                Consumer<BlockPos> consumer1 = targetPos -> {
                    if (Objects.equals(info, resolvedBlockInfo(world, targetPos))) {
                        consumer.accept(targetPos);
                    }
                };
                if (isPlaneOnly) {
                    for (int j = -radius + 1; j < radius; ++j) {
                        for (int i = -radius + 1; i < radius; ++i) {
                            consumer1.accept(resolvedPos(dir, i, j));
                        }
                    }
                } else {
                    for (int iy = -radius + 1; iy < radius; ++iy) {
                        for (int ix = -radius + 1; ix < radius; ++ix) {
                            for (int iz = -radius + 1; iz < radius; ++iz) {
                                consumer1.accept(pos.offset(ix, iy, iz));
                            }
                        }
                    }
                }
                break;
            }
            case TOUCHING: {
                BlockUtils.findTouchingBlockFaces(world, pos, dir, radius, isPlaneOnly).forEach(consumer);
                break;
            }
        }
    }

    private BlockPos resolvedPos(Direction dir, int i, int j) {
        switch (dir) {
            case UP:
            case DOWN:
                return pos.offset(j, 0, i);

            case NORTH:
            case SOUTH:
                return pos.offset(i, j, 0);

            case WEST:
            case EAST:
                return pos.offset(0, i, j);

            default:
                return pos;
        }
    }

    private Direction[] resolvedDirections(Direction clickedFace) {
        if (!this.isApplyAllFaces) {
            return new Direction[]{clickedFace};
        }
        return Direction.values();
    }

    private Object resolvedBlockInfo(World world, BlockPos pos) {
        if (radius == 1) {
            return null;
        }
        return world.getBlockState(pos).is(ModBlocks.BOUNDING_BOX);
    }

    private enum MatchMode {
        ALL,
        SAME,
        TOUCHING
    }
}

