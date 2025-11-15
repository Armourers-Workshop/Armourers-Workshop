package moe.plushie.armourers_workshop.core.client.render.state;

import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.client.other.BlockEntityRenderData;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;

public class SkinnableRenderState extends BlockEntityRenderState {

    protected OpenQuaternionf renderRotations = OpenQuaternionf.ONE;

    protected final SkinRenderState slots = new SkinRenderState();

    public OpenQuaternionf renderRotations() {
        return renderRotations;
    }

    public SkinRenderState slots() {
        return slots;
    }

    public static void extract(SkinnableBlockEntity entity, SkinnableRenderState renderState) {
        var blockState = entity.getBlockState();
        var renderData = BlockEntityRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        renderData.tick(entity);
        renderState.animationManager = renderData.animationManager();
        renderState.slots.prepare(renderData.allSkins());
        renderState.renderRotations = entity.getRenderRotations(blockState).orElse(OpenQuaternionf.ONE);
    }
}
