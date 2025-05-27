package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface SkinItemProperty {

    float apply(ItemStack itemStack, @Nullable Entity entity, @Nullable Level level, int flags, OpenItemDisplayContext displayContext);
}
