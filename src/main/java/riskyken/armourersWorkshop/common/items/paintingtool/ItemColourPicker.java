package riskyken.armourersWorkshop.common.items.paintingtool;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.painting.IPaintingTool;
import riskyken.armourersWorkshop.api.common.painting.IPantable;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.items.AbstractModItem;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiToolOptionUpdate;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.painting.PaintingHelper;
import riskyken.armourersWorkshop.common.painting.tool.AbstractToolOption;
import riskyken.armourersWorkshop.common.painting.tool.IConfigurableTool;
import riskyken.armourersWorkshop.utils.TranslateUtils;

public class ItemColourPicker extends AbstractModItem implements IPaintingTool, IConfigurableTool {
    
    public ItemColourPicker() {
        super(LibItemNames.COLOUR_PICKER);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        PaintType paintType = PaintingHelper.getToolPaintType(stack);
        if (paintType != PaintType.NORMAL) {
            return true;
        }
        return false;
    }
    
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn,
            BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        
        IBlockState blockState = worldIn.getBlockState(pos);
        
        if (playerIn.isSneaking() & blockState.getBlock() == ModBlocks.colourMixer & getToolHasColour(stack)) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te != null && te instanceof IPantable) {
                if (!worldIn.isRemote) {
                    int colour = getToolColour(stack);
                    PaintType paintType = getToolPaintType(stack);
                    ((IPantable)te).setColour(colour);
                    ((IPantable)te).setPaintType(paintType, 0);
                }
            }
            return EnumActionResult.PASS;
        }
        
        if (blockState.getBlock() instanceof IPantableBlock) {
            IPantableBlock paintable = (IPantableBlock) blockState.getBlock();
            PaintType paintType = paintable.getPaintType(worldIn, pos, facing);
            
            if (paintable.isRemoteOnly(worldIn, pos, facing) & worldIn.isRemote) {
                int colour = paintable.getColour(worldIn, pos, facing);
                NBTTagCompound compound = new NBTTagCompound();
                byte[] paintData = new byte[4];
                Color c = new Color(colour);
                paintData[0] = (byte) c.getRed();
                paintData[1] = (byte) c.getGreen();
                paintData[2] = (byte) c.getBlue();
                paintData[3] = (byte) paintType.getKey();
                PaintingHelper.setPaintData(compound, paintData);
                PacketHandler.networkWrapper.sendToServer(new MessageClientGuiToolOptionUpdate(compound));
            } else if (!paintable.isRemoteOnly(worldIn, pos, facing) & !worldIn.isRemote) {
                setToolColour(stack, ((IPantableBlock)blockState.getBlock()).getColour(worldIn, pos, facing));
                setToolPaintType(stack, paintType);
            }
            
            if (!worldIn.isRemote) {
                //worldIn.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.PICKER, 1.0F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
            }
            return EnumActionResult.PASS;
        }
        
        return EnumActionResult.FAIL;
    }
    
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

    @Override
    public void getToolOptions(ArrayList<AbstractToolOption> toolOptionList) {
        //Only here to allow colour updates
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
