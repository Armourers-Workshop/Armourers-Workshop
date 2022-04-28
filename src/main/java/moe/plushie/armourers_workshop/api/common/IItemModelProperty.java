package moe.plushie.armourers_workshop.api.common;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IItemModelProperty {

    float getValue(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity entity);
}
