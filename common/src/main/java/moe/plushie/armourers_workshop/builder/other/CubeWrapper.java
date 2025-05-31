package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.core.data.paint.IBlockPaintable;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CubeWrapper implements IBlockPaintable {

    private final Consumer<CubeChanges> consumer;
    private final Level level;

    private BlockPos pos;

    private Supplier<BlockState> state;
    private Supplier<BlockEntity> blockEntity;
    private Supplier<IBlockPaintable> target;

    private CubeChanges changes;

    public CubeWrapper(Level level, Consumer<CubeChanges> consumer) {
        this.consumer = consumer;
        this.level = level;
    }

    public boolean is(Class<?> clazz) {
        // for the block check, we need forwarding the call to `getBlock`.
        if (Block.class.isAssignableFrom(clazz)) {
            return clazz.isInstance(getBlock());
        }
        return clazz.isInstance(getBlockEntity());
    }

    public boolean is(Block block) {
        return getBlockState().is(block);
    }

    public Block getBlock() {
        return getBlockState().getBlock();
    }

    public BlockState getBlockState() {
        if (this.state != null) {
            return this.state.get();
        }
        if (this.pos != null) {
            var state = level.getBlockState(pos);
            this.state = () -> state;
            return state;
        }
        return Blocks.AIR.defaultBlockState();
    }

    public void setBlockState(BlockState state) {
        this.lastChanges().setBlockState(state);
    }

    public void setBlockStateAndTag(BlockState state, CompoundTag nbt) {
        this.lastChanges().setBlockState(state);
        this.lastChanges().setCompoundTag(nbt);
    }

    public void setBlockStateAndColors(BlockState state, Map<OpenDirection, SkinPaintColor> colors) {
        this.lastChanges().setBlockState(state);
        this.lastChanges().setColors(colors);
    }

    @Nullable
    public BlockEntity getBlockEntity() {
        if (this.blockEntity != null) {
            return this.blockEntity.get();
        }
        if (this.pos != null) {
            var blockEntity = level.getBlockEntity(pos);
            this.blockEntity = () -> blockEntity;
            return blockEntity;
        }
        return null;
    }

    @Nullable
    public CompoundTag getBlockTag() {
        var blockEntity = getBlockEntity();
        if (blockEntity != null) {
            return blockEntity.saveFullData(level.registryAccess());
        }
        return null;
    }

    @Override
    public SkinPaintColor getColor(OpenDirection direction) {
        var target = getTarget();
        if (target != null) {
            return target.getColor(direction);
        }
        return null;
    }

    @Override
    public void setColor(OpenDirection direction, SkinPaintColor color) {
        lastChanges().setColor(direction, color);
    }

    @Override
    public void setColors(Map<OpenDirection, SkinPaintColor> colors) {
        lastChanges().setColors(colors);
    }

    @Override
    public boolean shouldChangeColor(OpenDirection direction) {
        var target = getTarget();
        if (target != null) {
            return target.shouldChangeColor(direction);
        }
        return false;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        if (this.pos != pos) {
            this.submit();
        }
        this.pos = pos;
    }

    private IBlockPaintable getTarget() {
        if (this.target != null) {
            return this.target.get();
        }
        var target = Objects.safeCast(getBlockEntity(), IBlockPaintable.class);
        if (target != null) {
            this.target = () -> target;
            return target;
        }
        return null;
    }

    private CubeChanges lastChanges() {
        if (changes == null) {
            changes = new CubeChanges(level, pos);
        }
        return changes;
    }

    private void submit() {
        if (this.changes != null) {
            this.consumer.accept(changes);
            this.changes = null;
        }
        this.pos = null;
        this.state = null;
        this.blockEntity = null;
        this.target = null;
    }
}



