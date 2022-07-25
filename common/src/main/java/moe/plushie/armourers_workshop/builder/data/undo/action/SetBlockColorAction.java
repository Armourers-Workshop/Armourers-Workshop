package moe.plushie.armourers_workshop.builder.data.undo.action;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.action.IUndoCommand;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;

public class SetBlockColorAction extends BlockUndoAction {

    private final ImmutableMap<Direction, IPaintColor> newValue;

    public SetBlockColorAction(Level world, BlockPos pos, HashMap<Direction, IPaintColor> newValue) {
        super(world, pos);
        this.newValue = ImmutableMap.copyOf(newValue);
    }

    @Override
    public IUndoCommand apply() throws CommandRuntimeException {
        IPaintable target = (IPaintable) getTileEntity();
        HashMap<Direction, IPaintColor> oldValue = new HashMap<>();
        for (Direction direction : newValue.keySet()) {
            IPaintColor paintColor = target.getColor(direction);
            if (paintColor == null) {
                paintColor = PaintColor.CLEAR;
            }
            oldValue.put(direction, paintColor);
        }
        IUndoCommand revertAction = new SetBlockColorAction(world, blockPos, oldValue);
        target.setColors(newValue);
        return revertAction;
    }

    @Override
    public BlockEntity getTileEntity() {
        BlockEntity tileEntity = super.getTileEntity();
        if (tileEntity instanceof IPaintable) {
            return tileEntity;
        }
        return null;
    }
}
