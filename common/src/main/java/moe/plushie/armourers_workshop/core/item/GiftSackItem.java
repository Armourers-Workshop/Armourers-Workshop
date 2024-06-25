package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IItemGroup;
import moe.plushie.armourers_workshop.api.common.IItemGroupProvider;
import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;
import moe.plushie.armourers_workshop.core.holiday.Holiday;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModHolidays;
import moe.plushie.armourers_workshop.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class GiftSackItem extends FlavouredItem implements IItemGroupProvider, IItemTintColorProvider {

    public GiftSackItem(Properties properties) {
        super(properties);
    }

    public static ItemStack of(Holiday holiday) {
        var stack = new ItemStack(ModItems.GIFT_SACK.get());
        stack.set(ModDataComponents.HOLIDAY.get(), holiday);
        if (holiday.getHandler() != null) {
            stack.set(ModDataComponents.GIFT_COLOR_BG.get(), holiday.getHandler().getBackgroundColor());
            stack.set(ModDataComponents.GIFT_COLOR_FG.get(), holiday.getHandler().getForegroundColor());
        }
        return stack;
    }

    public static ItemStack getGift(ItemStack itemStack, Player player) {
        var holiday = itemStack.get(ModDataComponents.HOLIDAY.get());
        if (holiday != null && holiday.getHandler() != null) {
            return holiday.getHandler().getGift(player);
        }
        return itemStack.getOrDefault(ModDataComponents.GIFT.get(), ItemStack.EMPTY);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);
        var giftStack = getGift(itemStack, player);
        if (giftStack.isEmpty()) {
            return InteractionResultHolder.pass(itemStack);
        }
        if (!level.isClientSide()) {
            if (player.getInventory().add(giftStack)) {
                itemStack.shrink(1);
            } else {
                player.sendSystemMessage(Component.translatable("chat.armourers_workshop.inventoryFull"));
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public void fillItemGroup(List<ItemStack> results, IItemGroup group) {
        // add all the gifts into creative inventory
        for (Holiday holiday : ModHolidays.getHolidays()) {
            if (holiday.getHandler() != null) {
                results.add(of(holiday));
            }
        }
    }

    @Override
    public int getTintColor(ItemStack itemStack, int index) {
        if (index == 1) {
            return itemStack.getOrDefault(ModDataComponents.GIFT_COLOR_FG.get(), 0x333333) | 0xff000000;
        }
        return itemStack.getOrDefault(ModDataComponents.GIFT_COLOR_BG.get(), 0xffffff) | 0xff000000;
    }
}
