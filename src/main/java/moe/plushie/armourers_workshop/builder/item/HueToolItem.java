package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.function.Consumer;

public class HueToolItem extends PaintbrushItem {

    public HueToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public void createToolProperties(Consumer<IPaintingToolProperty<?>> builder) {
        builder.accept(ToolOptions.CHANGE_HUE);
        builder.accept(ToolOptions.CHANGE_SATURATION);
        builder.accept(ToolOptions.CHANGE_BRIGHTNESS);
        builder.accept(ToolOptions.CHANGE_PAINT_TYPE);
        super.createToolProperties(builder);
    }

    @Override
    public void appendColorHoverText(ItemStack itemStack, List<ITextComponent> tooltips) {
        IPaintColor paintColor = ObjectUtils.defaultIfNull(ColorUtils.getColor(itemStack), PaintColor.WHITE);
        tooltips.addAll(ColorUtils.getColorTooltips(paintColor, true));
    }

    @Override
    public IPaintColor getMixedColor(IPaintable target, Direction direction, ItemStack itemStack, ItemUseContext context) {
        IPaintColor sourceColor = ObjectUtils.defaultIfNull(ColorUtils.getColor(itemStack), PaintColor.WHITE);
        IPaintColor destinationColor = target.getColor(direction);
        float[] sourceHSB = ColorUtils.RGBtoHSB(sourceColor);
        float[] destinationHSB = ColorUtils.RGBtoHSB(destinationColor);
        if (ToolOptions.CHANGE_HUE.get(itemStack)) {
            destinationHSB[0] = sourceHSB[0];
        }
        if (ToolOptions.CHANGE_SATURATION.get(itemStack)) {
            destinationHSB[1] = sourceHSB[1];
        }
        if (ToolOptions.CHANGE_BRIGHTNESS.get(itemStack)) {
            destinationHSB[2] = sourceHSB[2];
        }
        ISkinPaintType paintType = destinationColor.getPaintType();
        if (ToolOptions.CHANGE_PAINT_TYPE.get(itemStack)) {
            paintType = sourceColor.getPaintType();
        }
        int color = ColorUtils.HSBtoRGB(destinationHSB);
        return PaintColor.of(color, paintType);
    }
}

