package moe.plushie.armourers_workshop.common.items.paintingtool;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.painting.PaintType;
import moe.plushie.armourers_workshop.common.painting.tool.AbstractToolOption;
import moe.plushie.armourers_workshop.common.painting.tool.IConfigurableTool;
import moe.plushie.armourers_workshop.common.painting.tool.ToolOptions;
import moe.plushie.armourers_workshop.common.undo.UndoManager;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemPaintRoller extends AbstractPaintingTool implements IConfigurableTool {
    
    public ItemPaintRoller() {
        super(LibItemNames.PAINT_ROLLER);
        setSortPriority(19);
    }
    /*
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
            int side, float hitX, float hitY, float hitZ) {
        
        Block block = world.getBlock(x, y, z);
        
        if (player.isSneaking() & block == ModBlocks.colourMixer) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof IPantable) {
                if (!world.isRemote) {
                    int colour = ((IPantable)te).getColour(0);
                    PaintType paintType = ((IPantable)te).getPaintType(0);
                    setToolColour(stack, colour);
                    setToolPaintType(stack, paintType);
                }
            }
            return true;
        }
        
        if (block instanceof IPantableBlock) {
            if (!world.isRemote) {
                UndoManager.begin(player);
            }
            paintArea(world, block, player, stack, x, y, z, side);
            if (!world.isRemote) {
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.PAINT, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
                UndoManager.end(player);
            }
            
            return true;
        }
        
        if (block == ModBlocks.armourerBrain & player.isSneaking()) {
            if (!world.isRemote) {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te != null && te instanceof TileEntityArmourer) {
                    ((TileEntityArmourer)te).toolUsedOnArmourer(this, world, stack, player);
                }
            }
            return true;
        }
        
        return false;
    }*/
    /*
    private void paintArea(World world, Block targetBlock, EntityPlayer player, ItemStack stack, int x, int y, int z, int side) {
        int radius = (Integer) ToolOptions.RADIUS.readFromNBT(stack.getTagCompound());
        for (int i = -radius + 1; i < radius; i++ ) {
            for (int j = -radius + 1; j < radius; j++ ) {
                BlockLocation bl = null;
                switch (side) {
                    case 0:
                        bl = new BlockLocation(x + j, y, z + i);
                        break;
                    case 1:
                        bl = new BlockLocation(x + j , y, z + i);
                        break;
                    case 2:
                        bl = new BlockLocation(x + i, y  + j, z);
                        break;
                    case 3:
                        bl = new BlockLocation(x + i, y + j, z);
                        break;
                    case 4:
                        bl = new BlockLocation(x, y + i, z + j);
                        break;
                    case 5:
                        bl = new BlockLocation(x, y + i, z + j);
                        break;
                }
                if (bl != null) {
                    Block block = world.getBlock(bl.x, bl.y, bl.z);
                    if ((targetBlock != ModBlocks.boundingBox & block != ModBlocks.boundingBox) |
                            (targetBlock == ModBlocks.boundingBox & block == ModBlocks.boundingBox)) {
                        usedOnBlockSide(stack, player, world, bl, block, side);
                    }
                }
            }
        }
    }*/
    
    @SuppressWarnings("deprecation")
    @Override
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockPos pos, Block block, EnumFacing facing) {
        if (block instanceof IPantableBlock) {
            int newColour = getToolColour(stack);
            PaintType paintType = getToolPaintType(stack);
            if (!world.isRemote) {
                IPantableBlock worldColourable = (IPantableBlock) block;
                if ((Boolean) ToolOptions.FULL_BLOCK_MODE.readFromNBT(stack.getTagCompound())) {
                    for (int i = 0; i < 6; i++) {
                        EnumFacing face = EnumFacing.VALUES[i];
                        int oldColour = worldColourable.getColour(world, pos, face);
                        byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, face).getKey();
                        UndoManager.blockPainted(player, world, pos, oldColour, oldPaintType, face);
                        ((IPantableBlock)block).setColour(world, pos, newColour, face);
                        ((IPantableBlock)block).setPaintType(world, pos, paintType, face);
                    }
                } else {
                    int oldColour = worldColourable.getColour(world, pos, facing);
                    byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, facing).getKey();
                    UndoManager.blockPainted(player, world, pos, oldColour, oldPaintType, facing);
                    ((IPantableBlock)block).setColour(world, pos, newColour, facing);
                    ((IPantableBlock)block).setPaintType(world, pos, paintType, facing);
                }
            } else {
                spawnPaintParticles(world, pos, facing, newColour);
            }
        }
    }
    /*
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote & player.isSneaking()) {
            player.openGui(ArmourersWorkshop.instance, LibGuiIds.TOOL_OPTIONS, world, 0, 0, 0);
        }
        return stack;
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        super.addInformation(stack, player, list, p_77624_4_);
        Color c = new Color(getToolColour(stack));
        PaintType paintType = getToolPaintType(stack);
        int radius = (Integer) ToolOptions.RADIUS.readFromNBT(stack.getTagCompound());
        
        String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
        String colourText = TranslateUtils.translate("item.armourersworkshop:rollover.colour", c.getRGB());
        String hexText = TranslateUtils.translate("item.armourersworkshop:rollover.hex", hex);
        String paintText = TranslateUtils.translate("item.armourersworkshop:rollover.paintType", paintType.getLocalizedName());
        String radiusText = TranslateUtils.translate("item.armourersworkshop:rollover.radius", radius * 2 - 1, radius * 2 - 1, 1);
        
        list.add(colourText);
        list.add(hexText);
        list.add(paintText);
        list.add(radiusText);
        list.add(TranslateUtils.translate("item.armourersworkshop:rollover.openSettings"));
    }
    */
    @Override
    public void getToolOptions(ArrayList<AbstractToolOption> toolOptionList) {
        toolOptionList.add(ToolOptions.FULL_BLOCK_MODE);
        toolOptionList.add(ToolOptions.RADIUS);
    }
}
