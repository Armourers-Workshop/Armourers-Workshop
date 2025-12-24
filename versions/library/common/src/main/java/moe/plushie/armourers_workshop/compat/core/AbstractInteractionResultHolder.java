package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

@Available("[1.16, 1.26)")
public class AbstractInteractionResultHolder {

    public static OpenInteractionResult wrap(InteractionResultHolder<ItemStack> result) {
        var result1 = AbstractInteractionResult.wrap(result.getResult());
        if (result1 instanceof OpenInteractionResult.Success result2) {
            return result2.heldItemTransformedTo(result.getObject());
        }
        return result1;
    }

    public static InteractionResultHolder<ItemStack> unwrap(OpenInteractionResult result, Supplier<ItemStack> provider) {
        var result1 = AbstractInteractionResult.unwrap(result);
        var itemStack = getItemStack(result, provider);
        return new InteractionResultHolder<>(result1, itemStack);
    }

    private static ItemStack getItemStack(OpenInteractionResult result, Supplier<ItemStack> provider) {
        if (result instanceof OpenInteractionResult.Success result2) {
            var itemStack = result2.heldItemTransformedTo();
            if (itemStack != null) {
                return itemStack;
            }
        }
        return provider.get();
    }
}
