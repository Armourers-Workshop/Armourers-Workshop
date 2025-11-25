package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolAction;
import moe.plushie.armourers_workshop.builder.item.option.PaintingToolOptions;
import moe.plushie.armourers_workshop.builder.other.CubePaintingEvent;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.ModSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class ShadeNoiseToolItem extends AbstractColoredToolItem implements IBlockPaintViewer {

    public ShadeNoiseToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public void createToolProperties(Consumer<IConfigurableToolProperty<?>> builder) {
        builder.accept(PaintingToolOptions.FULL_BLOCK_MODE);
        builder.accept(PaintingToolOptions.INTENSITY);
    }

    @Override
    public IPaintToolAction createPaintToolAction(IUseOnContext context) {
        var itemStack = context.itemInHand();
        var intensity = itemStack.get(PaintingToolOptions.INTENSITY);
        return new CubePaintingEvent.NoiseAction(intensity, true);
    }

    @Override
    protected void appendSettingHoverText(ItemStack itemStack, List<Component> tooltips) {
        var intensity = itemStack.get(PaintingToolOptions.INTENSITY);
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.intensity", intensity));
        super.appendSettingHoverText(itemStack, tooltips);
    }

    @Override
    public IRegistryHolder<SoundEvent> getItemSoundEvent(IUseOnContext context) {
        return ModSoundEvents.NOISE;
    }
}
