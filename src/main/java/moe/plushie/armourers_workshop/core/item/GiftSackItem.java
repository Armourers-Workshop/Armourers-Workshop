package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;
import moe.plushie.armourers_workshop.core.holiday.Holiday;
import moe.plushie.armourers_workshop.core.holiday.Holidays;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModItems;
import moe.plushie.armourers_workshop.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

@SuppressWarnings("NullableProblems")
public class GiftSackItem extends FlavouredItem implements IItemTintColorProvider {

    public GiftSackItem(Properties properties) {
        super(properties);
    }

    public static ItemStack of(Holiday holiday) {
        ItemStack stack = new ItemStack(ModItems.GIFT_SACK);
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putString(AWConstants.NBT.HOLIDAY, holiday.getName());
        if (holiday.getHandler() != null) {
            nbt.putInt(AWConstants.NBT.COLOR_1, holiday.getHandler().getBackgroundColor());
            nbt.putInt(AWConstants.NBT.COLOR_2, holiday.getHandler().getForegroundColor());
        }
        return stack;
    }

    public static ItemStack getGift(ItemStack itemStack, PlayerEntity player) {
        Holiday holiday = getHoliday(itemStack);
        if (holiday != null && holiday.getHandler() != null) {
            return holiday.getHandler().getGift(player);
        }
        CompoundNBT itemNBT = itemStack.getTagElement(AWConstants.NBT.GIFT);
        if (itemNBT != null) {
            return ItemStack.of(itemNBT);
        }
        return ItemStack.EMPTY;
    }

    public static Holiday getHoliday(ItemStack itemStack) {
        CompoundNBT nbt = itemStack.getTag();
        if (nbt != null) {
            return Holidays.byName(nbt.getString(AWConstants.NBT.HOLIDAY));
        }
        return null;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        ItemStack giftStack = getGift(itemStack, player);
        if (giftStack.isEmpty()) {
            return ActionResult.pass(itemStack);
        }
        if (!world.isClientSide()) {
            if (player.inventory.add(giftStack)) {
                itemStack.shrink(1);
            } else {
                player.sendMessage(TranslateUtils.title("chat.armourers_workshop.inventoryFull"), player.getUUID());
            }
        }
        return ActionResult.sidedSuccess(itemStack, world.isClientSide());
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        super.fillItemCategory(group, items);
        // add all the gifts into creative inventory
        if (this.allowdedIn(group)) {
            for (Holiday holiday : Holidays.getHolidays()) {
                if (holiday.getHandler() != null) {
                    items.add(of(holiday));
                }
            }
        }
    }

    @Override
    public int getTintColor(ItemStack itemStack, int index) {
        CompoundNBT nbt = itemStack.getTag();
        if (index == 1) {
            return AWDataSerializers.getInt(nbt, AWConstants.NBT.COLOR_2, 0x333333) | 0xff000000;
        }
        return AWDataSerializers.getInt(nbt, AWConstants.NBT.COLOR_1, 0xffffff) | 0xff000000;
    }
}
