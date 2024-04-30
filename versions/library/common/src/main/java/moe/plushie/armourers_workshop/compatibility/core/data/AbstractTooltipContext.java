package moe.plushie.armourers_workshop.compatibility.core.data;

import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.core.skin.SkinOptions;
import net.minecraft.world.item.TooltipFlag;

public class AbstractTooltipContext<T> implements ITooltipContext {

    public final T context;

    public final TooltipFlag flag;

    public AbstractTooltipContext(T context, TooltipFlag flag) {
        this.context = context;
        this.flag = flag;
    }

    @Override
    public TooltipFlag getFlags() {
        return flag;
    }
}
