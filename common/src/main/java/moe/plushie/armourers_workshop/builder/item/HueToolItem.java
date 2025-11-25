package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolAction;
import moe.plushie.armourers_workshop.builder.item.option.PaintingToolOptions;
import moe.plushie.armourers_workshop.builder.other.CubePaintingEvent;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.init.ModSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class HueToolItem extends PaintbrushItem {

    public HueToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public void createToolProperties(Consumer<IConfigurableToolProperty<?>> builder) {
        builder.accept(PaintingToolOptions.CHANGE_HUE);
        builder.accept(PaintingToolOptions.CHANGE_SATURATION);
        builder.accept(PaintingToolOptions.CHANGE_BRIGHTNESS);
        builder.accept(PaintingToolOptions.CHANGE_PAINT_TYPE);
        super.createToolProperties(builder);
    }

    @Override
    public IPaintToolAction createPaintToolAction(IUseOnContext context) {
        var itemStack = context.itemInHand();
        var paintColor = getItemColor(itemStack, SkinPaintColor.WHITE);
        var hue = itemStack.get(PaintingToolOptions.CHANGE_HUE);
        var saturation = itemStack.get(PaintingToolOptions.CHANGE_SATURATION);
        var brightness = itemStack.get(PaintingToolOptions.CHANGE_BRIGHTNESS);
        var paintType = itemStack.get(PaintingToolOptions.CHANGE_PAINT_TYPE);
        return new CubePaintingEvent.HueAction(paintColor, hue, saturation, brightness, paintType);
    }

    @Override
    protected void appendColorHoverText(ItemStack itemStack, List<Component> tooltips) {
        var paintColor = getItemColor(itemStack, SkinPaintColor.WHITE);
        tooltips.addAll(Colors.getColorTooltips(paintColor, true));
    }

    @Override
    public IRegistryHolder<SoundEvent> getItemSoundEvent(IUseOnContext context) {
        return ModSoundEvents.PAINT;
    }
}
