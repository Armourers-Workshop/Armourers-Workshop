package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolApplier;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolSelector;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.builder.other.SkinCubeSelector;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

public abstract class AbstractPaintToolItem extends AbstractConfigurableToolItem implements IPaintToolApplier {

    public AbstractPaintToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return usePaintTool(context);
    }

    @Override
    public boolean shouldUseTool(UseOnContext context) {
        // by default, applying colors only execute on the client side,
        // and then send the final color to the server side.
        return context.getLevel().isClientSide();
    }

    @Override
    public IPaintToolSelector createPaintToolSelector(UseOnContext context) {
        boolean isFullMode = shouldUseFullMode(context);
        return SkinCubeSelector.box(context.getClickedPos(), isFullMode);
    }

    public boolean shouldUseFullMode(UseOnContext context) {
        return ToolOptions.FULL_BLOCK_MODE.get(context.getItemInHand());
    }
}
