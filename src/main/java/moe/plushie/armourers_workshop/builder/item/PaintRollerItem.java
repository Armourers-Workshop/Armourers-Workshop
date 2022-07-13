package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolSelector;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.builder.world.SkinCubeSelector;
import moe.plushie.armourers_workshop.init.common.ModSounds;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import java.util.List;
import java.util.function.Consumer;

public class PaintRollerItem extends PaintbrushItem {

    public PaintRollerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void createToolProperties(Consumer<IPaintingToolProperty<?>> builder) {
        super.createToolProperties(builder);
        builder.accept(ToolOptions.RADIUS);
    }

    @Override
    public IPaintToolSelector createPaintToolSelector(ItemUseContext context) {
        ItemStack itemStack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        int radius = ToolOptions.RADIUS.get(itemStack);
        return SkinCubeSelector.plane(pos, radius, shouldUseFullMode(context));
    }

    @Override
    public void appendSettingHoverText(ItemStack itemStack, List<ITextComponent> tooltips) {
        int radius = ToolOptions.RADIUS.get(itemStack);
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.area", radius * 2 - 1, radius * 2 - 1, 1));
        super.appendSettingHoverText(itemStack, tooltips);
    }

    @Override
    public void playParticle(ItemUseContext context) {
    }

    @Override
    public SoundEvent getItemSoundEvent(ItemUseContext context) {
        return ModSounds.PAINT;
    }
}
