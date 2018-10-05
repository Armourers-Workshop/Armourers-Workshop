package moe.plushie.armourers_workshop.common.items.paintingtool;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.painting.IPaintingTool;
import moe.plushie.armourers_workshop.common.items.AbstractModItem;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.painting.PaintType;
import moe.plushie.armourers_workshop.common.painting.PaintingHelper;
import moe.plushie.armourers_workshop.common.painting.tool.IConfigurableTool;
import moe.plushie.armourers_workshop.common.painting.tool.ToolOption;
import moe.plushie.armourers_workshop.common.painting.tool.ToolOptions;
import net.minecraft.item.ItemStack;

public class ItemColourPicker extends AbstractModItem implements IPaintingTool, IConfigurableTool {
    
    public ItemColourPicker() {
        super(LibItemNames.COLOUR_PICKER);
        setSortPriority(12);
    }
    /*
    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        PaintType paintType = PaintingHelper.getToolPaintType(stack);
        if (paintType != PaintType.NORMAL) {
            return true;
        }
        return false;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
            int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);
        
        boolean changePaintType = (boolean) ToolOptions.CHANGE_PAINT_TYPE.readFromNBTBool(stack.getTagCompound());
        PaintType paintType = getToolPaintType(stack);
        
        if (player.isSneaking() & block == ModBlocks.colourMixer & getToolHasColour(stack)) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof IPantable) {
                if (!world.isRemote) {
                    int colour = getToolColour(stack);;
                    ((IPantable)te).setColour(colour);
                    ((IPantable)te).setPaintType(paintType, 0);
                }
            }
            return true;
        }
        
        if (block instanceof IPantableBlock) {
            IPantableBlock paintable = (IPantableBlock) block;
            PaintType targetPaintType = paintable.getPaintType(world, x, y, z, side);
            
            if (paintable.isRemoteOnly(world, x, y, z, side) & world.isRemote) {
                int colour = paintable.getColour(world, x, y, z, side);
                NBTTagCompound compound = new NBTTagCompound();
                byte[] paintData = new byte[4];
                Color c = new Color(colour);
                paintData[0] = (byte) c.getRed();
                paintData[1] = (byte) c.getGreen();
                paintData[2] = (byte) c.getBlue();
                if (changePaintType) {
                    paintData[3] = (byte) targetPaintType.getKey();
                } else {
                    paintData[3] = (byte) paintType.getKey();
                }
                
                PaintingHelper.setPaintData(compound, paintData);
                PacketHandler.networkWrapper.sendToServer(new MessageClientGuiToolOptionUpdate(compound));
            } else if (!paintable.isRemoteOnly(world, x, y, z, side) & !world.isRemote) {
                setToolColour(stack, ((IPantableBlock)block).getColour(world, x, y, z, side));
                if (changePaintType) {
                    setToolPaintType(stack, targetPaintType);
                } else {
                    setToolPaintType(stack, paintType);
                }
            }
            
            if (!world.isRemote) {
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.PICKER, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            }
            return true;
        }
        
        return false;
    }*/
    /*
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        super.addInformation(stack, player, list, p_77624_4_);
        if (getToolHasColour(stack)) {
            Color c = new Color(getToolColour(stack));
            PaintType paintType = getToolPaintType(stack);
            String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
            String colourText = TranslateUtils.translate("item.armourersworkshop:rollover.colour", c.getRGB());
            String hexText = TranslateUtils.translate("item.armourersworkshop:rollover.hex", hex);
            String paintText = TranslateUtils.translate("item.armourersworkshop:rollover.paintType", paintType.getLocalizedName());
            
            list.add(colourText);
            list.add(hexText);
            list.add(paintText);
        } else {
            String noPaint = TranslateUtils.translate("item.armourersworkshop:rollover.nopaint");
            list.add(noPaint);
        }
    }
    */
    
    @Override
    public boolean getToolHasColour(ItemStack stack) {
        return PaintingHelper.getToolHasPaint(stack);
    }

    @Override
    public int getToolColour(ItemStack stack) {
        return PaintingHelper.getToolPaintColourRGB(stack);
    }

    @Override
    public void setToolColour(ItemStack stack, int colour) {
        PaintingHelper.setToolPaintColour(stack, colour);
    }
    /*
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote & player.isSneaking()) {
            player.openGui(ArmourersWorkshop.instance, LibGuiIds.TOOL_OPTIONS, world, 0, 0, 0);
        }
        return stack;
    }*/
    
    @Override
    public void getToolOptions(ArrayList<ToolOption<?>> toolOptionList) {
        toolOptionList.add(ToolOptions.CHANGE_PAINT_TYPE);
    }
    
    @Override
    public void setToolPaintType(ItemStack stack, PaintType paintType) {
        PaintingHelper.setToolPaint(stack, paintType);
    }
    
    @Override
    public PaintType getToolPaintType(ItemStack stack) {
        return PaintingHelper.getToolPaintType(stack) ;
    }
}
