package moe.plushie.armourers_workshop.core.client.render.model;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainer;
import moe.plushie.armourers_workshop.core.data.DataContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public abstract class PlaceholderModel<S, P extends IModelPart> implements IEntityModel<S>, IAssociatedContainer {

    protected final ArrayList<P> allParts = new ArrayList<>();
    protected final HashMap<String, P> namedParts = new HashMap<>();
    protected final DataContainer storage = new DataContainer();

    protected P createPart(String name) {
        return null;
    }

    @Override
    public P abi$getPartByName(String name) {
        P part = namedParts.get(name);
        if (part != null) {
            return part;
        }
        part = createPart(name);
        if (part != null) {
            namedParts.put(name, part);
            allParts.add(part);
            return part;
        }
        return null;
    }

    @Override
    public Collection<? extends P> abi$allParts() {
        return allParts;
    }

    @Override
    public <T> T getAssociatedObject(IAssociatedContainer.Key<T> key) {
        return storage.getAssociatedObject(key);
    }

    @Override
    public <T> void setAssociatedObject(IAssociatedContainer.Key<T> key, T value) {
        storage.setAssociatedObject(key, value);
    }
}
