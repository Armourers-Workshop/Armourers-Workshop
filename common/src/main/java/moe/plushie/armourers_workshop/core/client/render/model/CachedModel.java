package moe.plushie.armourers_workshop.core.client.render.model;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class CachedModel<S> extends PlaceholderModel<S, IModelPart> {

    public void put(String key, IModelPart part) {
        namedParts.put(key, part);
        allParts.add(part);
    }

    public void unnamed(IModelPart part) {
        allParts.add(part);
    }

    @Override
    protected IModelPart createPart(String name) {
        return new PlaceholderModelPart(name);
    }
}
