package moe.plushie.armourers_workshop.api.client.model;

import moe.plushie.armourers_workshop.api.data.IAssociatedContainer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IModel extends IAssociatedContainer {

    @Nullable
    IModelBabyPose getBabyPose();

    IModelPart getPart(String name);

    Collection<IModelPart> getAllParts();
}

