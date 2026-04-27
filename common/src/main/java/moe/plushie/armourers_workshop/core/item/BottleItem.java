package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.core.data.paint.IItemPaintable;
import moe.plushie.armourers_workshop.core.data.paint.IPaintProvider;
import moe.plushie.armourers_workshop.core.data.paint.IPaintToolPicker;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

public class BottleItem extends FlavouredItem implements IItemPaintable, IPaintToolPicker {

    public BottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public OpenInteractionResult usePickTool(Level level, BlockPos pos, OpenDirection dir, BlockEntity blockEntity, IUseOnContext context) {
        var itemStack = context.itemInHand();
        if (blockEntity instanceof IPaintProvider provider) {
            setItemColor(itemStack, provider.color());
            return OpenInteractionResult.sidedSuccess(level.isClientSide());
        }
        return OpenInteractionResult.PASS;
    }

    @Override
    protected boolean abi$isFoil(ItemStack itemStack) {
        var paintColor = getItemColor(itemStack);
        if (paintColor != null) {
            return paintColor.paintType() != SkinPaintTypes.NORMAL;
        }
        return false;
    }

    @Override
    public void setItemColor(ItemStack itemStack, SkinPaintColor paintColor) {
        itemStack.set(ModDataComponents.TOOL_COLOR.get(), paintColor);
    }

    @Override
    public SkinPaintColor getItemColor(ItemStack itemStack) {
        return itemStack.get(ModDataComponents.TOOL_COLOR.get());
    }

    @Override
    protected OpenInteractionResult abi$useOn(IUseOnContext context) {
        return usePickTool(context);
    }

    @Override
    protected int abi$getModelTintColor(ItemStack itemStack, @Nullable Level level, @Nullable LivingEntity entity, int layerIndex) {
        if (layerIndex == 1) {
            return Colors.getDisplayRGB(itemStack);
        }
        return 0xffffffff;
    }

    @Override
    protected void abi$appendModelProperties(BiConsumer<OpenResourceKey, IItemModelProperty> builder) {
        builder.accept(ModConstants.key("empty"), (itemStack, level, entity, id) -> itemStack.has(ModDataComponents.TOOL_COLOR.get()) ? 0 : 1);
    }

    @Override
    protected void abi$appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        super.abi$appendHoverText(itemStack, tooltips, context);
        var paintColor = getItemColor(itemStack);
        if (paintColor != null) {
            tooltips.addAll(Colors.getColorTooltips(paintColor, false));
        } else {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.empty"));
        }
    }
}
