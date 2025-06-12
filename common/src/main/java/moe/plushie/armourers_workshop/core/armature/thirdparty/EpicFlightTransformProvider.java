package moe.plushie.armourers_workshop.core.armature.thirdparty;

import moe.plushie.armourers_workshop.core.data.DataContainerKey;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;

@FunctionalInterface
public interface EpicFlightTransformProvider {

    DataContainerKey<EpicFlightTransformProvider> KEY = DataContainerKey.of("transforms", EpicFlightTransformProvider.class);

    OpenPoseStack.Pose poseByName(String name);
}
