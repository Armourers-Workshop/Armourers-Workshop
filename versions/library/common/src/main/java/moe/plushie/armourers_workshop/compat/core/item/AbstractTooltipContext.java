package moe.plushie.armourers_workshop.compat.core.item;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

@Available("[1.21, 1.26)")
public class AbstractTooltipContext implements ITooltipContext {

    public final Item.TooltipContext context;
    public final TooltipFlag flag;

    public AbstractTooltipContext(Item.TooltipContext context, TooltipFlag flag) {
        this.context = context;
        this.flag = flag;
    }

    public static ITooltipContext wrap(Item.TooltipContext context, Object display, TooltipFlag flag) {
        return new AbstractTooltipContext(context, flag);
    }

    public static AbstractTooltipContext unwrap(ITooltipContext context) {
        return ((AbstractTooltipContext) context);
    }

    @Override
    public TooltipFlag flags() {
        return flag;
    }
}
