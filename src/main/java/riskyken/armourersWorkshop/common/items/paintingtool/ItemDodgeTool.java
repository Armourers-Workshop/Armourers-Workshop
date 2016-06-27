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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.items.AbstractModItem;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientToolPaintBlock;
import riskyken.armourersWorkshop.common.painting.IBlockPainter;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.painting.tool.AbstractToolOption;
import riskyken.armourersWorkshop.common.painting.tool.IConfigurableTool;
import riskyken.armourersWorkshop.common.painting.tool.ToolOptions;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import riskyken.armourersWorkshop.common.undo.UndoManager;
import riskyken.armourersWorkshop.utils.TranslateUtils;
import riskyken.armourersWorkshop.utils.UtilColour;
import riskyken.armourersWorkshop.utils.UtilItems;

public class ItemDodgeTool extends AbstractModItem implements IConfigurableTool, IBlockPainter {

    public ItemDodgeTool() {
        super(LibItemNames.DODGE_TOOL);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
            EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        
        IBlockState blockState = worldIn.getBlockState(pos);

        if (blockState.getBlock() instanceof IPantableBlock) {
            if (!worldIn.isRemote) {
                UndoManager.begin(playerIn);
            }
            
            if ((Boolean) ToolOptions.FULL_BLOCK_MODE.readFromNBT(stack.getTagCompound())) {
                for (int i = 0; i < 6; i++) {
                    usedOnBlockSide(stack, playerIn, worldIn, pos, blockState.getBlock(), EnumFacing.values()[i]);
                }
            } else {
                usedOnBlockSide(stack, playerIn, worldIn, pos, blockState.getBlock(), facing);
            }
            if (!worldIn.isRemote) {
                UndoManager.end(playerIn);
                //worldIn.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.DODGE, 1.0F, 1.0F);
            }
            
            return EnumActionResult.PASS;
        }
        
        if (blockState.getBlock() == ModBlocks.armourerBrain & playerIn.isSneaking()) {
            if (!worldIn.isRemote) {
                TileEntity te = worldIn.getTileEntity(pos);
                if (te != null && te instanceof TileEntityArmourer) {
                    ((TileEntityArmourer)te).toolUsedOnArmourer(this, worldIn, stack, playerIn);
                }
            }
            return EnumActionResult.PASS;
        }
        
        return EnumActionResult.FAIL;
    }
    
    @Override
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockPos pos, Block block, EnumFacing side) {
        int intensity = (Integer) ToolOptions.INTENSITY.readFromNBT(stack.getTagCompound());
        IPantableBlock worldColourable = (IPantableBlock) block;
        if (worldColourable.isRemoteOnly(world, pos, side) & world.isRemote) {
            byte[] rgbt = new byte[4];
            int oldColour = worldColourable.getColour(world, pos, side);
            PaintType oldPaintType = worldColourable.getPaintType(world, pos, side);
            Color c = UtilColour.makeColourBighter(new Color(oldColour), intensity);
            rgbt[0] = (byte)c.getRed();
            rgbt[1] = (byte)c.getGreen();
            rgbt[2] = (byte)c.getBlue();
            rgbt[3] = (byte)oldPaintType.getKey();
            if (block == ModBlocks.boundingBox && oldPaintType == PaintType.NONE) {
                rgbt[3] = (byte)PaintType.NORMAL.getKey();
            }
            MessageClientToolPaintBlock message = new MessageClientToolPaintBlock(pos, side, rgbt);
            PacketHandler.networkWrapper.sendToServer(message);
        } else if(!worldColourable.isRemoteOnly(world, pos, side) & !world.isRemote) {
            int oldColour = worldColourable.getColour(world, pos, side);
            byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, side).getKey();
            int newColour = UtilColour.makeColourBighter(new Color(oldColour), intensity).getRGB();
            UndoManager.blockPainted(player, world, pos, oldColour, oldPaintType, side);
            ((IPantableBlock) block).setColour(world, pos, newColour, side);
        }
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (worldIn.isRemote & playerIn.isSneaking()) {
            playerIn.openGui(ArmourersWorkshop.instance, LibGuiIds.TOOL_OPTIONS, worldIn, 0, 0, 0);
        }
        return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        super.addInformation(stack, player, list, p_77624_4_);
        int intensity = UtilItems.getIntensityFromStack(stack, 16);
        String rollover = TranslateUtils.translate("item.armourersworkshop:rollover.intensity", intensity);
        list.add(rollover);
        list.add(TranslateUtils.translate("item.armourersworkshop:rollover.openSettings"));
    }

    @Override
    public void getToolOptions(ArrayList<AbstractToolOption> toolOptionList) {
        toolOptionList.add(ToolOptions.FULL_BLOCK_MODE);
        toolOptionList.add(ToolOptions.INTENSITY);
    }
}
