package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.client.render.element.SpecialRenderElement;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocument;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedBlockGuideRenderer extends AdvancedAbstractGuideRenderer {

    private final BlockState blockState;

    public AdvancedBlockGuideRenderer() {
        this.blockState = Blocks.GRASS_BLOCK.defaultBlockState();
    }

    @Override
    public void render(SkinDocument document, int lightmap, int overlay, IGraphicsContext context) {
        context.saveGraphicsState();
        context.scaleCTM(-16, -16, 16);
        context.translateCTM(-0.5f, -1.5f, -0.5f);
        context.draw(SpecialRenderElement.blockState(blockState, lightmap, overlay));
        context.restoreGraphicsState();
    }
}
