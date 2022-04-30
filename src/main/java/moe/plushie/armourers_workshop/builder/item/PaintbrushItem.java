package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IItemModelPropertiesProvider;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;
import moe.plushie.armourers_workshop.api.painting.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.core.item.impl.IPaintPicker;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import moe.plushie.armourers_workshop.init.common.AWCore;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PaintbrushItem extends AbstractPaintingToolItem implements IItemTintColorProvider, IItemModelPropertiesProvider, IPaintPicker, IBlockPaintViewer {

    public PaintbrushItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (pickColor(context)) {
            return ActionResultType.sidedSuccess(world.isClientSide);
        }
        return super.useOn(context);
    }

    @Override
    public void createToolProperties(Consumer<IPaintingToolProperty<?>> builder) {
        builder.accept(ToolOptions.FULL_BLOCK_MODE);
    }

    @Override
    public void createModelProperties(BiConsumer<ResourceLocation, IItemModelProperty> builder) {
        builder.accept(AWCore.resource("small"), (itemStack, world, entity) -> ToolOptions.FULL_BLOCK_MODE.get(itemStack) ? 0 : 1);
    }

    @Override
    public void appendColorHoverText(ItemStack itemStack, List<ITextComponent> tooltips, ITooltipFlag flags) {
        IPaintColor paintColor = ObjectUtils.defaultIfNull(ColorUtils.getColor(itemStack), PaintColor.WHITE);
        tooltips.addAll(ColorUtils.getColorTooltips(paintColor, true));
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

//    @Override
//    public void playToolSound(EntityPlayer player, World world, BlockPos pos, ItemStack stack) {
//        SoundEvent soundEvent = ModSounds.PAINT;
//        if (ModHolidays.APRIL_FOOLS.isHolidayActive()) {
//            soundEvent = ModSounds.BOI;
//        }
//        if (ToolOptions.FULL_BLOCK_MODE.getValue(stack)) {
//            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.2F + 0.9F);
//        } else {
//            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.2F + 1.5F);
//        }
//    }
//
//    @Override
//    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockPos pos, Block block, EnumFacing face, boolean spawnParticles) {
//        int colour = getToolColour(stack);
//        IPaintType paintType = getToolPaintType(stack);
//        if (!world.isRemote) {
//            IPantableBlock worldColourable = (IPantableBlock) block;
//            int oldColour = worldColourable.getColour(world, pos, face);
//            byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, face).getId();
//            UndoManager.blockPainted(player, world, pos, oldColour, oldPaintType, face);
//            ((IPantableBlock)block).setColour(world, pos, colour, face);
//            ((IPantableBlock)block).setPaintType(world, pos, paintType, face);
//        } else {
//            if (spawnParticles) {
//                spawnPaintParticles(world, pos, face, colour);
//            }
//        }
//    }
}