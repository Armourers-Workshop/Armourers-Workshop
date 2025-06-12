package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelBabyPose;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.compatibility.client.model.AbstractModelPartHolder;
import moe.plushie.armourers_workshop.core.data.DataContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;

@SuppressWarnings("unused")
public class CachedModel<P> implements IModel {

    private final Container<P> container;
    private final DataContainer storage = new DataContainer();

    public CachedModel(Container<P> container) {
        this.container = container;
    }

    @Override
    public IModelBabyPose babyPose() {
        return container.babyPose();
    }

    @Override
    public IModelPart partByName(String name) {
        return container.parts.get(name);
    }

    @Override
    public Collection<? extends IModelPart> allParts() {
        return container.values;
    }

    @Override
    public <T> T getAssociatedObject(IAssociatedContainerKey<T> key) {
        return storage.getAssociatedObject(key);
    }

    @Override
    public <T> void setAssociatedObject(IAssociatedContainerKey<T> key, T value) {
        storage.setAssociatedObject(key, value);
    }

    @Override
    public Class<?> type() {
        return container.type;
    }

    public static class Container<P> {

        protected IModelBabyPose babyPose;

        protected final Class<?> type;
        protected final Function<P, IModelPart> transformer;
        protected final ArrayList<IModelPart> values = new ArrayList<>();
        protected final HashMap<String, IModelPart> parts = new HashMap<>();

        public Container(Class<?> type, Function<P, IModelPart> transformer) {
            this.type = type;
            this.transformer = transformer;
        }

        public void put(String name, P part) {
            IModelPart holder = transformer.apply(part);
            parts.put(name, holder);
            values.add(holder);
            if (holder instanceof AbstractModelPartHolder holder1) {
                holder1.setName(name);
            }
        }

        public void unnamed(Iterable<P> parts) {
            for (P part : parts) {
                values.add(transformer.apply(part));
            }
        }

        public IModelBabyPose babyPose() {
            return babyPose;
        }
    }
}
