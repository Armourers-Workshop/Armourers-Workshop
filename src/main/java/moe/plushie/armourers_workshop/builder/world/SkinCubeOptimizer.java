package moe.plushie.armourers_workshop.builder.world;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

public class SkinCubeOptimizer implements IPaintable {

    protected final World world;
    protected final HashMap<Direction, IPaintColor> changes = new HashMap<>();

    protected BlockPos targetPos;
    protected Supplier<IPaintable> target;

    public SkinCubeOptimizer(World world) {
        this.world = world;
    }

    public void setLocation(BlockPos location) {
        if (Objects.equals(this.targetPos, location)) {
            return;
        }
        this.clean();
        this.targetPos = location;
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
        this.changes.put(direction, color);
    }

    @Override
    public boolean shouldChangeColor(Direction direction) {
        IPaintable target = getTarget();
        if (target != null) {
            return target.shouldChangeColor(direction);
        }
        return false;
    }


    public void submit(IPaintable target) {
        target.setColors(this.changes);
    }

    public void clean() {
        if (!this.changes.isEmpty()) {
            IPaintable target = getTarget();
            if (target != null) {
                submit(target);
            }
        }
        this.target = null;
        this.targetPos = null;
        this.changes.clear();
    }

    public IPaintable getTarget() {
        if (this.target != null) {
            return this.target.get();
        }
        if (this.targetPos != null) {
            TileEntity tileEntity = world.getBlockEntity(targetPos);
            IPaintable target = tileEntity instanceof IPaintable ? (IPaintable) tileEntity : null;
            this.target = () -> target;
            return target;
        }
        return null;
    }
}
