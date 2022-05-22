package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.painting.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.builder.tileentity.BoundingBoxTileEntity;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

public class ShadeNoiseToolItem extends AbstractPaintingToolItem implements IBlockPaintViewer {

    public ShadeNoiseToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public void createToolProperties(Consumer<IPaintingToolProperty<?>> builder) {
        builder.accept(ToolOptions.FULL_BLOCK_MODE);
        builder.accept(ToolOptions.INTENSITY);
    }

    @Override
    public void appendSettingHoverText(ItemStack itemStack, List<ITextComponent> tooltips) {
        int intensity = ToolOptions.INTENSITY.get(itemStack);
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.intensity", intensity));
        super.appendSettingHoverText(itemStack, tooltips);
    }

    @Override
    public IPaintColor getMixedColor(IPaintable target, Direction direction, ItemStack itemStack, ItemUseContext context) {
        int intensity = ToolOptions.INTENSITY.get(itemStack);
        IPaintColor oldColor = target.getColor(direction);
        int rgb = ColorUtils.addShadeNoise(oldColor.getRGB(), intensity);
        return PaintColor.of(rgb, oldColor.getPaintType());
    }

//    @Override
//    public boolean shouldApplyColor(World worldIn, IPaintable target, Direction direction, ItemUseContext context) {
//        // for bounding box block, we need to apply color on both sides.
//        if (target instanceof BoundingBoxTileEntity) {
//            return true;
//        }
//        // we need to mix the colors with random.
//        // random results is diff on the client/server side.
//        // so we only mix the colors on the server side.
//        return !worldIn.isClientSide();
//    }
}

//public class ItemShadeNoiseTool extends AbstractModItem implements IConfigurableTool, IBlockPainter {
//
//    @Override
//    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
//        IBlockState state = worldIn.getBlockState(pos);
//        ItemStack stack = player.getHeldItem(hand);
//
//        if (state.getBlock() instanceof IPantableBlock) {
//            if (!worldIn.isRemote) {
//                UndoManager.begin(player);
//            }
//            if (ToolOptions.FULL_BLOCK_MODE.getValue(stack)) {
//                for (int i = 0; i < 6; i++) {
//                    usedOnBlockSide(stack, player, worldIn, pos, state.getBlock(), EnumFacing.values()[i], facing == EnumFacing.values()[i]);
//                }
//            } else {
//                usedOnBlockSide(stack, player, worldIn, pos, state.getBlock(), facing, true);
//            }
//            if (!worldIn.isRemote) {
//                UndoManager.end(player);
//                if (ToolOptions.FULL_BLOCK_MODE.getValue(stack)) {
//                    worldIn.playSound(null, pos, ModSounds.NOISE, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.2F + 0.9F);
//                } else {
//                    worldIn.playSound(null, pos, ModSounds.NOISE, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.2F + 1.5F);
//                }
//            }
//            return EnumActionResult.SUCCESS;
//        }
//
//        if (state.getBlock() == ModBlocks.ARMOURER & player.isSneaking()) {
//            if (!worldIn.isRemote) {
//                TileEntity te = worldIn.getTileEntity(pos);
//                if (te != null && te instanceof TileEntityArmourer) {
//                    ((TileEntityArmourer)te).toolUsedOnArmourer(this, worldIn, stack, player);
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
//        int intensity = ToolOptions.INTENSITY.getValue(stack);
//        IPantableBlock worldColourable = (IPantableBlock) block;
//        if (worldColourable.isRemoteOnly(world, pos, face) & world.isRemote) {
//            byte[] rgbt = new byte[4];
//            int oldColour = worldColourable.getColour(world, pos, face);
//            IPaintType oldPaintType = worldColourable.getPaintType(world, pos, face);
//            Color c = UtilColour.addShadeNoise(new Color(oldColour), intensity);
//            rgbt[0] = (byte)c.getRed();
//            rgbt[1] = (byte)c.getGreen();
//            rgbt[2] = (byte)c.getBlue();
//            rgbt[3] = (byte)oldPaintType.getId();
//            if (block == ModBlocks.BOUNDING_BOX && oldPaintType == PaintTypeRegistry.PAINT_TYPE_NONE) {
//                rgbt[3] = (byte)PaintTypeRegistry.PAINT_TYPE_NORMAL.getId();
//            }
//            MessageClientToolPaintBlock message = new MessageClientToolPaintBlock(pos, face, rgbt);
//            PacketHandler.networkWrapper.sendToServer(message);
//        } else if(!worldColourable.isRemoteOnly(world, pos, face) & !world.isRemote) {
//            int oldColour = worldColourable.getColour(world, pos, face);
//            byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, face).getId();
//            int newColour = UtilColour.addShadeNoise(new Color(oldColour), intensity).getRGB();
//            UndoManager.blockPainted(player, world, pos, oldColour, oldPaintType, face);
//            ((IPantableBlock) block).setColour(world, pos, newColour, face);
//        }
//    }
