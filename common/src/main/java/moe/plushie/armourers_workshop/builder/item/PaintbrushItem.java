package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.builder.client.gui.PaletteToolWindow;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolAction;
import moe.plushie.armourers_workshop.builder.item.option.PaintingToolOptions;
import moe.plushie.armourers_workshop.builder.other.CubePaintingEvent;
import moe.plushie.armourers_workshop.core.data.paint.IItemPaintable;
import moe.plushie.armourers_workshop.core.data.paint.IPaintProvider;
import moe.plushie.armourers_workshop.core.data.paint.IPaintToolPicker;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModSoundEvents;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutorIO;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PaintbrushItem extends AbstractColoredToolItem implements IItemPaintable, IBlockPaintViewer, IPaintToolPicker {

    public PaintbrushItem(Properties properties) {
        super(properties);
    }

    @Override
    public OpenInteractionResult usePickTool(Level level, BlockPos pos, OpenDirection dir, BlockEntity blockEntity, IUseOnContext context) {
        if (blockEntity instanceof IPaintProvider provider) {
            setItemColor(context.itemInHand(), provider.color());
            return OpenInteractionResult.sidedSuccess(level.isClientSide());
        }
        return OpenInteractionResult.PASS;
    }

    @Override
    public void createToolProperties(Consumer<IConfigurableToolProperty<?>> builder) {
        builder.accept(PaintingToolOptions.FULL_BLOCK_MODE);
        builder.accept(PaintingToolOptions.CHANGE_PAINT_COLOR);
        builder.accept(PaintingToolOptions.CHANGE_PAINT_TYPE);
    }

    @Override
    public IPaintToolAction createPaintToolAction(IUseOnContext context) {
        var itemStack = context.itemInHand();
        var paintColor = getItemColor(itemStack, SkinPaintColor.WHITE);
        var usePaintColor = itemStack.get(PaintingToolOptions.CHANGE_PAINT_COLOR);
        var usePaintType = itemStack.get(PaintingToolOptions.CHANGE_PAINT_TYPE);
        return new CubePaintingEvent.SetAction(paintColor, usePaintColor, usePaintType);
    }

    @Override
    public boolean openContainer(Level level, Player player, OpenInteractionHand hand, ItemStack itemStack) {
        // when the play hold ctrl, we need to open the built-in palette.
        if (level.isClientSide() && EnvironmentExecutorIO.hasControlDown()) {
            openPaletteGUI(level, player, hand, itemStack);
            return true;
        }
        return super.openContainer(level, player, hand, itemStack);
    }

    public void openPaletteGUI(Level level, Player player, OpenInteractionHand hand, ItemStack itemStack) {
        EnvironmentExecutor.runOnClient(() -> () -> {
            var window = new PaletteToolWindow(itemStack.getHoverName(), itemStack, hand);
            Minecraft.getInstance().setScreen(window.asScreen());
        });
    }

    @Override
    protected void appendColorHoverText(ItemStack itemStack, List<Component> tooltips) {
        var paintColor = getItemColor(itemStack, SkinPaintColor.WHITE);
        tooltips.addAll(Colors.getColorTooltips(paintColor, true));
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
    public IRegistryHolder<SoundEvent> getItemSoundEvent(IUseOnContext context) {
        return ModSoundEvents.PAINT;
    }

    @Override
    public OpenInteractionResult abi$useOn(IUseOnContext context) {
        var resultType = usePickTool(context);
        if (resultType.consumesAction()) {
            return resultType;
        }
        return super.abi$useOn(context);
    }

    @Override
    protected int abi$getModelTintColor(ItemStack itemStack, @Nullable Level level, @Nullable LivingEntity entity, int layerIndex) {
        if (layerIndex == 1) {
            return Colors.getDisplayRGB(itemStack);
        }
        return 0xffffffff;
    }

    @Override
    public void abi$appendModelProperties(BiConsumer<OpenResourceKey, IItemModelProperty> builder) {
        builder.accept(ModConstants.key("small"), (itemStack, level, entity, id) -> itemStack.get(PaintingToolOptions.FULL_BLOCK_MODE) ? 0 : 1);
    }

    @Override
    protected boolean abi$isFoil(ItemStack itemStack) {
        var paintColor = getItemColor(itemStack, SkinPaintColor.WHITE);
        return paintColor.paintType() != SkinPaintTypes.NORMAL;
    }
}
