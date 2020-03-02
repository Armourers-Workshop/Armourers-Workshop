package moe.plushie.armourers_workshop.common.init.items.paintingtool;

import moe.plushie.armourers_workshop.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.api.common.painting.IPaintingTool;
import moe.plushie.armourers_workshop.common.init.items.AbstractModItem;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.painting.PaintingHelper;
import net.minecraft.item.ItemStack;

public class ItemPaintballGun extends AbstractModItem implements IPaintingTool {
    
    public ItemPaintballGun() {
        super(LibItemNames.PAINTBALL_GUN);
        setSortPriority(15);
    }
    /*
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        //TODO Add paintball sound.
        world.playSoundAtEntity(entityPlayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        return itemStack;
    }
    
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
        return false;
    }*/
    /*
    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        PaintType paintType = PaintingHelper.getToolPaintType(stack);
        if (paintType != PaintType.NORMAL) {
            return true;
        }
        return false;
    }*/
    
    @Override
    public boolean getToolHasColour(ItemStack stack) {
        return true;
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
    public void setToolPaintType(ItemStack stack, IPaintType paintType) {
        PaintingHelper.setToolPaint(stack, paintType);
    }
    
    @Override
    public IPaintType getToolPaintType(ItemStack stack) {
        return PaintingHelper.getToolPaintType(stack) ;
    }
}
