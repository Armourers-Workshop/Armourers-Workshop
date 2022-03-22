package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.core.utils.AWContainerOpener;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.container.SkinWardrobeContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;


@SuppressWarnings("NullableProblems")
public class WandOfStyleItem extends FlavouredItem {

    public WandOfStyleItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack itemStack, PlayerEntity player, Entity entity) {
        if (!player.level.isClientSide) {
            openGUI(player, entity);
        }
        return true;
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack itemStack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (!player.level.isClientSide) {
            openGUI(player, entity);
        }
        return ActionResultType.sidedSuccess(player.level.isClientSide);
    }

    private void openGUI(PlayerEntity player, Entity entity) {
        SkinWardrobe wardrobe = SkinWardrobe.of(entity);
        if (wardrobe != null && wardrobe.getProfile().canCustomize()) {
            AWContainerOpener.open(SkinWardrobeContainer.TYPE, player, wardrobe);
        }
    }
}
