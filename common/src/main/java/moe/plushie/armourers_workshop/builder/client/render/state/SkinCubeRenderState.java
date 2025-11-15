package moe.plushie.armourers_workshop.builder.client.render.state;

import moe.plushie.armourers_workshop.core.client.render.state.BlockEntityRenderState;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.data.paint.IBlockPaintable;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SkinCubeRenderState extends BlockEntityRenderState {

    protected BlockPaintColor colors = new BlockPaintColor();

    public static <T extends BlockEntity & IBlockPaintable> void extract(T entity, SkinCubeRenderState renderState) {
        // extract the side color into colors.
        for (var direction : OpenDirection.values()) {
            var color = SkinPaintColor.CLEAR;
            if (entity.shouldChangeColor(direction) && entity.hasColor(direction)) {
                color = entity.getColor(direction);
            }
            renderState.colors.put(direction, color);
        }
    }

    public BlockPaintColor colors() {
        return colors;
    }
}
