package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IItemColorProvider;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.IItemPropertiesProvider;
import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.core.item.impl.IPaintProvider;
import moe.plushie.armourers_workshop.core.item.impl.IPaintToolPicker;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

public class BottleItem extends FlavouredItem implements IItemTintColorProvider, IItemPropertiesProvider, IItemColorProvider, IPaintToolPicker {

    public BottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return usePickTool(context);
    }

    @Override
    public InteractionResult usePickTool(Level level, BlockPos pos, Direction dir, BlockEntity tileEntity, UseOnContext context) {
        ItemStack itemStack = context.getItemInHand();
        if (tileEntity instanceof IPaintProvider) {
            setItemColor(itemStack, ((IPaintProvider) tileEntity).getColor());
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        IPaintColor paintColor = getItemColor(itemStack);
        if (paintColor != null) {
            return paintColor.getPaintType() != SkinPaintTypes.NORMAL;
        }
        return false;
    }

    @Override
    public void setItemColor(ItemStack itemStack, IPaintColor paintColor) {
        ColorUtils.setColor(itemStack, paintColor);
    }

    @Override
    public IPaintColor getItemColor(ItemStack itemStack) {
        return ColorUtils.getColor(itemStack);
    }

    @Override
    public int getTintColor(ItemStack itemStack, int index) {
        if (index == 1) {
            return ColorUtils.getDisplayRGB(itemStack);
        }
        return 0xffffffff;
    }

    @Override
    public void createModelProperties(BiConsumer<ResourceLocation, IItemModelProperty> builder) {
        builder.accept(ModConstants.key("empty"), (itemStack, level, entity, id) -> ColorUtils.hasColor(itemStack) ? 0 : 1);
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltips, TooltipFlag flags) {
        super.appendHoverText(itemStack, level, tooltips, flags);
        IPaintColor paintColor = getItemColor(itemStack);
        if (paintColor != null) {
            tooltips.addAll(ColorUtils.getColorTooltips(paintColor, false));
        } else {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.empty"));
        }
    }
}
