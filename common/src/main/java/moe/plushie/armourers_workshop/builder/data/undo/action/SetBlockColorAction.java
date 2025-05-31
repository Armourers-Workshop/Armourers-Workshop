package moe.plushie.armourers_workshop.builder.data.undo.action;

import moe.plushie.armourers_workshop.api.action.IUserAction;
import moe.plushie.armourers_workshop.core.data.paint.IBlockPaintable;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Map;

public class SetBlockColorAction extends BlockUserAction {

    private final Map<OpenDirection, SkinPaintColor> newValue;

    public SetBlockColorAction(Level level, BlockPos pos, Map<OpenDirection, SkinPaintColor> newValue) {
        super(level, pos);
        this.newValue = new HashMap<>(newValue);
    }

    @Override
    public IUserAction apply() throws RuntimeException {
        var target = (IBlockPaintable) getBlockEntity();
        var oldValue = new HashMap<OpenDirection, SkinPaintColor>();
        for (var direction : newValue.keySet()) {
            var paintColor = target.getColor(direction);
            if (paintColor == null) {
                paintColor = SkinPaintColor.CLEAR;
            }
            oldValue.put(direction, paintColor);
        }
        var revertAction = new SetBlockColorAction(level, blockPos, oldValue);
        target.setColors(newValue);
        return revertAction;
    }

    @Override
    public BlockEntity getBlockEntity() {
        var blockEntity = super.getBlockEntity();
        if (blockEntity instanceof IBlockPaintable) {
            return blockEntity;
        }
        return null;
    }
}
