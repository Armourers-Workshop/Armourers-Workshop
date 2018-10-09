package moe.plushie.armourers_workshop.common.items;

import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGiftSack extends AbstractModItem {

    public ItemGiftSack() {
        super(LibItemNames.GIFT_SACK);
    }
    
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            return 0xFF0000;
        }
        if (tintIndex == 1) {
            return 0x00FF00;
        }
        return 0xFFFFFFFF;
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStack);
    }
    /*
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            if (stack.getItemDamage() == 1000) {
                ItemStack giftStack = new ItemStack(ModBlocks.doll, 1);
                NBTTagCompound profileTag = new NBTTagCompound();
                NBTUtil.writeGameProfile(profileTag, player.getGameProfile());
                giftStack.setTagCompound(new NBTTagCompound());
                giftStack.getTagCompound().setTag(TAG_OWNER, profileTag);
                if (player.inventory.addItemStackToInventory(giftStack)) {
                    stack.stackSize--;
                } else {
                    player.sendMessage(new TextComponentString(I18n.format("chat.armourersworkshop:inventoryFull")));
                }
            }
        }
        return super.onItemRightClick(stack, world, player);
    }
*/
}
