package moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind;

import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.EnchantmentSelector;

public class EnchantmentSelectorImpl implements EnchantmentSelector {

    protected Object value;

    public EnchantmentSelectorImpl apply(Object value) {
        this.value = value;
        return this;
    }

    @Override
    public int level() {
        if (value instanceof Number number) {
            return number.intValue();
        }
        return 0;
    }
}
