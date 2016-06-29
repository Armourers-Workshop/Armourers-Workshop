package riskyken.armourersWorkshop.common.items;

import java.awt.Color;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.painting.IPaintingTool;
import riskyken.armourersWorkshop.api.common.painting.IPaintingTool;
import riskyken.armourersWorkshop.api.common.painting.IPantable;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.painting.PaintingHelper;
import riskyken.armourersWorkshop.utils.TranslateUtils;

public class ItemDyeBottle extends AbstractModItem implements IPaintingTool {

    public ItemDyeBottle() {
        super(LibItemNames.DYE_BOTTLE);
    }
    
    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world,
            BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        IBlockState blockState = world.getBlockState(pos);
        
        if (player.isSneaking() & blockState.getBlock() == ModBlocks.colourMixer) {
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof IPantable) {
                if (!world.isRemote) {
                    int colour = ((IPantable)te).getColour(side);
                    PaintType paintType = ((IPantable)te).getPaintType(side);
                    setToolColour(stack, colour);
                    setToolPaintType(stack, paintType);
                }
            }
            return EnumActionResult.PASS;
        }
        
        return EnumActionResult.FAIL;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        PaintType paintType = PaintingHelper.getToolPaintType(stack);
        return paintType != PaintType.NORMAL;
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
            String emptyText = TranslateUtils.translate("item.armourersworkshop:rollover.empty");
            list.add(emptyText);
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
    public void setToolPaintType(ItemStack stack, PaintType paintType) {
        PaintingHelper.setToolPaint(stack, paintType);
    }
    
    @Override
    public PaintType getToolPaintType(ItemStack stack) {
        return PaintingHelper.getToolPaintType(stack) ;
    }
}
