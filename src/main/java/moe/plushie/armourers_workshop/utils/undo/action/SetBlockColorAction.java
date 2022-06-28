package moe.plushie.armourers_workshop.utils.undo.action;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.action.IUndoCommand;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.command.CommandException;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import java.util.HashMap;

public class SetBlockColorAction extends BlockUndoAction {

    private final ImmutableMap<Direction, IPaintColor> newValue;

    public SetBlockColorAction(TileEntity tileEntity, HashMap<Direction, IPaintColor> newValue) {
        super(tileEntity.getLevel(), tileEntity.getBlockPos());
        this.newValue = ImmutableMap.copyOf(newValue);
    }

    @Override
    public IUndoCommand apply() throws CommandException {
        IPaintable target = (IPaintable) getTileEntity();
        HashMap<Direction, IPaintColor> oldValue = new HashMap<>();
        for (Direction direction : newValue.keySet()) {
            IPaintColor paintColor = target.getColor(direction);
            if (paintColor == null) {
                paintColor = PaintColor.CLEAR;
            }
            oldValue.put(direction, paintColor);
        }
        IUndoCommand revertAction = new SetBlockColorAction(getTileEntity(), oldValue);
        target.setColors(newValue);
        return revertAction;
    }

    @Override
    public TileEntity getTileEntity() {
        TileEntity tileEntity = super.getTileEntity();
        if (tileEntity instanceof IPaintable) {
            return tileEntity;
        }
        return null;
    }
}
