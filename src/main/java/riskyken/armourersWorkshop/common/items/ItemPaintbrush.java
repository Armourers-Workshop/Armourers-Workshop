package riskyken.armourersWorkshop.common.items;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.IWorldColourable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPaintbrush extends AbstractModItem {

    private static final String TAG_COLOUR = "colour";
    
    public ItemPaintbrush() {
        super(LibItemNames.PAINTBRUSH);
        setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    private IIcon tipIcon;
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibModInfo.ID + ":" + "paintbrush");
        tipIcon = register.registerIcon(LibModInfo.ID + ":" + "paintbrush-tip");
    }
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
            int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);
        
        if (!brushHasPaint(stack)) {
            return false;
        }
        
        if (block instanceof IWorldColourable) {
            ((IWorldColourable)block).setColour(world, x, y, z, getBrushColour(stack));
            return true;
        }
        return false;
    }
    
    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }
    
    @Override
    public int getRenderPasses(int metadata) {
        return 2;
    }
    
    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        if (pass == 0) {
            return super.getColorFromItemStack(stack, pass);
        }
        return getBrushColour(stack);
    }
    
    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        if (pass == 0) {
            return itemIcon;
        }
        return tipIcon;
    }
    
    private static boolean brushHasPaint(ItemStack stack) {
        NBTTagCompound compound = getCompound(stack);
        if (compound.hasKey(TAG_COLOUR)) {
            return true;
        }
        return false;
    }
    
    private static int getBrushColour(ItemStack stack) {
        NBTTagCompound compound = getCompound(stack);
        if (compound.hasKey(TAG_COLOUR)) {
            compound.getInteger(TAG_COLOUR);
        }
        return 16777215;
    }
    
    private static void setBrushColour(ItemStack stack, int colour) {
        NBTTagCompound compound = getCompound(stack);
        compound.setInteger(TAG_COLOUR, colour);
    }
    
    private static NBTTagCompound getCompound(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }
}
