package moe.plushie.armourers_workshop.core.armature.thirdparty;

import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;

import java.util.HashMap;
import java.util.function.BiConsumer;

public class EpicFlightTransformProvider {

    private static final DataContainer.Key<EpicFlightTransformProvider> KEY = DataContainer.key("transforms", EpicFlightTransformProvider::new);

    private final HashMap<String, OpenPoseStack.Pose> transforms = new HashMap<>();

    public static EpicFlightTransformProvider of(IEntityModel<?> entityModel) {
        return DataContainer.of(entityModel, KEY);
    }

    public void linkTo(BiConsumer<String, OpenPoseStack.Pose> transformProvider) {
        if (transformProvider != null) {
            transforms.forEach(transformProvider);
        }
    }

    public OpenPoseStack.Pose byName(String name) {
        return transforms.computeIfAbsent(name, it -> new OpenPoseStack.Pose());
    }
}
