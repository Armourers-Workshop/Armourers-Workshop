package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolSelector;
import moe.plushie.armourers_workshop.builder.item.option.PaintingToolOptions;
import moe.plushie.armourers_workshop.builder.other.CubeSelector;
import moe.plushie.armourers_workshop.init.ModSounds;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;
import java.util.function.Consumer;

public class PaintRollerItem extends PaintbrushItem {

    public PaintRollerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void createToolProperties(Consumer<IConfigurableToolProperty<?>> builder) {
        super.createToolProperties(builder);
        builder.accept(PaintingToolOptions.RADIUS);
    }

    @Override
    public IPaintToolSelector createPaintToolSelector(UseOnContext context) {
        var itemStack = context.getItemInHand();
        var pos = context.getClickedPos();
        var radius = itemStack.get(PaintingToolOptions.RADIUS);
        return CubeSelector.plane(pos, radius, shouldUseFullMode(context));
    }

    @Override
    public void appendSettingHoverText(ItemStack itemStack, List<Component> tooltips) {
        var radius = itemStack.get(PaintingToolOptions.RADIUS);
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.area", radius * 2 - 1, radius * 2 - 1, 1));
        super.appendSettingHoverText(itemStack, tooltips);
    }

    @Override
    public void playParticle(UseOnContext context) {
    }

    @Override
    public IRegistryHolder<SoundEvent> getItemSoundEvent(UseOnContext context) {
        return ModSounds.PAINT;
    }
}
