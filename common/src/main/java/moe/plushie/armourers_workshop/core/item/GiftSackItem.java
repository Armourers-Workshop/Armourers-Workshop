package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.core.holiday.Holiday;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModHolidays;
import moe.plushie.armourers_workshop.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GiftSackItem extends FlavouredItem {

    public GiftSackItem(Properties properties) {
        super(properties);
    }

    public static ItemStack of(Holiday holiday) {
        var stack = new ItemStack(ModItems.GIFT_SACK.get());
        stack.set(ModDataComponents.HOLIDAY.get(), holiday);
        if (holiday.handler() != null) {
            stack.set(ModDataComponents.GIFT_COLOR_BG.get(), holiday.handler().backgroundColor());
            stack.set(ModDataComponents.GIFT_COLOR_FG.get(), holiday.handler().foregroundColor());
        }
        return stack;
    }

    public static ItemStack getGift(ItemStack itemStack, Player player) {
        var holiday = itemStack.get(ModDataComponents.HOLIDAY.get());
        if (holiday != null && holiday.handler() != null) {
            return holiday.handler().getGift(player);
        }
        return itemStack.getOrDefault(ModDataComponents.GIFT.get(), ItemStack.EMPTY);
    }

    @Override
    protected OpenInteractionResult abi$use(Level level, Player player, OpenInteractionHand hand) {
        var itemStack = player.getItemInHand(hand);
        var giftStack = getGift(itemStack, player);
        if (giftStack.isEmpty()) {
            return OpenInteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            if (player.getInventory().add(giftStack)) {
                itemStack.shrink(1);
            } else {
                player.sendSystemMessage(Component.translatable("chat.armourers_workshop.inventoryFull"));
            }
        }
        return OpenInteractionResult.sidedSuccess(level.isClientSide()).heldItemTransformedTo(itemStack);
    }

    @Override
    protected void abi$fill(List<ItemStack> displayItems, CreativeModeTab tab) {
        super.abi$fill(displayItems, tab);
        // add all the gifts into creative inventory
        for (var holiday : ModHolidays.holidays()) {
            if (holiday.handler() != null) {
                displayItems.add(of(holiday));
            }
        }
    }

    @Override
    protected int abi$getModelTintColor(ItemStack itemStack, @Nullable Level level, @Nullable LivingEntity entity, int layerIndex) {
        if (layerIndex == 1) {
            return itemStack.getOrDefault(ModDataComponents.GIFT_COLOR_FG.get(), 0x333333) | 0xff000000;
        }
        return itemStack.getOrDefault(ModDataComponents.GIFT_COLOR_BG.get(), 0xffffff) | 0xff000000;
    }
}
