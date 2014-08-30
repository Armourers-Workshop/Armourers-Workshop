package riskyken.armourersWorkshop.common.items;

import java.util.List;

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

public class ItemPaintbrush extends AbstractModItem implements IColourTool {

    private static final String TAG_COLOUR = "colour";
    
    public ItemPaintbrush() {
        super(LibItemNames.PAINTBRUSH);
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
        
        if (!getToolHasColour(stack)) {
            return false;
        }
        
        if (block instanceof IWorldColourable) {
            if (!world.isRemote) {
                ((IWorldColourable)block).setColour(world, x, y, z, getToolColour(stack));
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        super.addInformation(stack, player, list, p_77624_4_);
        if (getToolHasColour(stack)) {
            list.add("Colour " + getToolColour(stack));
        } else {
            list.add("No paint");
        }
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
        return getToolColour(stack);
    }
    
    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        if (pass == 0) {
            return itemIcon;
        }
        return tipIcon;
    }
    
    private NBTTagCompound getCompound(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    @Override
    public boolean getToolHasColour(ItemStack stack) {
        NBTTagCompound compound = getCompound(stack);
        if (compound.hasKey(TAG_COLOUR)) {
            return true;
        }
        return false;
    }

    @Override
    public int getToolColour(ItemStack stack) {
        NBTTagCompound compound = getCompound(stack);
        if (compound.hasKey(TAG_COLOUR)) {
            return compound.getInteger(TAG_COLOUR);
        }
        return 16777215;
    }

    @Override
    public void setToolColour(ItemStack stack, int colour) {
        NBTTagCompound compound = getCompound(stack);
        compound.setInteger(TAG_COLOUR, colour);
    }
}
