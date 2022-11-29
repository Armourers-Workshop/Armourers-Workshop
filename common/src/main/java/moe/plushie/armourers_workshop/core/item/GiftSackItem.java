package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;
import moe.plushie.armourers_workshop.core.holiday.Holiday;
import moe.plushie.armourers_workshop.init.ModHolidays;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GiftSackItem extends FlavouredItem implements IItemTintColorProvider {

    public GiftSackItem(Properties properties) {
        super(properties);
    }

    public static ItemStack of(Holiday holiday) {
        ItemStack stack = new ItemStack(ModItems.GIFT_SACK.get());
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putString(Constants.Key.HOLIDAY, holiday.getName());
        if (holiday.getHandler() != null) {
            nbt.putInt(Constants.Key.COLOR_1, holiday.getHandler().getBackgroundColor());
            nbt.putInt(Constants.Key.COLOR_2, holiday.getHandler().getForegroundColor());
        }
        return stack;
    }

    public static ItemStack getGift(ItemStack itemStack, Player player) {
        Holiday holiday = getHoliday(itemStack);
        if (holiday != null && holiday.getHandler() != null) {
            return holiday.getHandler().getGift(player);
        }
        CompoundTag itemNBT = itemStack.getTagElement(Constants.Key.GIFT);
        if (itemNBT != null) {
            return ItemStack.of(itemNBT);
        }
        return ItemStack.EMPTY;
    }

    public static Holiday getHoliday(ItemStack itemStack) {
        CompoundTag nbt = itemStack.getTag();
        if (nbt != null) {
            return ModHolidays.byName(nbt.getString(Constants.Key.HOLIDAY));
        }
        return null;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        ItemStack giftStack = getGift(itemStack, player);
        if (giftStack.isEmpty()) {
            return InteractionResultHolder.pass(itemStack);
        }
        if (!level.isClientSide()) {
            if (player.getInventory().add(giftStack)) {
                itemStack.shrink(1);
            } else {
                player.sendSystemMessage(TranslateUtils.title("chat.armourers_workshop.inventoryFull"));
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public void fillItemCategory(CreativeModeTab creativeModeTab, NonNullList<ItemStack> nonNullList) {
        super.fillItemCategory(creativeModeTab, nonNullList);
        // add all the gifts into creative inventory
        if (this.allowdedIn(creativeModeTab)) {
            for (Holiday holiday : ModHolidays.getHolidays()) {
                if (holiday.getHandler() != null) {
                    nonNullList.add(of(holiday));
                }
            }
        }
    }

    @Override
    public int getTintColor(ItemStack itemStack, int index) {
        CompoundTag nbt = itemStack.getTag();
        if (index == 1) {
            return DataSerializers.getInt(nbt, Constants.Key.COLOR_2, 0x333333) | 0xff000000;
        }
        return DataSerializers.getInt(nbt, Constants.Key.COLOR_1, 0xffffff) | 0xff000000;
    }
}
