package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SkinUnlockItem extends FlavouredItem {

    private final SkinSlotType slotType;

    public SkinUnlockItem(SkinSlotType slotType, Properties properties) {
        super(properties);
        this.slotType = slotType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.consume(itemStack);
        }
        ISkinType skinType = slotType.getSkinType();
        SkinWardrobe wardrobe = SkinWardrobe.of(player);
        if (wardrobe == null || skinType == null) {
            return InteractionResultHolder.fail(itemStack);
        }
        Component skinName = TranslateUtils.Name.of(skinType);
        if (wardrobe.getUnlockedSize(slotType) >= slotType.getMaxSize()) {
            player.sendSystemMessage(Component.translatable("chat.armourers_workshop.slotUnlockedFailed", skinName));
            return InteractionResultHolder.fail(itemStack);
        }
        itemStack.shrink(1);
        int count = wardrobe.getUnlockedSize(slotType) + 1;
        wardrobe.setUnlockedSize(slotType, count);
        wardrobe.broadcast();
        player.sendSystemMessage(Component.translatable("chat.armourers_workshop.slotUnlocked", skinName, Integer.toString(count)));
        return InteractionResultHolder.success(itemStack);
    }
}
