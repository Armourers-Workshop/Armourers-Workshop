package moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector;

import org.jetbrains.annotations.Nullable;

public interface ItemSelector {

    String id();

    int damage();

    int maxDamage();

    boolean hasTag(String tag);

    @Nullable
    EnchantmentSelector enchantmentByName(String name);
}
