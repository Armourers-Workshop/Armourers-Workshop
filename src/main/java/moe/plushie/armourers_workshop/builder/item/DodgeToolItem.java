package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.painting.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.init.common.ModSounds;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Consumer;

public class DodgeToolItem extends AbstractPaintingToolItem implements IBlockPaintViewer {

    public DodgeToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public void createToolProperties(Consumer<IPaintingToolProperty<?>> builder) {
        builder.accept(ToolOptions.FULL_BLOCK_MODE);
        builder.accept(ToolOptions.INTENSITY);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendSettingHoverText(ItemStack itemStack, List<ITextComponent> tooltips) {
        int intensity = ToolOptions.INTENSITY.get(itemStack);
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.intensity", intensity));
        super.appendSettingHoverText(itemStack, tooltips);
    }

    @Override
    public IPaintColor getMixedColor(IPaintable target, Direction direction, ItemStack itemStack, ItemUseContext context) {
        int intensity = ToolOptions.INTENSITY.get(itemStack);
        IPaintColor oldColor = target.getColor(direction);
        int rgb = ColorUtils.makeColourBighter(oldColor.getRGB(), intensity);
        return PaintColor.of(rgb, oldColor.getPaintType());
    }

    @Override
    public SoundEvent getItemSoundEvent(ItemUseContext context) {
        return ModSounds.DODGE;
    }
}