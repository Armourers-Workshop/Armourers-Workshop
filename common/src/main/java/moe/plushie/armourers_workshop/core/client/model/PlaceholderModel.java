package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelBabyPose;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.utils.OpenModelPartPose;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class PlaceholderModel implements IModel {

    private final ArrayList<IModelPart> allParts = new ArrayList<>();
    private final HashMap<String, Part> namedParts = new HashMap<>();

    private final DataContainer storage = new DataContainer();

    @Override
    public IModelBabyPose babyPose() {
        return null;
    }

    @Override
    public Part partByName(String name) {
        return namedParts.computeIfAbsent(name, it -> {
            Part part = new Part(name);
            allParts.add(part);
            return part;
        });
    }

    @Override
    public Collection<? extends Part> allParts() {
        return namedParts.values();
    }

    @Override
    public Class<?> type() {
        return PlaceholderModel.class;
    }

    @Override
    public <T> T getAssociatedObject(IAssociatedContainerKey<T> key) {
        return storage.getAssociatedObject(key);
    }

    @Override
    public <T> void setAssociatedObject(IAssociatedContainerKey<T> key, T value) {
        storage.setAssociatedObject(key, value);
    }

    public static class Part implements IModelPart {

        private boolean isVisible = true;
        private OpenModelPartPose pose;
        private final String name;

        public Part(String name) {
            this.name = name;
            this.pose = new OpenModelPartPose(0, 0, 0, 0, 0, 0);
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
        public OpenModelPartPose pose() {
            return pose;
        }
    }
}