//public class ItemHueTool extends AbstractPaintingTool implements IConfigurableTool {
//
//    public ItemHueTool() {
//        super(LibItemNames.HUE_TOOL);
//        setSortPriority(13);
//    }
//
//    @Override
//    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
//        IBlockState state = worldIn.getBlockState(pos);
//        ItemStack stack = player.getHeldItem(hand);
//
//        if (player.isSneaking() & state.getBlock() == ModBlocks.COLOUR_MIXER) {
//            TileEntity te = worldIn.getTileEntity(pos);
//            if (te != null && te instanceof IPantable) {
//                if (!worldIn.isRemote) {
//                    int colour = ((IPantable) te).getColour(0);
//                    IPaintType paintType = ((IPantable) te).getPaintType(0);
//                    setToolColour(stack, colour);
//                    setToolPaintType(stack, paintType);
//                }
//            }
//            return EnumActionResult.SUCCESS;
//        }
//
//        if (state.getBlock() instanceof IPantableBlock) {
//            if (!worldIn.isRemote) {
//                UndoManager.begin(player);
//            }
//
//            if (ToolOptions.FULL_BLOCK_MODE.getValue(stack)) {
//                for (int i = 0; i < 6; i++) {
//                    usedOnBlockSide(stack, player, worldIn, pos, state.getBlock(), EnumFacing.VALUES[i], facing == EnumFacing.VALUES[i]);
//                }
//            } else {
//                usedOnBlockSide(stack, player, worldIn, pos, state.getBlock(), facing, true);
//            }
//            if (!worldIn.isRemote) {
//                UndoManager.end(player);
//                worldIn.playSound(null, pos, ModSounds.PAINT, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
//            }
//
//            return EnumActionResult.SUCCESS;
//        }
//
//        if (state.getBlock() == ModBlocks.ARMOURER & player.isSneaking()) {
//            if (!worldIn.isRemote) {
//                TileEntity te = worldIn.getTileEntity(pos);
//                if (te != null && te instanceof TileEntityArmourer) {
//                    ((TileEntityArmourer) te).toolUsedOnArmourer(this, worldIn, stack, player);
//                }
//            }
//            return EnumActionResult.SUCCESS;
//        }
//
//        return EnumActionResult.PASS;
//    }
//
//    @Override
//    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockPos pos, Block block, EnumFacing face, boolean spawnParticles) {
//        boolean changeHue = ToolOptions.CHANGE_HUE.getValue(stack);
//        boolean changeSaturation = ToolOptions.CHANGE_SATURATION.getValue(stack);
//        boolean changeBrightness = ToolOptions.CHANGE_BRIGHTNESS.getValue(stack);
//        boolean changePaintType = ToolOptions.CHANGE_PAINT_TYPE.getValue(stack);
//
//        Color toolColour = new Color(getToolColour(stack));
//        IPaintType paintType = getToolPaintType(stack);
//        float[] toolhsb;
//        toolhsb = Color.RGBtoHSB(toolColour.getRed(), toolColour.getGreen(), toolColour.getBlue(), null);
//        IPantableBlock worldColourable = (IPantableBlock) block;
//
//        if (worldColourable.isRemoteOnly(world, pos, face) & world.isRemote) {
//            int oldColour = worldColourable.getColour(world, pos, face);
//            byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, face).getId();
//            float[] blockhsb;
//            Color blockColour = new Color(oldColour);
//            blockhsb = Color.RGBtoHSB(blockColour.getRed(), blockColour.getGreen(), blockColour.getBlue(), null);
//
//            float[] recolour = new float[] { blockhsb[0], blockhsb[1], blockhsb[2] };
//            if (changeHue) {
//                recolour[0] = toolhsb[0];
//            }
//            if (changeSaturation) {
//                recolour[1] = toolhsb[1];
//            }
//            if (changeBrightness) {
//                recolour[2] = toolhsb[2];
//            }
//
//            int newColour = Color.HSBtoRGB(recolour[0], recolour[1], recolour[2]);
//            Color c = new Color(newColour);
//            byte[] rgbt = new byte[4];
//            rgbt[0] = (byte) c.getRed();
//            rgbt[1] = (byte) c.getGreen();
//            rgbt[2] = (byte) c.getBlue();
//            rgbt[3] = oldPaintType;
//            if (changePaintType) {
//                rgbt[3] = (byte) paintType.getId();
//            }
//            MessageClientToolPaintBlock message = new MessageClientToolPaintBlock(pos, face, rgbt);
//            PacketHandler.networkWrapper.sendToServer(message);
//        } else if (!worldColourable.isRemoteOnly(world, pos, face) & !world.isRemote) {
//            int oldColour = worldColourable.getColour(world, pos, face);
//            byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, face).getId();
//            float[] blockhsb;
//            Color blockColour = new Color(oldColour);
//            blockhsb = Color.RGBtoHSB(blockColour.getRed(), blockColour.getGreen(), blockColour.getBlue(), null);
//
//            float[] recolour = new float[] { blockhsb[0], blockhsb[1], blockhsb[2] };
//            if (changeHue) {
//                recolour[0] = toolhsb[0];
//            }
//            if (changeSaturation) {
//                recolour[1] = toolhsb[1];
//            }
//            if (changeBrightness) {
//                recolour[2] = toolhsb[2];
//            }
//
//            int newColour = Color.HSBtoRGB(recolour[0], recolour[1], recolour[2]);
//
//            UndoManager.blockPainted(player, world, pos, oldColour, oldPaintType, face);
//
//            ((IPantableBlock) block).setColour(world, pos, newColour, face);
//            if (changePaintType) {
//                ((IPantableBlock) block).setPaintType(world, pos, paintType, face);
//            }
//        }
//    }
//}