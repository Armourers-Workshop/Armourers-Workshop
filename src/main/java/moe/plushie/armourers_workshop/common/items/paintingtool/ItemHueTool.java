package moe.plushie.armourers_workshop.common.items.paintingtool;

import java.awt.Color;
import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientToolPaintBlock;
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

public class ItemHueTool extends AbstractPaintingTool implements IConfigurableTool {
    
    public ItemHueTool() {
        super(LibItemNames.HUE_TOOL);
        setSortPriority(13);
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
            
            if ((Boolean) ToolOptions.FULL_BLOCK_MODE.readFromNBT(stack.getTagCompound())) {
                for (int i = 0; i < 6; i++) {
                    usedOnBlockSide(stack, player, world, new BlockLocation(x, y, z), block, i);
                }
            } else {
                usedOnBlockSide(stack, player, world, new BlockLocation(x, y, z), block, side);
            }
            if (!world.isRemote) {
                UndoManager.end(player);
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.BURN, 1.0F, 1.0F);
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
    
    @SuppressWarnings("deprecation")
    @Override
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockPos pos, Block block, EnumFacing facing) {
        boolean changeHue = (boolean) ToolOptions.CHANGE_HUE.readFromNBTBool(stack.getTagCompound());
        boolean changeSaturation = (boolean) ToolOptions.CHANGE_SATURATION.readFromNBTBool(stack.getTagCompound());
        boolean changeBrightness = (boolean) ToolOptions.CHANGE_BRIGHTNESS.readFromNBTBool(stack.getTagCompound());
        boolean changePaintType = (boolean) ToolOptions.CHANGE_PAINT_TYPE.readFromNBTBool(stack.getTagCompound());
        
        Color toolColour = new Color(getToolColour(stack));
        PaintType paintType = getToolPaintType(stack);
        float[] toolhsb;
        toolhsb = Color.RGBtoHSB(toolColour.getRed(), toolColour.getGreen(), toolColour.getBlue(), null);
        IPantableBlock worldColourable = (IPantableBlock) block;
        
        
        if (worldColourable.isRemoteOnly(world, pos, facing) & world.isRemote) {
            int oldColour = worldColourable.getColour(world, pos, facing);
            byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, facing).getKey();
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
            MessageClientToolPaintBlock message = new MessageClientToolPaintBlock(pos, facing, rgbt);
            PacketHandler.networkWrapper.sendToServer(message);
        } else if(!worldColourable.isRemoteOnly(world, pos, facing) & !world.isRemote) {
            int oldColour = worldColourable.getColour(world, pos, facing);
            byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, facing).getKey();
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
            
            UndoManager.blockPainted(player, world, pos, oldColour, oldPaintType, facing);
            
            ((IPantableBlock)block).setColour(world, pos, newColour, facing);
            if (changePaintType) {
                ((IPantableBlock)block).setPaintType(world, pos, paintType, facing);
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
        String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
        String colourText = TranslateUtils.translate("item.armourersworkshop:rollover.colour", c.getRGB());
        String hexText = TranslateUtils.translate("item.armourersworkshop:rollover.hex", hex);
        String paintText = TranslateUtils.translate("item.armourersworkshop:rollover.paintType", paintType.getLocalizedName());
        
        list.add(colourText);
        list.add(hexText);
        list.add(paintText);
        list.add(TranslateUtils.translate("item.armourersworkshop:rollover.openSettings"));
    }*/

    @Override
    public void getToolOptions(ArrayList<AbstractToolOption> toolOptionList) {
        toolOptionList.add(ToolOptions.CHANGE_HUE);
        toolOptionList.add(ToolOptions.CHANGE_SATURATION);
        toolOptionList.add(ToolOptions.CHANGE_BRIGHTNESS);
        toolOptionList.add(ToolOptions.CHANGE_PAINT_TYPE);
        toolOptionList.add(ToolOptions.FULL_BLOCK_MODE);
    }
}
