package moe.plushie.armourers_workshop.core.armature.thirdparty;

import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.utils.DataStorageKey;

public interface EpicFlightTransformProvider {

    DataStorageKey<EpicFlightTransformProvider> KEY = DataStorageKey.of("transforms", EpicFlightTransformProvider.class);

    ITransformf apply(String name);
}
