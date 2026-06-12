package moe.plushie.armourers_workshop.core.client.item.tintsource;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface ItemTintSource {

    int calculate(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity);

    IDataMapCodec<? extends ItemTintSource> type();
}
