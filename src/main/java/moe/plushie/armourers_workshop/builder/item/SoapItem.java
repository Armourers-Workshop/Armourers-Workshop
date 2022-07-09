package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolAction;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolApplier;
import moe.plushie.armourers_workshop.builder.world.SkinCubeColorApplier;
import moe.plushie.armourers_workshop.core.item.FlavouredItem;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SoapItem extends FlavouredItem implements IPaintToolApplier {

    public SoapItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        ActionResultType resultType = usePaintTool(context);
        if (resultType.consumesAction()) {
            return resultType;
        }
        return super.useOn(context);
    }

    @Nullable
    @Override
    public IPaintToolAction createPaintToolAction(ItemUseContext context) {
        return new SkinCubeColorApplier.ClearAction();
    }

    @Override
    public boolean shouldUseTool(ItemUseContext context) {
        // by default, applying colors only execute on the client side,
        // and then send the final color to the server side.
        return context.getLevel().isClientSide();
    }

    @Override
    public boolean shouldUseFullMode(ItemUseContext context) {
        return false;
    }
}
