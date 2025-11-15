package moe.plushie.armourers_workshop.core.client.render.model;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public interface SkinItemProperty {

    float call(ItemStack itemStack, @Nullable Entity entity, @Nullable Level level, int flags, OpenItemDisplayContext displayContext);
}
