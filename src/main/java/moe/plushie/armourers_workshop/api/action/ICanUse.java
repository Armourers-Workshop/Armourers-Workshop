package moe.plushie.armourers_workshop.api.action;

import com.google.common.collect.Range;

public interface ICanUse {

    Range<Integer> getUseRange();
}
