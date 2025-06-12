package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.IItemPropertiesProvider;
import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.builder.client.gui.PaletteToolWindow;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolAction;
import moe.plushie.armourers_workshop.builder.item.option.PaintingToolOptions;
import moe.plushie.armourers_workshop.builder.other.CubePaintingEvent;
import moe.plushie.armourers_workshop.core.data.paint.IItemPaintable;
import moe.plushie.armourers_workshop.core.data.paint.IPaintProvider;
import moe.plushie.armourers_workshop.core.data.paint.IPaintToolPicker;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModSounds;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutorIO;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PaintbrushItem extends AbstractColoredToolItem implements IItemTintColorProvider, IItemPropertiesProvider, IItemPaintable, IBlockPaintViewer, IPaintToolPicker {

    public PaintbrushItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var resultType = usePickTool(context);
        if (resultType.consumesAction()) {
            return resultType;
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResult usePickTool(Level level, BlockPos pos, OpenDirection dir, BlockEntity blockEntity, UseOnContext context) {
        if (blockEntity instanceof IPaintProvider provider) {
            setItemColor(context.getItemInHand(), provider.color());
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    public void createToolProperties(Consumer<IConfigurableToolProperty<?>> builder) {
        builder.accept(PaintingToolOptions.FULL_BLOCK_MODE);
        builder.accept(PaintingToolOptions.CHANGE_PAINT_COLOR);
        builder.accept(PaintingToolOptions.CHANGE_PAINT_TYPE);
    }

    @Override
    public IPaintToolAction createPaintToolAction(UseOnContext context) {
        var itemStack = context.getItemInHand();
        var paintColor = getItemColor(itemStack, SkinPaintColor.WHITE);
        var usePaintColor = itemStack.get(PaintingToolOptions.CHANGE_PAINT_COLOR);
        var usePaintType = itemStack.get(PaintingToolOptions.CHANGE_PAINT_TYPE);
        return new CubePaintingEvent.SetAction(paintColor, usePaintColor, usePaintType);
    }

    @Override
    public void createModelProperties(BiConsumer<IResourceLocation, IItemModelProperty> builder) {
        builder.accept(ModConstants.key("small"), (itemStack, level, entity, id) -> itemStack.get(PaintingToolOptions.FULL_BLOCK_MODE) ? 0 : 1);
    }

    @Override
    public void appendColorHoverText(ItemStack itemStack, List<Component> tooltips) {
        var paintColor = getItemColor(itemStack, SkinPaintColor.WHITE);
        tooltips.addAll(ColorUtils.getColorTooltips(paintColor, true));
    }

    @Override
    public boolean openContainer(Level level, Player player, InteractionHand hand, ItemStack itemStack) {
        // when the play hold ctrl, we need to open the built-in palette.
        if (level.isClientSide() && EnvironmentExecutorIO.hasControlDown()) {
            openPaletteGUI(level, player, hand, itemStack);
            return true;
        }
        return super.openContainer(level, player, hand, itemStack);
    }

    @Environment(EnvType.CLIENT)
    public void openPaletteGUI(Level level, Player player, InteractionHand hand, ItemStack itemStack) {
        var window = new PaletteToolWindow(itemStack.getHoverName(), itemStack, hand);
        Minecraft.getInstance().setScreen(window.asScreen());
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
    public int getTintColor(ItemStack itemStack, int index) {
        if (index == 1) {
            return ColorUtils.getDisplayRGB(itemStack);
        }
        return 0xffffffff;
    }

    @Override
    public IRegistryHolder<SoundEvent> getItemSoundEvent(UseOnContext context) {
        return ModSounds.PAINT;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        var paintColor = getItemColor(itemStack, SkinPaintColor.WHITE);
        return paintColor.paintType() != SkinPaintTypes.NORMAL;
    }
}
