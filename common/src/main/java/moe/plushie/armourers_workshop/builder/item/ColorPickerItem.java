package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.builder.item.option.PaintingToolOptions;
import moe.plushie.armourers_workshop.builder.network.UpdateColorPickerPacket;
import moe.plushie.armourers_workshop.core.data.paint.IBlockPaintable;
import moe.plushie.armourers_workshop.core.data.paint.IItemPaintable;
import moe.plushie.armourers_workshop.core.data.paint.IPaintProvider;
import moe.plushie.armourers_workshop.core.data.paint.IPaintToolPicker;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModSoundEvents;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ColorPickerItem extends AbstractPaintToolItem implements IItemPaintable, IPaintToolPicker, IBlockPaintViewer {

    public ColorPickerItem(Properties properties) {
        super(properties);
    }

    @Override
    public OpenInteractionResult usePickTool(Level level, BlockPos pos, OpenDirection dir, BlockEntity blockEntity, IUseOnContext context) {
        var itemStack = context.itemInHand();
        if (blockEntity instanceof IBlockPaintable paintable) {
            if (!level.isClientSide()) {
                return OpenInteractionResult.CONSUME;
            }
            var color = paintable.getColor(dir);
            itemStack.set(ModDataComponents.TOOL_COLOR.get(), color);
            var packet = new UpdateColorPickerPacket(context.hand(), itemStack);
            NetworkManager.sendToServer(packet);
            // we only play local sound, color pick not need send to other players.
            playSound(context);
            return OpenInteractionResult.SUCCESS;
        }
        if (blockEntity instanceof IPaintProvider provider) {
            var player = context.player();
            if (player != null && !player.isSecondaryUseActive()) {
                return OpenInteractionResult.PASS;
            }
            var newColor = getItemColor(itemStack);
            if (newColor == null) {
                // this is an empty color picker, we don't need to do anything.
                return OpenInteractionResult.CONSUME;
            }
            if (!itemStack.get(PaintingToolOptions.CHANGE_PAINT_TYPE)) {
                newColor = newColor.withPaintType(provider.color().paintType());
            }
            provider.setColor(newColor);
            return OpenInteractionResult.sidedSuccess(level.isClientSide());
        }
        return OpenInteractionResult.PASS;
    }

    @Override
    public void createToolProperties(Consumer<IConfigurableToolProperty<?>> builder) {
        builder.accept(PaintingToolOptions.CHANGE_PAINT_TYPE);
    }

    @Override
    protected void appendColorHoverText(ItemStack itemStack, List<Component> tooltips) {
        var paintColor = getItemColor(itemStack);
        if (paintColor != null) {
            tooltips.addAll(Colors.getColorTooltips(paintColor, false));
        } else {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.empty"));
        }
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
    protected boolean abi$isFoil(ItemStack itemStack) {
        var paintColor = getItemColor(itemStack, SkinPaintColor.WHITE);
        return paintColor.paintType() != SkinPaintTypes.NORMAL;
    }

    @Override
    public IRegistryHolder<SoundEvent> getItemSoundEvent(IUseOnContext context) {
        return ModSoundEvents.PICKER;
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
    protected void abi$appendModelProperties(BiConsumer<OpenResourceLocation, IItemModelProperty> builder) {
        builder.accept(ModConstants.key("empty"), (itemStack, level, entity, id) -> itemStack.has(ModDataComponents.TOOL_COLOR.get()) ? 0 : 1);
    }
}
