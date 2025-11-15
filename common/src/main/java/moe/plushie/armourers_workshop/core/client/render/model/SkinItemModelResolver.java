package moe.plushie.armourers_workshop.core.client.render.model;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@OnlyIn(Dist.CLIENT)
public interface SkinItemModelResolver {

    SkinItemModel resolve(SkinItemModel itemModel, ItemStack itemStack, int flags, OpenItemDisplayContext displayContext);
}
