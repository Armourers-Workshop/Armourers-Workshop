package moe.plushie.armourers_workshop.core.client.render.model;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainer;

import java.util.HashSet;

@OnlyIn(Dist.CLIENT)
public class LinkedModel<S> extends PlaceholderModel<S, LinkedModelPart> {

    private final IEntityModel<S> parent;
    private IEntityModel<S> target;

    public LinkedModel(IEntityModel<S> parent) {
        this.parent = parent;
    }

    public void linkTo(IEntityModel<S> target) {
        if (this.target == target) {
            return;
        }
        var exists = new HashSet<IModelPart>();
        this.target = target;
        this.allParts.clear();
        // link named parts.
        this.namedParts.forEach((key, value) -> {
            var part = target.abi$getPartByName(key);
            value.linkTo(part);
            allParts.add(value);
            exists.add(part);
        });
        // link unnamed parts.
        for (var part : target.abi$allParts()) {
            if (!exists.contains(part)) {
                var linkedPart = new LinkedModelPart(null, part);
                linkedPart.linkTo(part);
                allParts.add(linkedPart);
            }
        }
    }

    @Override
    protected LinkedModelPart createPart(String name) {
        IModelPart part = null;
        if (parent != null) {
            part = parent.abi$getPartByName(name);
        }
        return new LinkedModelPart(name, part);
    }

    public IEntityModel<?> parent() {
        return parent;
    }

    @Override
    public <T> T getAssociatedObject(IAssociatedContainer.Key<T> key) {
        // get the object form the parent container.
        if (parent instanceof IAssociatedContainer container) {
            return container.getAssociatedObject(key);
        }
        return super.getAssociatedObject(key);
    }

    @Override
    public <T> void setAssociatedObject(IAssociatedContainer.Key<T> key, T value) {
        // set the object into parent container.
        if (parent instanceof IAssociatedContainer container) {
            container.setAssociatedObject(key, value);
        }
        super.setAssociatedObject(key, value);
    }
}
