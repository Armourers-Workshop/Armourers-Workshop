package moe.plushie.armourers_workshop.compat.forge.mixin.core;

import moe.plushie.armourers_workshop.compat.core.AbstractInteractionResult;
import moe.plushie.armourers_workshop.compat.core.item.AbstractItemHandler;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeItem;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractItemHandler.class)
public interface ForgeItemHandlerMixin extends AbstractForgeItem {

    @Override
    default boolean onLeftClickEntity(ItemStack itemStack, Player player, Entity entity) {
        var handler = (AbstractItemHandler) Objects.unsafeCast(this);
        return handler.attackLivingEntity(itemStack, player, entity) != OpenInteractionResult.PASS;
    }

    @Override
    default InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        var handler = (AbstractItemHandler) Objects.unsafeCast(this);
        return AbstractInteractionResult.unwrap(handler.useOnFirst(stack, context));
    }
}
