package moe.plushie.armourers_workshop.core.armature.thirdparty;

import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.utils.DataStorageKey;

public interface EpicFlightTransformProvider {

    DataStorageKey<EpicFlightTransformProvider> KEY = DataStorageKey.of("transforms", EpicFlightTransformProvider.class);

    IJointTransform apply(String name);
}
