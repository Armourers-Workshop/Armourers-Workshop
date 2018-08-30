package riskyken.armourersWorkshop.common.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.common.blocks.BlockLocation;
import riskyken.armourersWorkshop.common.blocks.BlockSkinnable;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;

public class ItemLinkingTool extends AbstractModItem {

    private static final String TAG_LINK_LOCATION = "linkLocation";
    
    public ItemLinkingTool() {
        super(LibItemNames.LINKING_TOOL);
        setSortPriority(7);
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
                    player.sendMessage(new TextComponentTranslation("chat.armourersworkshop:linkingTool.start", (Object)null));
                    return true;
                } else {
                    player.sendMessage(new TextComponentTranslation("chat.armourersworkshop:linkingTool.linkedToSkinnable", (Object)null));
                    return true;
                }
            } else {
                BlockLocation loc = getLinkLocation(stack);
                Block block = world.getBlock(x, y, z);
                if (block instanceof BlockSkinnable) {
                    TileEntity te = world.getTileEntity(x, y, z);
                    if (te != null && te instanceof TileEntitySkinnable) {
                        ((TileEntitySkinnable)te).getParent().setLinkedBlock(loc);
                        player.sendMessage(new TextComponentTranslation("chat.armourersworkshop:linkingTool.finish", (Object)null));
                        removeLinkLocation(stack);
                        return true;
                    }
                }
            }
            removeLinkLocation(stack);
            player.addChatMessage(new TextComponentTranslation("chat.armourersworkshop:linkingTool.fail", (Object)null));
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
