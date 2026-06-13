package moe.plushie.armourers_workshop.core.client.special;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class SpecialModelRenderer<T> {

    protected abstract void abi$render(T data, OpenItemDisplayContext itemDisplayContext, int lightmap, int overlay, IGraphicsContext context);

    protected abstract T abi$extractArgument(ItemStack itemStack);

    public final void render(@Nullable T data, OpenItemDisplayContext itemDisplayContext, int lightmap, int overlay, IGraphicsContext context) {
        abi$render(data, itemDisplayContext, lightmap, overlay, context);
    }

    public final T extractArgument(ItemStack itemStack) {
        return abi$extractArgument(itemStack);
    }
}
