package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.compat.core.item.AbstractItem;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FlavouredItem extends AbstractItem {

    public FlavouredItem(Properties properties) {
        super(properties);
    }

    @Override
    protected void abi$appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        super.abi$appendHoverText(itemStack, tooltips, context);
        tooltips.addAll(TranslateUtils.subtitles(abi$getDescriptionId(itemStack) + ".flavour"));
    }
}
