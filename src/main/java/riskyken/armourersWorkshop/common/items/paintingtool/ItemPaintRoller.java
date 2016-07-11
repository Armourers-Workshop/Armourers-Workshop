package riskyken.armourersWorkshop.common.items.paintingtool;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.api.common.painting.IPantable;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.painting.tool.AbstractToolOption;
import riskyken.armourersWorkshop.common.painting.tool.IConfigurableTool;
import riskyken.armourersWorkshop.common.painting.tool.ToolOptions;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import riskyken.armourersWorkshop.common.undo.UndoManager;
import riskyken.armourersWorkshop.utils.TranslateUtils;

public class ItemPaintRoller extends AbstractPaintingTool implements IConfigurableTool {
    
    public ItemPaintRoller() {
        super(LibItemNames.PAINT_ROLLER);
    }
    
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
            EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        
        IBlockState blockState = worldIn.getBlockState(pos);
        
        if (playerIn.isSneaking() & blockState.getBlock() == ModBlocks.colourMixer) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te != null && te instanceof IPantable) {
                if (!worldIn.isRemote) {
                    int colour = ((IPantable)te).getColour(facing);
                    PaintType paintType = ((IPantable)te).getPaintType(facing);
                    setToolColour(stack, colour);
                    setToolPaintType(stack, paintType);
                }
            }
            return EnumActionResult.SUCCESS;
        }
        
        if (blockState.getBlock() instanceof IPantableBlock) {
            if (!worldIn.isRemote) {
                UndoManager.begin(playerIn);
            }
            paintArea(worldIn, blockState.getBlock(), playerIn, stack, pos, facing);
            if (!worldIn.isRemote) {
                //worldIn.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.PAINT, 1.0F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
                UndoManager.end(playerIn);
            }
            
            return EnumActionResult.SUCCESS;
        }
        
        if (blockState.getBlock() == ModBlocks.armourerBrain & playerIn.isSneaking()) {
            if (!worldIn.isRemote) {
                TileEntity te = worldIn.getTileEntity(pos);
                if (te != null && te instanceof TileEntityArmourer) {
                    ((TileEntityArmourer)te).toolUsedOnArmourer(this, worldIn, stack, playerIn);
                }
            }
            return EnumActionResult.SUCCESS;
        }
        
        return EnumActionResult.FAIL;
    }
    
    private void paintArea(World world, Block targetBlock, EntityPlayer player, ItemStack stack, BlockPos pos, EnumFacing side) {
        int radius = (Integer) ToolOptions.RADIUS.readFromNBT(stack.getTagCompound());
        for (int i = -radius + 1; i < radius; i++ ) {
            for (int j = -radius + 1; j < radius; j++ ) {
                BlockPos bp = null;
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                
                switch (side) {
                case DOWN:
                    bp = new BlockPos(x + j, y, z + i);
                break;
                case UP:
                    bp = new BlockPos(x + j , y, z + i);
                break;
                case NORTH:
                    bp = new BlockPos(x + i, y + j, z);
                break;
                case SOUTH:
                    bp = new BlockPos(x + i, y + j, z);
                break;
                case WEST:
                    bp = new BlockPos(x, y + i, z + j);
                break;
                case EAST:
                    bp = new BlockPos(x, y + i, z + j);
                break;
                }
                
                IBlockState blockState = world.getBlockState(pos);
                if ((targetBlock != ModBlocks.boundingBox & blockState.getBlock() != ModBlocks.boundingBox) |
                        (targetBlock == ModBlocks.boundingBox & blockState.getBlock() == ModBlocks.boundingBox)) {
                    usedOnBlockSide(stack, player, world, bp, blockState.getBlock(), side);
                }
            }
        }
    }
    
    @Override
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockPos pos, Block block, EnumFacing side) {
        if (block instanceof IPantableBlock) {
            int newColour = getToolColour(stack);
            PaintType paintType = getToolPaintType(stack);
            if (!world.isRemote) {
                IPantableBlock worldColourable = (IPantableBlock) block;
                if ((Boolean) ToolOptions.FULL_BLOCK_MODE.readFromNBT(stack.getTagCompound())) {
                    for (int i = 0; i < 6; i++) {
                        EnumFacing face = EnumFacing.values()[i];
                        int oldColour = worldColourable.getColour(world, pos, face);
                        byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, face).getKey();
                        UndoManager.blockPainted(player, world, pos, oldColour, oldPaintType, face);
                        ((IPantableBlock)block).setColour(world, pos, newColour, face);
                        ((IPantableBlock)block).setPaintType(world, pos, paintType, face);
                    }
                } else {
                    int oldColour = worldColourable.getColour(world, pos, side);
                    byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, side).getKey();
                    UndoManager.blockPainted(player, world, pos, oldColour, oldPaintType, side);
                    ((IPantableBlock)block).setColour(world, pos, newColour, side);
                    ((IPantableBlock)block).setPaintType(world, pos, paintType, side);
                }
            } else {
                spawnPaintParticles(world, pos, side, newColour);
            }
        }
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (worldIn.isRemote & playerIn.isSneaking()) {
            playerIn.openGui(ArmourersWorkshop.instance, LibGuiIds.TOOL_OPTIONS, worldIn, 0, 0, 0);
        }
        return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
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
    
    @Override
    public void getToolOptions(ArrayList<AbstractToolOption> toolOptionList) {
        toolOptionList.add(ToolOptions.FULL_BLOCK_MODE);
        toolOptionList.add(ToolOptions.RADIUS);
    }
}
