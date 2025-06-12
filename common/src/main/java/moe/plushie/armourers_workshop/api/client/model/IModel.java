package moe.plushie.armourers_workshop.api.client.model;

import moe.plushie.armourers_workshop.api.data.IAssociatedContainerProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IModel extends IAssociatedContainerProvider {

    @Nullable
    IModelBabyPose babyPose();

    IModelPart partByName(String name);

    Collection<? extends IModelPart> allParts();

    Class<?> type();
}
