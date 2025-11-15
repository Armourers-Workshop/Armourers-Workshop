package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.LivingEntity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.UseAnim;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.16, 1.22)")
public class EatingStatusExt {

    public static boolean isEating(@This LivingEntity entity) {
        return entity.getUseItem().getUseAnimation() == UseAnim.EAT;
    }
}
