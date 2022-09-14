package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface IItemModelProperty {

    float getValue(ItemStack itemStack, @Nullable Level level, @Nullable LivingEntity entity, int id);
}
