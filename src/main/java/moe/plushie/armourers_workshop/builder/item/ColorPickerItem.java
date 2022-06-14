package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IItemModelPropertiesProvider;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;
import moe.plushie.armourers_workshop.api.painting.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.core.item.impl.IPaintPicker;
import moe.plushie.armourers_workshop.core.item.impl.IPaintProvider;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateColorPickerPacket;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.init.common.ModSounds;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import moe.plushie.armourers_workshop.init.common.AWCore;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ColorPickerItem extends AbstractPaintingToolItem implements IItemTintColorProvider, IItemModelPropertiesProvider, IPaintPicker, IBlockPaintViewer {

    public ColorPickerItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        World world = context.getLevel();
        if (pickColor(context)) {
            return ActionResultType.sidedSuccess(world.isClientSide);
        }
        if (applyColor(context)) {
            return ActionResultType.sidedSuccess(world.isClientSide);
        }
        return ActionResultType.PASS;
    }

    @Override
    public boolean pickColor(IWorld worldIn, BlockPos blockPos, Direction direction, ItemUseContext context) {
        ItemStack itemStack = context.getItemInHand();
        TileEntity tileEntity = worldIn.getBlockEntity(blockPos);
        if (tileEntity instanceof IPaintable) {
            IPaintColor color = ((IPaintable) tileEntity).getColor(direction);
            ColorUtils.setColor(itemStack, color);
            UpdateColorPickerPacket packet = new UpdateColorPickerPacket(context.getHand(), itemStack);
            NetworkHandler.getInstance().sendToServer(packet);
            // we only play local sound, color pick not need send to other players.
            playSound(context);
            return true;
        }
        // we required player must hold shift + right-click to apply the color,
        // but someone calls this method directly,
        // we must apply the color to the target.
        if (context.getPlayer() == null) {
            return applyColor(context);
        }
        return false;
    }

    @Override
    public boolean applyColor(World world, BlockPos blockPos, Direction direction, IPaintUpdater updater, ItemUseContext context) {
        ItemStack itemStack = context.getItemInHand();
        IPaintColor color = ColorUtils.getColor(itemStack);
        if (color == null) {
            return false;
        }
        TileEntity tileEntity = world.getBlockEntity(blockPos);
        if (tileEntity instanceof IPaintProvider) {
            IPaintProvider provider = (IPaintProvider) tileEntity;
            if (!ToolOptions.CHANGE_PAINT_TYPE.get(itemStack)) {
                color = PaintColor.of(color.getRGB(), provider.getColor().getPaintType());
            }
            provider.setColor(color);
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldPickColor(ItemUseContext context) {
        // because we have some data that the server side doesn't exist, such as skin texture
        // so the color pick must work on client side and then sent the color data to the server.
        return context.getLevel().isClientSide();
    }

    @Override
    public boolean shouldApplyColor(ItemUseContext context) {
        return IPaintPicker.super.shouldPickColor(context);
    }

    @Override
    public void createModelProperties(BiConsumer<ResourceLocation, IItemModelProperty> builder) {
        builder.accept(AWCore.resource("empty"), (itemStack, world, entity) -> ColorUtils.hasColor(itemStack) ? 0 : 1);
    }

    @Override
    public void createToolProperties(Consumer<IPaintingToolProperty<?>> builder) {
        builder.accept(ToolOptions.CHANGE_PAINT_TYPE);
    }

    @Override
    public void appendColorHoverText(ItemStack itemStack, List<ITextComponent> tooltips) {
        IPaintColor paintColor = ColorUtils.getColor(itemStack);
        if (paintColor != null) {
            tooltips.addAll(ColorUtils.getColorTooltips(paintColor, false));
        } else {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.empty"));
        }
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
        IPaintColor paintColor = ColorUtils.getColor(itemStack);
        if (paintColor != null) {
            return paintColor.getPaintType() != SkinPaintTypes.NORMAL;
        }
        return false;
    }

    @Override
    public SoundEvent getItemSoundEvent(ItemUseContext context) {
        return ModSounds.PICKER;
    }
}