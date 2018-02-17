package riskyken.armourersWorkshop.common.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.blocks.BlockLocation;
import riskyken.armourersWorkshop.common.blocks.BlockSkinnable;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;

public class ItemLinkingTool extends AbstractModItem {

    private static final String TAG_LINK_LOCATION = "linkLocation";
    
    @SideOnly(Side.CLIENT)
    private IIcon linkIcon;
    
    public ItemLinkingTool() {
        super(LibItemNames.LINKING_TOOL);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(LibItemResources.LINKING_TOOL);
        linkIcon = iconRegister.registerIcon(LibItemResources.LINKING_TOOL_LINK);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconIndex(ItemStack stack) {
        if (hasLinkLocation(stack)) {
            return linkIcon;
        }
        return itemIcon;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        if (hasLinkLocation(stack)) {
            return linkIcon;
        }
        return itemIcon;
    }
    
    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return false;
        }
        if (!world.isRemote) {
            if (!hasLinkLocation(stack)) {
                Block block = world.getBlock(x, y, z);
                if (!(block instanceof BlockSkinnable)) {
                    setLinkLocation(stack, new BlockLocation(x, y, z));
                    player.addChatMessage(new ChatComponentTranslation("chat.armourersworkshop:linkingTool.start", (Object)null));
                    return true;
                } else {
                    player.addChatMessage(new ChatComponentTranslation("chat.armourersworkshop:linkingTool.linkedToSkinnable", (Object)null));
                    return true;
                }
            } else {
                BlockLocation loc = getLinkLocation(stack);
                Block block = world.getBlock(x, y, z);
                if (block instanceof BlockSkinnable) {
                    TileEntity te = world.getTileEntity(x, y, z);
                    if (te != null && te instanceof TileEntitySkinnable) {
                        ((TileEntitySkinnable)te).getParent().setLinkedBlock(loc);
                        player.addChatMessage(new ChatComponentTranslation("chat.armourersworkshop:linkingTool.finish", (Object)null));
                        removeLinkLocation(stack);
                        return true;
                    }
                }
            }
            removeLinkLocation(stack);
            player.addChatMessage(new ChatComponentTranslation("chat.armourersworkshop:linkingTool.fail", (Object)null));
        }
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        super.addInformation(itemStack, player, list, par4);
    }
    
    private void setLinkLocation(ItemStack stack, BlockLocation loc) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setIntArray(TAG_LINK_LOCATION, new int[] {loc.x, loc.y, loc.z});
    }
    
    private void removeLinkLocation(ItemStack stack) {
        if (stack.hasTagCompound()) {
            stack.getTagCompound().removeTag(TAG_LINK_LOCATION);
        }
    }
    
    private boolean hasLinkLocation(ItemStack stack) {
        if (stack.hasTagCompound()) {
            return stack.getTagCompound().hasKey(TAG_LINK_LOCATION, NBT.TAG_INT_ARRAY);
        }
        return false;
    }
    
    private BlockLocation getLinkLocation(ItemStack stack) {
        if (hasLinkLocation(stack)) {
            int[] loc = stack.getTagCompound().getIntArray(TAG_LINK_LOCATION);
            return new BlockLocation(loc[0], loc[1], loc[2]);
        }
        return new BlockLocation(0, 0, 0);
    }
}
