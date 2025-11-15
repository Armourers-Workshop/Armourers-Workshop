package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.client.model.IModelPart;

import java.util.Collection;

public interface IEntityModel<S> {

    IModelPart abi$getPartByName(String name);

    Collection<? extends IModelPart> abi$allParts();

    interface Provider<S> {

        IEntityModel<S> abi$getEntityModel(S renderState);
    }
}
