package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.item.ItemStack;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractInteractionHand;
import moe.plushie.armourers_workshop.compat.core.AbstractInteractionResult;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, )")
@Extension
public class OpenAPI {

    public static OpenInteractionResult interactLivingEntity(@This ItemStack itemStack, Player player, LivingEntity livingEntity, OpenInteractionHand hand, Object context) {
        return AbstractInteractionResult.wrap(itemStack.interactLivingEntity(player, livingEntity, AbstractInteractionHand.unwrap(hand)));
    }
}
