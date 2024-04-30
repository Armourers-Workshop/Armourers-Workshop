package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.compatibility.core.AbstractItem;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FlavouredItem extends AbstractItem {

    public FlavouredItem(Properties properties) {
        super(properties);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        super.appendHoverText(itemStack, tooltips, context);
        tooltips.addAll(TranslateUtils.subtitles(getDescriptionId(itemStack) + ".flavour"));
    }
}
