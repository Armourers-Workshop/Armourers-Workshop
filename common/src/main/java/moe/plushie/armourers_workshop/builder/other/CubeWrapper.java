package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class CubeWrapper implements IPaintable {

    private final Consumer<CubeChanges> consumer;
    private final Level level;

    private BlockPos pos;

    private Supplier<BlockState> state;
    private Supplier<BlockEntity> tileEntity;
    private Supplier<IPaintable> target;

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
            BlockState state = level.getBlockState(pos);
            this.state = () -> state;
            return state;
        }
        return Blocks.AIR.defaultBlockState();
    }

    public void setBlockState(BlockState state) {
        this.lastChanges().setState(state);
    }

    @Nullable
    public BlockEntity getBlockEntity() {
        if (this.tileEntity != null) {
            return this.tileEntity.get();
        }
        if (this.pos != null) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            this.tileEntity = () -> tileEntity;
            return tileEntity;
        }
        return null;
    }

    @Nullable
    public CompoundTag getBlockEntityNBT() {
        BlockEntity tileEntity = getBlockEntity();
        if (tileEntity != null) {
            return tileEntity.save(new CompoundTag());
        }
        return null;
    }

    public void setBlockState(BlockState state, CompoundTag nbt) {
        this.lastChanges().setState(state);
        this.lastChanges().setCompoundTag(nbt);
    }

    public void setBlockState(BlockState state, Map<Direction, IPaintColor> colors) {
        this.lastChanges().setState(state);
        this.lastChanges().setColors(colors);
    }

    @Override
    public IPaintColor getColor(Direction direction) {
        IPaintable target = getTarget();
        if (target != null) {
            return target.getColor(direction);
        }
        return null;
    }

    @Override
    public void setColor(Direction direction, IPaintColor color) {
        lastChanges().setColor(direction, color);
    }

    @Override
    public void setColors(Map<Direction, IPaintColor> colors) {
        lastChanges().setColors(colors);
    }

    @Override
    public boolean shouldChangeColor(Direction direction) {
        IPaintable target = getTarget();
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
            this.next();
        }
        this.pos = pos;
    }

    private IPaintable getTarget() {
        if (this.target != null) {
            return this.target.get();
        }
        IPaintable target = ObjectUtils.safeCast(getBlockEntity(), IPaintable.class);
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

    private void next() {
        if (this.changes != null) {
            this.consumer.accept(changes);
            this.changes = null;
        }
        this.pos = null;
        this.state = null;
        this.tileEntity = null;
        this.target = null;
    }
}



