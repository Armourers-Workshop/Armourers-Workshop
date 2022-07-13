package moe.plushie.armourers_workshop.builder.world;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SkinCubeWrapper implements IPaintable {

    private final Consumer<SkinCubeChanges> consumer;
    private final World world;

    private BlockPos pos;

    private Supplier<BlockState> state;
    private Supplier<TileEntity> tileEntity;
    private Supplier<IPaintable> target;

    private SkinCubeChanges changes;

    public SkinCubeWrapper(World world, Consumer<SkinCubeChanges> consumer) {
        this.consumer = consumer;
        this.world = world;
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
            BlockState state = world.getBlockState(pos);
            this.state = () -> state;
            return state;
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Nullable
    public TileEntity getBlockEntity() {
        if (this.tileEntity != null) {
            return this.tileEntity.get();
        }
        if (this.pos != null) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            this.tileEntity = () -> tileEntity;
            return tileEntity;
        }
        return null;
    }

    @Nullable
    public CompoundNBT getBlockEntityNBT() {
        TileEntity tileEntity = getBlockEntity();
        if (tileEntity != null) {
            return tileEntity.serializeNBT();
        }
        return null;
    }

    public void setBlockState(BlockState state) {
        this.lastChanges().setState(state);
    }

    public void setBlockState(BlockState state, CompoundNBT nbt) {
        this.lastChanges().setState(state);
        this.lastChanges().setCompoundNBT(nbt);
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
        TileEntity tileEntity = getBlockEntity();
        if (tileEntity instanceof IPaintable) {
            IPaintable target = (IPaintable) tileEntity;
            this.target = () -> target;
            return target;
        }
        return null;
    }

    private SkinCubeChanges lastChanges() {
        if (changes == null) {
            changes = new SkinCubeChanges(world, pos);
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



