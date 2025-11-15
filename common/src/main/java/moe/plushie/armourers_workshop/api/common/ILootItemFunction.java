package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.Set;

public interface ILootItemFunction {

    ItemStack apply(ItemStack itemStack, ILootContext lootContext);

    default Set<? extends IContextKey<?>> getReferencedContextParams() {
        return Collections.emptySet();
    }
}
