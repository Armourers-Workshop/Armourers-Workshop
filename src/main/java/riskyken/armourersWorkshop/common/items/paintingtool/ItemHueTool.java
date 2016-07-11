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
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientToolPaintBlock;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.painting.tool.AbstractToolOption;
import riskyken.armourersWorkshop.common.painting.tool.IConfigurableTool;
import riskyken.armourersWorkshop.common.painting.tool.ToolOptions;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import riskyken.armourersWorkshop.common.undo.UndoManager;
import riskyken.armourersWorkshop.utils.TranslateUtils;

public class ItemHueTool extends AbstractPaintingTool implements IConfigurableTool {
    
    public ItemHueTool() {
        super(LibItemNames.HUE_TOOL);
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
            
            if ((Boolean) ToolOptions.FULL_BLOCK_MODE.readFromNBT(stack.getTagCompound())) {
                for (int i = 0; i < 6; i++) {
                    usedOnBlockSide(stack, playerIn, worldIn, pos, blockState.getBlock(), EnumFacing.values()[i]);
                }
            } else {
                usedOnBlockSide(stack, playerIn, worldIn, pos, blockState.getBlock(), facing);
            }
            if (!worldIn.isRemote) {
                UndoManager.end(playerIn);
                //worldIn.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.BURN, 1.0F, 1.0F);
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
    
    @Override
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockPos pos, Block block, EnumFacing side) {
        boolean changeHue = (boolean) ToolOptions.CHANGE_HUE.readFromNBTBool(stack.getTagCompound());
        boolean changeSaturation = (boolean) ToolOptions.CHANGE_SATURATION.readFromNBTBool(stack.getTagCompound());
        boolean changeBrightness = (boolean) ToolOptions.CHANGE_BRIGHTNESS.readFromNBTBool(stack.getTagCompound());
        boolean changePaintType = (boolean) ToolOptions.CHANGE_PAINT_TYPE.readFromNBTBool(stack.getTagCompound());
        
        Color toolColour = new Color(getToolColour(stack));
        PaintType paintType = getToolPaintType(stack);
        float[] toolhsb;
        toolhsb = Color.RGBtoHSB(toolColour.getRed(), toolColour.getGreen(), toolColour.getBlue(), null);
        IPantableBlock worldColourable = (IPantableBlock) block;
        
        
        if (worldColourable.isRemoteOnly(world, pos, side) & world.isRemote) {
            int oldColour = worldColourable.getColour(world, pos, side);
            byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, side).getKey();
            float[] blockhsb;
            Color blockColour = new Color(oldColour);
            blockhsb = Color.RGBtoHSB(blockColour.getRed(), blockColour.getGreen(), blockColour.getBlue(), null);
            
            float[] recolour = new float[] { blockhsb[0], blockhsb[1], blockhsb[2] };
            if (changeHue) {
                recolour[0] = toolhsb[0];
            }
            if (changeSaturation) {
                recolour[1] = toolhsb[1];
            }
            if (changeBrightness) {
                recolour[2] = toolhsb[2];
            }
            
            int newColour = Color.HSBtoRGB(recolour[0], recolour[1], recolour[2]);
            Color c = new Color(newColour);
            byte[] rgbt = new byte[4];
            rgbt[0] = (byte)c.getRed();
            rgbt[1] = (byte)c.getGreen();
            rgbt[2] = (byte)c.getBlue();
            rgbt[3] = oldPaintType;
            if (changePaintType) {
                rgbt[3] = (byte)paintType.getKey();
            }
            MessageClientToolPaintBlock message = new MessageClientToolPaintBlock(pos, side, rgbt);
            PacketHandler.networkWrapper.sendToServer(message);
        } else if(!worldColourable.isRemoteOnly(world, pos, side) & !world.isRemote) {
            int oldColour = worldColourable.getColour(world, pos, side);
            byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, side).getKey();
            float[] blockhsb;
            Color blockColour = new Color(oldColour);
            blockhsb = Color.RGBtoHSB(blockColour.getRed(), blockColour.getGreen(), blockColour.getBlue(), null);
            
            float[] recolour = new float[] { blockhsb[0], blockhsb[1], blockhsb[2] };
            if (changeHue) {
                recolour[0] = toolhsb[0];
            }
            if (changeSaturation) {
                recolour[1] = toolhsb[1];
            }
            if (changeBrightness) {
                recolour[2] = toolhsb[2];
            }
            
            int newColour = Color.HSBtoRGB(recolour[0], recolour[1], recolour[2]);
            
            UndoManager.blockPainted(player, world, pos, oldColour, oldPaintType, side);
            
            ((IPantableBlock)block).setColour(world, pos, newColour, side);
            if (changePaintType) {
                ((IPantableBlock)block).setPaintType(world, pos, paintType, side);
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
        String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
        String colourText = TranslateUtils.translate("item.armourersworkshop:rollover.colour", c.getRGB());
        String hexText = TranslateUtils.translate("item.armourersworkshop:rollover.hex", hex);
        String paintText = TranslateUtils.translate("item.armourersworkshop:rollover.paintType", paintType.getLocalizedName());
        
        list.add(colourText);
        list.add(hexText);
        list.add(paintText);
        list.add(TranslateUtils.translate("item.armourersworkshop:rollover.openSettings"));
    }

    @Override
    public void getToolOptions(ArrayList<AbstractToolOption> toolOptionList) {
        toolOptionList.add(ToolOptions.CHANGE_HUE);
        toolOptionList.add(ToolOptions.CHANGE_SATURATION);
        toolOptionList.add(ToolOptions.CHANGE_BRIGHTNESS);
        toolOptionList.add(ToolOptions.CHANGE_PAINT_TYPE);
        toolOptionList.add(ToolOptions.FULL_BLOCK_MODE);
    }
}
