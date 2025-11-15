package moe.plushie.armourers_workshop.builder.client.render.state;

import moe.plushie.armourers_workshop.builder.blockentity.AdvancedBuilderBlockEntity;
import moe.plushie.armourers_workshop.core.client.render.state.BlockEntityRenderState;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocument;
import moe.plushie.armourers_workshop.init.ModDebugger;
import net.minecraft.world.phys.AABB;

public class AdvancedBuilderRenderState extends BlockEntityRenderState {

    protected OpenVector3f offset;

    protected OpenVector3f carmeOffset;
    protected OpenVector3f carmeRot;
    protected OpenVector3f carmeScale;

    protected SkinDocument document;

    protected AABB visibleBox;
    protected OpenVector3f renderOrigin;

    public SkinDocument document() {
        return document;
    }

    public OpenVector3f offset() {
        return offset;
    }

    public OpenVector3f carmeOffset() {
        return carmeOffset;
    }

    public OpenVector3f carmeRot() {
        return carmeRot;
    }

    public OpenVector3f carmeScale() {
        return carmeScale;
    }

    public AABB visibleBox() {
        return visibleBox;
    }

    public OpenVector3f renderOrigin() {
        return renderOrigin;
    }

    public static void extract(AdvancedBuilderBlockEntity entity, AdvancedBuilderRenderState renderState) {
        renderState.document = entity.document();
        renderState.offset = entity.offset;
        renderState.carmeOffset = entity.carmeOffset;
        renderState.carmeRot = entity.carmeRot;
        renderState.carmeScale = entity.carmeScale;
        if (ModDebugger.advancedBuilder) {
            var blockState = entity.getBlockState();
            renderState.visibleBox = entity.getVisibleBox(blockState);
            renderState.renderOrigin = entity.getRenderOrigin();
        }
    }
}
