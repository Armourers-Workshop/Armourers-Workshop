package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelBabyPose;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.utils.DataStorage;
import moe.plushie.armourers_workshop.utils.ModelPartPose;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class PlaceholderModel implements IModel {

    private final ArrayList<IModelPart> allParts = new ArrayList<>();
    private final HashMap<String, Part> namedParts = new HashMap<>();

    private final DataStorage storage = new DataStorage();

    @Override
    public IModelBabyPose getBabyPose() {
        return null;
    }

    @Override
    public Part getPart(String name) {
        return namedParts.computeIfAbsent(name, it -> {
            Part part = new Part(name);
            allParts.add(part);
            return part;
        });
    }

    @Override
    public Collection<? extends Part> getAllParts() {
        return namedParts.values();
    }

    @Override
    public Class<?> getType() {
        return PlaceholderModel.class;
    }

    @Override
    public <T> T getAssociatedObject(IAssociatedContainerKey<T> key) {
        return storage.getAssociatedObject(key);
    }

    @Override
    public <T> void setAssociatedObject(T value, IAssociatedContainerKey<T> key) {
        storage.setAssociatedObject(value, key);
    }

    public static class Part implements IModelPart {

        private boolean isVisible = true;
        private ModelPartPose pose;
        private final String name;

        public Part(String name) {
            this.name = name;
            this.pose = new ModelPartPose(0, 0, 0, 0, 0, 0);
        }

        @Override
        public boolean isVisible() {
            return this.isVisible;
        }

        @Override
        public void setVisible(boolean visible) {
            this.isVisible = visible;
        }

        @Override
        public ModelPartPose pose() {
            return pose;
        }
    }
}
