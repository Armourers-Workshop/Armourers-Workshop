package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.LivingEntity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.16, )")
public class HeldItemExt {

    public static ItemStack getItemInHand(@This LivingEntity entity, OpenInteractionHand hand) {
        return entity.getItemInHand(AbstractInteractionHand.unwrap(hand));
    }

    public static void setItemInHand(@This LivingEntity entity, OpenInteractionHand hand, ItemStack itemStack) {
        entity.setItemInHand(AbstractInteractionHand.unwrap(hand), itemStack);
    }
}
