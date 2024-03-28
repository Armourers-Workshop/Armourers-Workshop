package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelBabyPose;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.utils.DataStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;

public class LinkedModel implements IModel {

    private final IModel parent;
    private final DataStorage storage = new DataStorage();
    private final HashMap<String, LinkedModelPart> namedParts = new HashMap<>();

    private IModel target;

    public LinkedModel(IModel parent) {
        this.parent = parent;
    }

    public void linkTo(IModel target) {
        if (this.target != target) {
            this.target = target;
            this.namedParts.forEach((key, value) -> value.linkTo(target.getPart(key)));
        }
    }

    @Nullable
    @Override
    public IModelBabyPose getBabyPose() {
        if (target != null) {
            return target.getBabyPose();
        }
        if (parent != null) {
            return parent.getBabyPose();
        }
        return null;
    }

    @Override
    public LinkedModelPart getPart(String name) {
        return namedParts.computeIfAbsent(name, it -> {
            IModelPart part = null;
            if (parent != null) {
                part = parent.getPart(name);
            }
            return new LinkedModelPart(part);
        });
    }

    @Override
    public Collection<? extends IModelPart> getAllParts() {
        if (parent != null) {
            return parent.getAllParts();
        }
        if (target != null) {
            return target.getAllParts();
        }
        return null;
    }

    @Override
    public Class<?> getType() {
        if (parent != null) {
            return parent.getType();
        }
        return getClass();
    }

    @Override
    public <T> T getAssociatedObject(IAssociatedContainerKey<T> key) {
        if (parent != null) {
            return parent.getAssociatedObject(key);
        } else {
            return storage.getAssociatedObject(key);
        }
    }

    @Override
    public <T> void setAssociatedObject(T value, IAssociatedContainerKey<T> key) {
        if (parent != null) {
            parent.setAssociatedObject(value, key);
        } else {
            storage.setAssociatedObject(value, key);
        }
    }
}
