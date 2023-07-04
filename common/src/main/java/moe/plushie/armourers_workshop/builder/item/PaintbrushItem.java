package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.api.common.IItemColorProvider;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.IItemPropertiesProvider;
import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;
import moe.plushie.armourers_workshop.api.painting.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.builder.client.gui.PaletteToolWindow;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolAction;
import moe.plushie.armourers_workshop.builder.item.option.PaintingToolOptions;
import moe.plushie.armourers_workshop.builder.other.CubePaintingEvent;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.item.impl.IPaintProvider;
import moe.plushie.armourers_workshop.core.item.impl.IPaintToolPicker;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModSounds;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutorIO;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

public class PaintbrushItem extends AbstractColoredToolItem implements IItemTintColorProvider, IItemPropertiesProvider, IItemColorProvider, IBlockPaintViewer, IPaintToolPicker {

    public PaintbrushItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult resultType = usePickTool(context);
        if (resultType.consumesAction()) {
            return resultType;
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResult usePickTool(Level level, BlockPos pos, Direction dir, BlockEntity blockEntity, UseOnContext context) {
        if (blockEntity instanceof IPaintProvider) {
            setItemColor(context.getItemInHand(), ((IPaintProvider) blockEntity).getColor());
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    public void createToolProperties(Consumer<IConfigurableToolProperty<?>> builder) {
        builder.accept(PaintingToolOptions.FULL_BLOCK_MODE);
    }

    @Override
    public IPaintToolAction createPaintToolAction(UseOnContext context) {
        IPaintColor paintColor = getItemColor(context.getItemInHand(), PaintColor.WHITE);
        return new CubePaintingEvent.SetAction(paintColor);
    }

    @Override
    public void createModelProperties(BiConsumer<ResourceLocation, IItemModelProperty> builder) {
        builder.accept(ModConstants.key("small"), (itemStack, level, entity, id) -> PaintingToolOptions.FULL_BLOCK_MODE.get(itemStack) ? 0 : 1);
    }

    @Override
    public void appendColorHoverText(ItemStack itemStack, List<Component> tooltips) {
        IPaintColor paintColor = getItemColor(itemStack, PaintColor.WHITE);
        tooltips.addAll(ColorUtils.getColorTooltips(paintColor, true));
    }

    @Override
    public boolean openContainer(Level level, Player player, InteractionHand hand, ItemStack itemStack) {
        // when the play hold ctrl, we need to open the built-in palette.
        if (level.isClientSide() && EnvironmentExecutorIO.hasSprintDown()) {
            openPaletteGUI(level, player, hand, itemStack);
            return true;
        }
        return super.openContainer(level, player, hand, itemStack);
    }

    @Environment(EnvType.CLIENT)
    public void openPaletteGUI(Level level, Player player, InteractionHand hand, ItemStack itemStack) {
        PaletteToolWindow window = new PaletteToolWindow(itemStack.getHoverName(), itemStack, hand);
        Minecraft.getInstance().setScreen(window.asScreen());
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
    public IRegistryKey<SoundEvent> getItemSoundEvent(UseOnContext context) {
        return ModSounds.PAINT;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        IPaintColor paintColor = getItemColor(itemStack, PaintColor.WHITE);
        return paintColor.getPaintType() != SkinPaintTypes.NORMAL;
    }
}
