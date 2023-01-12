package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.*;
import moe.plushie.armourers_workshop.api.painting.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.builder.network.UpdateColorPickerPacket;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.item.impl.IPaintProvider;
import moe.plushie.armourers_workshop.core.item.impl.IPaintToolPicker;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModSounds;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ColorPickerItem extends AbstractConfigurableToolItem implements IItemTintColorProvider, IItemPropertiesProvider, IItemColorProvider, IPaintToolPicker, IBlockPaintViewer {

    public ColorPickerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return usePickTool(context);
    }

    @Override
    public InteractionResult usePickTool(Level level, BlockPos pos, Direction dir, BlockEntity tileEntity, UseOnContext context) {
        ItemStack itemStack = context.getItemInHand();
        if (tileEntity instanceof IPaintable) {
            if (!level.isClientSide()) {
                return InteractionResult.CONSUME;
            }
            IPaintColor color = ((IPaintable) tileEntity).getColor(dir);
            ColorUtils.setColor(itemStack, color);
            UpdateColorPickerPacket packet = new UpdateColorPickerPacket(context.getHand(), itemStack);
            NetworkManager.sendToServer(packet);
            // we only play local sound, color pick not need send to other players.
            playSound(context);
            return InteractionResult.SUCCESS;
        }
        if (tileEntity instanceof IPaintProvider) {
            Player player = context.getPlayer();
            if (player != null && !player.isShiftKeyDown()) {
                return InteractionResult.PASS;
            }
            IPaintProvider provider = (IPaintProvider) tileEntity;
            IPaintColor newColor = getItemColor(itemStack);
            if (newColor == null) {
                // this is an empty color picker, we don't need to do anything.
                return InteractionResult.CONSUME;
            }
            if (!ToolOptions.CHANGE_PAINT_TYPE.get(itemStack)) {
                newColor = PaintColor.of(newColor.getRGB(), provider.getColor().getPaintType());
            }
            provider.setColor(newColor);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    public void createModelProperties(BiConsumer<ResourceLocation, IItemModelProperty> builder) {
        builder.accept(ModConstants.key("empty"), (itemStack, level, entity, id) -> ColorUtils.hasColor(itemStack) ? 0 : 1);
    }

    @Override
    public void createToolProperties(Consumer<IPaintingToolProperty<?>> builder) {
        builder.accept(ToolOptions.CHANGE_PAINT_TYPE);
    }

    @Override
    public void appendColorHoverText(ItemStack itemStack, List<Component> tooltips) {
        IPaintColor paintColor = getItemColor(itemStack);
        if (paintColor != null) {
            tooltips.addAll(ColorUtils.getColorTooltips(paintColor, false));
        } else {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.empty"));
        }
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
    public boolean isFoil(ItemStack itemStack) {
        IPaintColor paintColor = getItemColor(itemStack, PaintColor.WHITE);
        return paintColor.getPaintType() != SkinPaintTypes.NORMAL;
    }

    @Override
    public IRegistryKey<SoundEvent> getItemSoundEvent(UseOnContext context) {
        return ModSounds.PICKER;
    }
}
