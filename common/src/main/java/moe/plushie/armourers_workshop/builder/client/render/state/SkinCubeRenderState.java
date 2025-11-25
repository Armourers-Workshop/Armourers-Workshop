package moe.plushie.armourers_workshop.builder.client.render.state;

import moe.plushie.armourers_workshop.core.client.render.state.BlockEntityRenderState;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.data.paint.IBlockPaintable;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SkinCubeRenderState extends BlockEntityRenderState {

    protected int markerTotal = 0;
    protected BlockPaintColor colors = new BlockPaintColor();

    public BlockPaintColor colors() {
        return colors;
    }

    public int markerTotal() {
        return markerTotal;
    }

    public static <T extends BlockEntity & IBlockPaintable> void extract(T entity, SkinCubeRenderState renderState) {
        // extract the side color into colors.
        var markers = 0;
        for (var direction : OpenDirection.values()) {
            var color = SkinPaintColor.CLEAR;
            if (entity.shouldChangeColor(direction) && entity.hasColor(direction)) {
                color = entity.getColor(direction);
                if (color.paintType() != SkinPaintTypes.NORMAL) {
                    markers += 1;
                }
            }
            renderState.colors.put(direction, color);
        }
        renderState.markerTotal = markers;
    }
}
