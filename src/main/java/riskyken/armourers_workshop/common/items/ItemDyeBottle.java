package riskyken.armourers_workshop.common.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import riskyken.armourers_workshop.api.common.painting.IPaintingTool;
import riskyken.armourers_workshop.api.common.painting.IPantable;
import riskyken.armourers_workshop.common.blocks.ModBlocks;
import riskyken.armourers_workshop.common.lib.LibItemNames;
import riskyken.armourers_workshop.common.painting.PaintType;
import riskyken.armourers_workshop.common.painting.PaintingHelper;

public class ItemDyeBottle extends AbstractModItem implements IPaintingTool {

    public ItemDyeBottle() {
        super(LibItemNames.DYE_BOTTLE);
        setSortPriority(11);
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        if (player.isSneaking() & state.getBlock() == ModBlocks.colourMixer) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te != null && te instanceof IPantable) {
                if (!worldIn.isRemote) {
                    ItemStack stack = player.getHeldItem(hand);
                    int colour = ((IPantable)te).getColour(0);
                    PaintType paintType = ((IPantable)te).getPaintType(0);
                    setToolColour(stack, colour);
                    setToolPaintType(stack, paintType);
                }
            }
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
    
    @Override
    public boolean hasEffect(ItemStack stack) {
        PaintType paintType = PaintingHelper.getToolPaintType(stack);
        if (paintType != PaintType.NORMAL) {
            return true;
        }
        return super.hasEffect(stack);
    }
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
            String emptyText = TranslateUtils.translate("item.armourersworkshop:rollover.empty");
            list.add(emptyText);
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
    
    @Override
    public void setToolPaintType(ItemStack stack, PaintType paintType) {
        PaintingHelper.setToolPaint(stack, paintType);
    }
    
    @Override
    public PaintType getToolPaintType(ItemStack stack) {
        return PaintingHelper.getToolPaintType(stack) ;
    }
}
