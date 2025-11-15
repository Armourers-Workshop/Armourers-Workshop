package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class WandOfStyleItem extends FlavouredItem {

    public WandOfStyleItem(Properties properties) {
        super(properties);
    }

    @Override
    protected OpenInteractionResult abi$attackLivingEntity(ItemStack itemStack, Player player, Entity entity) {
        if (!player.level().isClientSide()) {
            openGUI(player, entity);
        }
        return OpenInteractionResult.SUCCESS;
    }

    @Override
    protected OpenInteractionResult abi$interactLivingEntity(ItemStack itemStack, Player player, LivingEntity entity, OpenInteractionHand hand) {
        if (!player.level().isClientSide()) {
            openGUI(player, entity);
        }
        return OpenInteractionResult.sidedSuccess(player.level().isClientSide());
    }

    private void openGUI(Player player, Entity entity) {
        var wardrobe = SkinWardrobe.of(entity);
        if (wardrobe != null && wardrobe.isEditable(player)) {
            player.openMenu(ModMenuTypes.WARDROBE, wardrobe);
        }
    }
}
