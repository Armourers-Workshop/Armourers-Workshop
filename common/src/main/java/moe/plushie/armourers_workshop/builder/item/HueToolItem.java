package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolAction;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.builder.other.CubePaintingEvent;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.init.ModSounds;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;
import java.util.function.Consumer;

public class HueToolItem extends PaintbrushItem {

    public HueToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public void createToolProperties(Consumer<IPaintingToolProperty<?>> builder) {
        builder.accept(ToolOptions.CHANGE_HUE);
        builder.accept(ToolOptions.CHANGE_SATURATION);
        builder.accept(ToolOptions.CHANGE_BRIGHTNESS);
        builder.accept(ToolOptions.CHANGE_PAINT_TYPE);
        super.createToolProperties(builder);
    }

    @Override
    public IPaintToolAction createPaintToolAction(UseOnContext context) {
        ItemStack itemStack = context.getItemInHand();
        IPaintColor paintColor = getItemColor(itemStack, PaintColor.WHITE);
        boolean hue = ToolOptions.CHANGE_HUE.get(itemStack);
        boolean saturation = ToolOptions.CHANGE_SATURATION.get(itemStack);
        boolean brightness = ToolOptions.CHANGE_BRIGHTNESS.get(itemStack);
        boolean paintType = ToolOptions.CHANGE_PAINT_TYPE.get(itemStack);
        return new CubePaintingEvent.HueAction(paintColor, hue, saturation, brightness, paintType);
    }

    @Override
    public void appendColorHoverText(ItemStack itemStack, List<Component> tooltips) {
        IPaintColor paintColor = getItemColor(itemStack, PaintColor.WHITE);
        tooltips.addAll(ColorUtils.getColorTooltips(paintColor, true));
    }

    @Override
    public IRegistryKey<SoundEvent> getItemSoundEvent(UseOnContext context) {
        return ModSounds.PAINT;
    }
}