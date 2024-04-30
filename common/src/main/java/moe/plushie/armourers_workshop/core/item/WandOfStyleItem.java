package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IItemHandler;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class WandOfStyleItem extends FlavouredItem implements IItemHandler {

    public WandOfStyleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult attackLivingEntity(ItemStack itemStack, Player player, Entity entity) {
        if (!player.getLevel().isClientSide()) {
            openGUI(player, entity);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity entity, InteractionHand hand) {
        if (!player.getLevel().isClientSide()) {
            openGUI(player, entity);
        }
        return InteractionResult.sidedSuccess(player.getLevel().isClientSide());
    }

    private void openGUI(Player player, Entity entity) {
        SkinWardrobe wardrobe = SkinWardrobe.of(entity);
        if (wardrobe != null && wardrobe.isEditable(player)) {
            ModMenuTypes.WARDROBE.get().openMenu(player, wardrobe);
        }
    }
}
