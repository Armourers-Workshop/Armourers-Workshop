package moe.plushie.armourers_workshop.common.items;

import moe.plushie.armourers_workshop.common.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.utils.HolidayHelper;
import moe.plushie.armourers_workshop.utils.HolidayHelper.Holiday;
import moe.plushie.armourers_workshop.utils.NBTHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
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
    
    public ItemGiftSack() {
        super(LibItemNames.GIFT_SACK);
    }
    
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(createStackForHoliday(HolidayHelper.christmas_season));
            items.add(createStackForHoliday(HolidayHelper.halloween_season));
            items.add(createStackForHoliday(HolidayHelper.valentins));
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
        return 0xFFFFFFFF;
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            if (itemStack.hasTagCompound()) {
                ItemStack giftStack = NBTHelper.readStackfromNBT(itemStack.getTagCompound(), TAG_GIFT_ITEM);
                if (!giftStack.isEmpty()) {
                    if (playerIn.inventory.addItemStackToInventory(giftStack)) {
                        itemStack.shrink(1);
                    } else {
                        playerIn.sendMessage(new TextComponentTranslation("chat.armourersworkshop:inventoryFull"));
                    }
                }
            }
        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStack);
    }
    
    public ItemStack createStackForHoliday(Holiday holiday) {
        ItemStack stack = ItemStack.EMPTY;
        if (holiday == HolidayHelper.christmas_season) {
            stack = new ItemStack(this);
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setInteger(TAG_COLOUR_1, 0xFF0000);
            stack.getTagCompound().setInteger(TAG_COLOUR_2, 0x00FF00);
            NBTHelper.writeStackToNBT(stack.getTagCompound(), TAG_GIFT_ITEM, new ItemStack(ModBlocks.doll));
        }
        if (holiday == HolidayHelper.halloween_season) {
            stack = new ItemStack(this);
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setInteger(TAG_COLOUR_1, 0xFFFFFF);
            stack.getTagCompound().setInteger(TAG_COLOUR_2, 0x000000);
            NBTHelper.writeStackToNBT(stack.getTagCompound(), TAG_GIFT_ITEM, new ItemStack(Blocks.PUMPKIN));
        }
        if (holiday == HolidayHelper.valentins) {
            stack = new ItemStack(this);
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setInteger(TAG_COLOUR_1, 0x000000);
            stack.getTagCompound().setInteger(TAG_COLOUR_2, 0xFFFFFF);
            NBTHelper.writeStackToNBT(stack.getTagCompound(), TAG_GIFT_ITEM, new ItemStack(Items.CAKE));
        }
        return stack;
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.SUCCESS;
    }
}
