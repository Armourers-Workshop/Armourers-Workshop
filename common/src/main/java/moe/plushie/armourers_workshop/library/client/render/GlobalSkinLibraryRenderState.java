package moe.plushie.armourers_workshop.library.client.render;

import moe.plushie.armourers_workshop.core.client.render.state.BlockEntityRenderState;
import moe.plushie.armourers_workshop.library.block.GlobalSkinLibraryBlock;
import moe.plushie.armourers_workshop.library.blockentity.GlobalSkinLibraryBlockEntity;
import net.minecraft.core.Direction;

public class GlobalSkinLibraryRenderState extends BlockEntityRenderState {

    protected long gameTime;
    protected Direction facing;

    public long gameTime() {
        return gameTime;
    }

    public Direction facing() {
        return facing;
    }

    public static void extract(GlobalSkinLibraryBlockEntity entity, GlobalSkinLibraryRenderState renderState) {
        var state = entity.getBlockState();
        renderState.facing = state.getValue(GlobalSkinLibraryBlock.FACING);
        var level = entity.getLevel();
        if (level != null) {
            renderState.gameTime = level.getGameTime();
        }
    }
}
