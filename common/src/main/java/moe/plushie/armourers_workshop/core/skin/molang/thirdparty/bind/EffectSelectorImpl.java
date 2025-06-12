package moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind;

import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.EffectSelector;
import net.minecraft.world.effect.MobEffectInstance;

public class EffectSelectorImpl implements EffectSelector {

    protected MobEffectInstance effect;

    public EffectSelectorImpl apply(MobEffectInstance effect) {
        this.effect = effect;
        return this;
    }

    @Override
    public int level() {
        return effect.getAmplifier() + 1;
    }
}
