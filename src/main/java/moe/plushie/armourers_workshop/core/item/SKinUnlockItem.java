package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

@SuppressWarnings("NullableProblems")
public class SKinUnlockItem extends FlavouredItem {

    private final SkinSlotType slotType;

    public SKinUnlockItem(SkinSlotType slotType, Properties properties) {
        super(properties);
        this.slotType = slotType;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (world.isClientSide) {
            return ActionResult.consume(itemStack);
        }
        ISkinType skinType = slotType.getSkinType();
        SkinWardrobe wardrobe = SkinWardrobe.of(player);
        if (wardrobe == null || skinType == null) {
            return ActionResult.fail(itemStack);
        }
        ITextComponent skinName = new TranslationTextComponent("skinType." + skinType.getRegistryName());
        if (wardrobe.getUnlockedSize(slotType) >= slotType.getMaxSize()) {
            player.sendMessage(new TranslationTextComponent("chat.armourers_workshop.slotUnlockedFailed", skinName), player.getUUID());
            return ActionResult.fail(itemStack);
        }
        itemStack.shrink(1);
        int count = wardrobe.getUnlockedSize(slotType) + 1;
        wardrobe.setUnlockedSize(slotType, count);
        wardrobe.sendToAll();
        player.sendMessage(new TranslationTextComponent("chat.armourers_workshop.slotUnlocked", skinName, Integer.toString(count)), player.getUUID());
        return ActionResult.success(itemStack);
    }
}
