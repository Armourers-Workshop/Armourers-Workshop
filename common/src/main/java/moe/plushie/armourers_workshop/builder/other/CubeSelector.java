package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolSelector;
import moe.plushie.armourers_workshop.compatibility.core.AbstractDirection;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CubeSelector implements IPaintToolSelector {

    final BlockPos blockPos;
    final MatchMode mode;
    final ArrayList<OpenRectangle3i> rects;

    final int radius;
    final boolean isPlaneOnly;
    final boolean isApplyAllFaces;

    protected CubeSelector(MatchMode mode, BlockPos blockPos, int radius, boolean isApplyAllFaces, boolean isPlaneOnly) {
        this.blockPos = blockPos;
        this.mode = mode;
        this.radius = Math.max(radius, 1);
        this.isApplyAllFaces = isApplyAllFaces;
        this.isPlaneOnly = isPlaneOnly;
        this.rects = new ArrayList<>();
    }

    protected CubeSelector(MatchMode mode, Iterable<OpenRectangle3i> rects) {
        this.blockPos = BlockPos.ZERO;
        this.mode = mode;
        this.radius = 0;
        this.isApplyAllFaces = true;
        this.isPlaneOnly = false;
        this.rects = Collections.newList(rects);
    }

    protected CubeSelector(IFriendlyByteBuf buffer) {
        this.blockPos = buffer.readBlockPos();
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
                rects.add(new OpenRectangle3i(x, y, z, width, height, depth));
            }
        }
    }

    public static CubeSelector from(IFriendlyByteBuf buffer) {
        return new CubeSelector(buffer);
    }

    public static CubeSelector box(BlockPos pos, boolean isApplyAllFaces) {
        return box(pos, 1, isApplyAllFaces);
    }

    public static CubeSelector box(BlockPos pos, int radius, boolean isApplyAllFaces) {
        return new CubeSelector(MatchMode.SAME, pos, radius, isApplyAllFaces, false);
    }

    public static CubeSelector all(Iterable<OpenRectangle3i> rects) {
        return new CubeSelector(MatchMode.ALL, rects);
    }

    public static CubeSelector plane(BlockPos pos, int radius, boolean isApplyAllFaces) {
        return new CubeSelector(MatchMode.SAME, pos, radius, isApplyAllFaces, true);
    }

    public static CubeSelector touching(BlockPos pos, int radius, boolean isApplyAllFaces, boolean isPlaneOnly) {
        return new CubeSelector(MatchMode.TOUCHING, pos, radius, isApplyAllFaces, isPlaneOnly);
    }

    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeEnum(mode);
        buffer.writeInt(radius);
        buffer.writeBoolean(isApplyAllFaces);
        buffer.writeBoolean(isPlaneOnly);
        if (mode == MatchMode.ALL) {
            buffer.writeInt(rects.size());
            for (OpenRectangle3i rect : rects) {
                buffer.writeInt(rect.x());
                buffer.writeInt(rect.y());
                buffer.writeInt(rect.z());
                buffer.writeInt(rect.width());
                buffer.writeInt(rect.height());
                buffer.writeInt(rect.depth());
            }
        }
    }

    @Override
    public void forEach(UseOnContext context, BiConsumer<BlockPos, OpenDirection> consumer) {
        var level = context.getLevel();
        var clickedFace = AbstractDirection.wrap(context.getClickedFace());
        var dirs = resolvedDirections(clickedFace);
        forEach(level, clickedFace, targetPos -> {
            for (var dir : dirs) {
                consumer.accept(targetPos, dir);
            }
        });
    }

    private void forEach(Level level, OpenDirection dir, Consumer<BlockPos> consumer) {
        switch (this.mode) {
            case ALL: {
                for (var rect : rects) {
                    for (var pos : rect.enumerateZYX()) {
                        consumer.accept(new BlockPos(pos.x(), pos.y(), pos.z()));
                    }
                }
                break;
            }
            case SAME: {
                var info = resolvedBlockInfo(level, blockPos);
                Consumer<BlockPos> consumer1 = targetPos -> {
                    if (Objects.equals(info, resolvedBlockInfo(level, targetPos))) {
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
                                consumer1.accept(blockPos.offset(ix, iy, iz));
                            }
                        }
                    }
                }
                break;
            }
            case TOUCHING: {
                BlockUtils.findTouchingBlockFaces(level, blockPos, dir, radius, isPlaneOnly).forEach(consumer);
                break;
            }
        }
    }

    private BlockPos resolvedPos(OpenDirection dir, int i, int j) {
        return switch (dir) {
            case UP, DOWN -> blockPos.offset(j, 0, i);
            case NORTH, SOUTH -> blockPos.offset(i, j, 0);
            case WEST, EAST -> blockPos.offset(0, i, j);
        };
    }

    private OpenDirection[] resolvedDirections(OpenDirection clickedFace) {
        if (!this.isApplyAllFaces) {
            return new OpenDirection[]{clickedFace};
        }
        return OpenDirection.values();
    }

    private Object resolvedBlockInfo(Level level, BlockPos pos) {
        if (radius == 1) {
            return null;
        }
        return level.getBlockState(pos).is(ModBlocks.BOUNDING_BOX.get());
    }

    protected enum MatchMode {
        ALL, SAME, TOUCHING
    }
}

