package moe.plushie.armourers_workshop.api.client.model;

import moe.plushie.armourers_workshop.api.data.IAssociatedContainer;
import moe.plushie.armourers_workshop.api.math.IVector3f;

import java.util.Collection;

public interface IModel extends IAssociatedContainer {

    default boolean isBaby() {
        return false;
    }

    default float getBabyScale() {
        return 1f;
    }

    default IVector3f getBabyOffset() {
        return null;
    }

    IModelPart getPart(String name);

    Collection<IModelPart> getAllParts();
}

