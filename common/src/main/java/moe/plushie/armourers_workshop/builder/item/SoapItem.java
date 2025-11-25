package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolAction;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolApplier;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolSelector;
import moe.plushie.armourers_workshop.builder.other.CubePaintingEvent;
import moe.plushie.armourers_workshop.builder.other.CubeSelector;
import moe.plushie.armourers_workshop.core.item.FlavouredItem;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;

public class SoapItem extends FlavouredItem implements IPaintToolApplier {

    public SoapItem(Properties properties) {
        super(properties);
    }

    @Override
    public IPaintToolSelector createPaintToolSelector(IUseOnContext context) {
        return CubeSelector.box(context.clickedPos(), false);
    }

    @Override
    public IPaintToolAction createPaintToolAction(IUseOnContext context) {
        return new CubePaintingEvent.ClearAction();
    }

    @Override
    public boolean shouldUseTool(IUseOnContext context) {
        // by default, applying colors only execute on the client side,
        // and then send the final color to the server side.
        return context.level().isClientSide();
    }

    @Override
    protected OpenInteractionResult abi$useOn(IUseOnContext context) {
        var resultType = usePaintTool(context);
        if (resultType.consumesAction()) {
            return resultType;
        }
        return super.abi$useOn(context);
    }
}
