package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IItemColorProvider;
import moe.plushie.armourers_workshop.api.common.IItemModelPropertiesProvider;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;
import moe.plushie.armourers_workshop.api.painting.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.core.item.impl.IPaintToolPicker;
import moe.plushie.armourers_workshop.core.item.impl.IPaintProvider;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateColorPickerPacket;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.init.common.ModSounds;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
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
import net.minecraft.world.World;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ColorPickerItem extends AbstractConfigurableToolItem implements IItemTintColorProvider, IItemModelPropertiesProvider, IItemColorProvider, IPaintToolPicker, IBlockPaintViewer {

    public ColorPickerItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        return usePickTool(context);
    }

    @Override
    public ActionResultType usePickTool(World world, BlockPos pos, Direction dir, TileEntity tileEntity, ItemUseContext context) {
        ItemStack itemStack = context.getItemInHand();
        if (tileEntity instanceof IPaintable) {
            IPaintColor color = ((IPaintable) tileEntity).getColor(dir);
            ColorUtils.setColor(itemStack, color);
            UpdateColorPickerPacket packet = new UpdateColorPickerPacket(context.getHand(), itemStack);
            NetworkHandler.getInstance().sendToServer(packet);
            // we only play local sound, color pick not need send to other players.
            playSound(context);
            return ActionResultType.SUCCESS;
        }
        if (tileEntity instanceof IPaintProvider) {
            PlayerEntity player = context.getPlayer();
            if (player != null && !player.isShiftKeyDown()) {
                return ActionResultType.PASS;
            }
            IPaintProvider provider = (IPaintProvider)tileEntity;
            IPaintColor newColor = getItemColor(itemStack);
            if (newColor == null) {
                return ActionResultType.PASS;
            }
            if (!ToolOptions.CHANGE_PAINT_TYPE.get(itemStack)) {
                newColor = PaintColor.of(newColor.getRGB(), provider.getColor().getPaintType());
            }
            provider.setColor(newColor);
            return ActionResultType.sidedSuccess(world.isClientSide);
        }
        return ActionResultType.PASS;
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
    public SoundEvent getItemSoundEvent(ItemUseContext context) {
        return ModSounds.PICKER;
    }
}