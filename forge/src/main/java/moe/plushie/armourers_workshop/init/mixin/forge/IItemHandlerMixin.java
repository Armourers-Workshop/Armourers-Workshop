package moe.plushie.armourers_workshop.init.mixin.forge;

import moe.plushie.armourers_workshop.api.common.IItemHandler;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IItemHandler.class)
public interface IItemHandlerMixin extends IForgeItem {

    @Override
    default boolean onLeftClickEntity(ItemStack itemStack, Player player, Entity entity) {
        IItemHandler handler = ObjectUtils.unsafeCast(this);
        return handler.attackLivingEntity(itemStack, player, entity) != InteractionResult.PASS;
    }

    @Override
    default InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        IItemHandler handler = ObjectUtils.unsafeCast(this);
        return handler.useOnFirst(stack, context);
    }
}
