package moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind;

import moe.plushie.armourers_workshop.compatibility.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.EnchantmentSelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.ItemSelector;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemSelectorImpl implements ItemSelector {

    protected ItemStack itemStack;

    protected EnchantmentSelectorImpl enchantmentSelector = new EnchantmentSelectorImpl();

    public ItemSelectorImpl apply(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    @Override
    public String id() {
        return AbstractRegistryManager.getItemKey(itemStack.getItem());
    }

    @Override
    public int damage() {
        return itemStack.getDamageValue();
    }

    @Override
    public int maxDamage() {
        return itemStack.getMaxDamage();
    }

    @Nullable
    @Override
    public EnchantmentSelector enchantmentByName(String name) {
        var enchantment = AbstractRegistryManager.getEnchantment(itemStack, name);
        if (enchantment != null) {
            return enchantmentSelector.apply(enchantment);
        }
        return null;
    }

    @Override
    public boolean hasTag(String name) {
        return AbstractRegistryManager.hasItemTag(itemStack, name);
    }
}

