package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolApplier;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;

@SuppressWarnings("NullableProblems")
public abstract class AbstractPaintToolItem extends AbstractConfigurableToolItem implements IPaintToolApplier {

    public AbstractPaintToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        return usePaintTool(context);
    }

    @Override
    public boolean shouldUseTool(ItemUseContext context) {
        // by default, applying colors only execute on the client side,
        // and then send the final color to the server side.
        return context.getLevel().isClientSide();
    }

    @Override
    public boolean shouldUseFullMode(ItemUseContext context) {
        return ToolOptions.FULL_BLOCK_MODE.get(context.getItemInHand());
    }
}
