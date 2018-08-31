package riskyken.armourers_workshop.common.items.paintingtool;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourers_workshop.api.common.painting.IPaintingTool;
import riskyken.armourers_workshop.common.items.AbstractModItem;
import riskyken.armourers_workshop.common.painting.IBlockPainter;
import riskyken.armourers_workshop.common.painting.PaintType;
import riskyken.armourers_workshop.common.painting.PaintingHelper;

public abstract class AbstractPaintingTool extends AbstractModItem implements IPaintingTool, IBlockPainter {

    public AbstractPaintingTool(String name) {
        super(name);
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
    
    @SideOnly(Side.CLIENT)
    protected void spawnPaintParticles (World world, int x, int y, int z, int side, int colour) {
        /*
        for (int i = 0; i < 3; i++) {
            EntityFXPaintSplash particle = new EntityFXPaintSplash(world, x + 0.5D, y + 0.5D, z + 0.5D,
                    colour, EnumFacing.getFront(side));
            ParticleManager.INSTANCE.spawnParticle(world, particle);
        }
        */
    }
    /*
    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        if (pass == 0) {
            return super.getColorFromItemStack(stack, pass);
        }
        return getToolColour(stack);
    }
    
    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }
    */
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
    public void setToolPaintType(ItemStack stack, PaintType paintType) {
        PaintingHelper.setToolPaint(stack, paintType);
    }
    
    @Override
    public PaintType getToolPaintType(ItemStack stack) {
        return PaintingHelper.getToolPaintType(stack) ;
    }
}
