package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolApplier;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolSelector;
import moe.plushie.armourers_workshop.builder.item.option.PaintingToolOptions;
import moe.plushie.armourers_workshop.builder.other.CubeSelector;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;

public abstract class AbstractColoredToolItem extends AbstractPaintToolItem implements IPaintToolApplier {

    public AbstractColoredToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean shouldUseTool(IUseOnContext context) {
        // by default, applying colors only execute on the client side,
        // and then send the final color to the server side.
        return context.level().isClientSide();
    }

    @Override
    public IPaintToolSelector createPaintToolSelector(IUseOnContext context) {
        var isFullMode = shouldUseFullMode(context);
        return CubeSelector.box(context.clickedPos(), isFullMode);
    }

    public boolean shouldUseFullMode(IUseOnContext context) {
        return context.itemInHand().get(PaintingToolOptions.FULL_BLOCK_MODE);
    }

    @Override
    protected OpenInteractionResult abi$useOn(IUseOnContext context) {
        return usePaintTool(context);
    }
}
