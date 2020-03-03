package moe.plushie.armourers_workshop.common.init.items;

import moe.plushie.armourers_workshop.common.holiday.Holiday;
import moe.plushie.armourers_workshop.common.holiday.ModHolidays;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.utils.NBTHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public class ItemGiftSack extends AbstractModItem {

    public static final String TAG_COLOUR_1 = "colour1";
    public static final String TAG_COLOUR_2 = "colour2";
    public static final String TAG_GIFT_ITEM = "giftItem";
    public static final String TAG_HOLIDAY = "holiday";
    
    public ItemGiftSack() {
        super(LibItemNames.GIFT_SACK);
    }
    
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            super.getSubItems(tab, items);
            for (Holiday holiday : ModHolidays.getHolidays()) {
                if (holiday.hasGiftSack()) {
                    items.add(holiday.getGiftSack());
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_COLOUR_1, NBT.TAG_INT)) {
                return stack.getTagCompound().getInteger(TAG_COLOUR_1);
            }
        }
        if (tintIndex == 1) {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_COLOUR_2, NBT.TAG_INT)) {
                return stack.getTagCompound().getInteger(TAG_COLOUR_2);
            }
        }
        if (tintIndex == 1) {
            return 0x333333;
        }
        return 0xFFFFFF;
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            if (itemStack.hasTagCompound()) {
                if (itemStack.getTagCompound().hasKey(TAG_GIFT_ITEM, NBT.TAG_COMPOUND)) {
                    ItemStack giftStack = NBTHelper.readStackfromNBT(itemStack.getTagCompound(), TAG_GIFT_ITEM);
                    if (!giftStack.isEmpty()) {
                        if (playerIn.inventory.addItemStackToInventory(giftStack)) {
                            itemStack.shrink(1);
                        } else {
                            playerIn.sendMessage(new TextComponentTranslation("chat.armourersworkshop:inventoryFull"));
                        }
                    }
                }
                if (itemStack.getTagCompound().hasKey(TAG_HOLIDAY, NBT.TAG_STRING)) {
                    Holiday holiday =  ModHolidays.getHoliday(itemStack.getTagCompound().getString(TAG_HOLIDAY));
                    if (holiday != null) {
                        ItemStack giftStack = holiday.getGift(playerIn);
                        if (!giftStack.isEmpty()) {
                            if (playerIn.inventory.addItemStackToInventory(giftStack)) {
                                itemStack.shrink(1);
                            } else {
                                playerIn.sendMessage(new TextComponentTranslation("chat.armourersworkshop:inventoryFull"));
                            }
                        }
                    }
                }
            }
        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStack);
    }
    
    public static ItemStack createStack(int colour1, int colour2, Holiday holiday) {
        ItemStack stack = new ItemStack(ModItems.GIFT_SACK);
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger(TAG_COLOUR_1, colour1);
        stack.getTagCompound().setInteger(TAG_COLOUR_2, colour2);
        stack.getTagCompound().setString(TAG_HOLIDAY, holiday.getName());
        return stack;
    }
    
    public static ItemStack createStack(int colour1, int colour2, ItemStack gift) {
        ItemStack stack = new ItemStack(ModItems.GIFT_SACK);
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger(TAG_COLOUR_1, colour1);
        stack.getTagCompound().setInteger(TAG_COLOUR_2, colour2);
        NBTHelper.writeStackToNBT(stack.getTagCompound(), TAG_GIFT_ITEM, gift);
        return stack;
    }
}
