package moe.plushie.armourers_workshop.builder.data.undo.action;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.action.IUserAction;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;

public class SetBlockColorAction extends BlockUserAction {

    private final ImmutableMap<Direction, IPaintColor> newValue;

    public SetBlockColorAction(Level level, BlockPos pos, HashMap<Direction, IPaintColor> newValue) {
        super(level, pos);
        this.newValue = ImmutableMap.copyOf(newValue);
    }

    @Override
    public IUserAction apply() throws RuntimeException {
        IPaintable target = (IPaintable) getBlockEntity();
        HashMap<Direction, IPaintColor> oldValue = new HashMap<>();
        for (Direction direction : newValue.keySet()) {
            IPaintColor paintColor = target.getColor(direction);
            if (paintColor == null) {
                paintColor = PaintColor.CLEAR;
            }
            oldValue.put(direction, paintColor);
        }
        IUserAction revertAction = new SetBlockColorAction(level, blockPos, oldValue);
        target.setColors(newValue);
        return revertAction;
    }

    @Override
    public BlockEntity getBlockEntity() {
        BlockEntity blockEntity = super.getBlockEntity();
        if (blockEntity instanceof IPaintable) {
            return blockEntity;
        }
        return null;
    }
}
