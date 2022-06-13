package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.init.common.ModSounds;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nullable;
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
    public void appendColorHoverText(ItemStack itemStack, List<ITextComponent> tooltips) {
        IPaintColor paintColor = ObjectUtils.defaultIfNull(ColorUtils.getColor(itemStack), PaintColor.WHITE);
        tooltips.addAll(ColorUtils.getColorTooltips(paintColor, true));
    }

    @Override
    public IPaintColor getMixedColor(IPaintable target, Direction direction, ItemStack itemStack, ItemUseContext context) {
        IPaintColor sourceColor = ObjectUtils.defaultIfNull(ColorUtils.getColor(itemStack), PaintColor.WHITE);
        IPaintColor destinationColor = target.getColor(direction);
        float[] sourceHSB = ColorUtils.RGBtoHSB(sourceColor);
        float[] destinationHSB = ColorUtils.RGBtoHSB(destinationColor);
        if (ToolOptions.CHANGE_HUE.get(itemStack)) {
            destinationHSB[0] = sourceHSB[0];
        }
        if (ToolOptions.CHANGE_SATURATION.get(itemStack)) {
            destinationHSB[1] = sourceHSB[1];
        }
        if (ToolOptions.CHANGE_BRIGHTNESS.get(itemStack)) {
            destinationHSB[2] = sourceHSB[2];
        }
        ISkinPaintType paintType = destinationColor.getPaintType();
        if (ToolOptions.CHANGE_PAINT_TYPE.get(itemStack)) {
            paintType = sourceColor.getPaintType();
        }
        int color = ColorUtils.HSBtoRGB(destinationHSB);
        return PaintColor.of(color, paintType);
    }

    @Override
    public SoundEvent getItemSoundEvent(ItemUseContext context) {
        return ModSounds.PAINT;
    }
}