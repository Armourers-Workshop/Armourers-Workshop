package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModel;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelBabyPose;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IPlayerModel;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.utils.DataStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;

@SuppressWarnings("unused")
public class CachedModel<P> implements IModel {

    private final Container<P> container;
    private final DataStorage storage = new DataStorage();

    public CachedModel(Container<P> container) {
        this.container = container;
    }

    @Override
    public IModelBabyPose getBabyPose() {
        return container.getBabyPose();
    }

    @Override
    public IModelPart getPart(String name) {
        return container.parts.get(name);
    }

    @Override
    public Collection<IModelPart> getAllParts() {
        return container.values;
    }

    @Override
    public <T> T getAssociatedObject(IAssociatedContainerKey<T> key) {
        return storage.getAssociatedObject(key);
    }

    @Override
    public <T> void setAssociatedObject(T value, IAssociatedContainerKey<T> key) {
        storage.setAssociatedObject(value, key);
    }

    public static class Humanoid<P> extends CachedModel<P> implements IHumanoidModel {

        private final IModelPart hat;
        private final IModelPart head;
        private final IModelPart body;
        private final IModelPart leftArm;
        private final IModelPart rightArm;
        private final IModelPart leftLeg;
        private final IModelPart rightLeg;

        public Humanoid(Container<P> container) {
            super(container);
            this.hat = IHumanoidModel.super.getHatPart();
            this.head = IHumanoidModel.super.getHeadPart();
            this.body = IHumanoidModel.super.getBodyPart();
            this.leftArm = IHumanoidModel.super.getLeftArmPart();
            this.rightArm = IHumanoidModel.super.getRightArmPart();
            this.leftLeg = IHumanoidModel.super.getLeftLegPart();
            this.rightLeg = IHumanoidModel.super.getRightLegPart();
        }

        @Override
        public IModelPart getHatPart() {
            return hat;
        }

        @Override
        public IModelPart getHeadPart() {
            return head;
        }

        @Override
        public IModelPart getBodyPart() {
            return body;
        }

        @Override
        public IModelPart getLeftArmPart() {
            return leftArm;
        }

        @Override
        public IModelPart getRightArmPart() {
            return rightArm;
        }

        @Override
        public IModelPart getLeftLegPart() {
            return leftLeg;
        }

        @Override
        public IModelPart getRightLegPart() {
            return rightLeg;
        }
    }

    public static class Player<P> extends Humanoid<P> implements IPlayerModel {

        private final IModelPart ear;
        private final IModelPart cloak;
        private final IModelPart jacket;
        private final IModelPart leftSleeve;
        private final IModelPart rightSleeve;
        private final IModelPart leftPants;
        private final IModelPart rightPants;

        public Player(Container<P> container) {
            super(container);
            this.ear = IPlayerModel.super.getEarPart();
            this.cloak = IPlayerModel.super.getCloakPart();
            this.jacket = IPlayerModel.super.getJacketPart();
            this.leftSleeve = IPlayerModel.super.getLeftSleevePart();
            this.rightSleeve = IPlayerModel.super.getRightSleevePart();
            this.leftPants = IPlayerModel.super.getLeftPantsPart();
            this.rightPants = IPlayerModel.super.getRightPantsPart();
        }

        @Override
        public IModelPart getEarPart() {
            return ear;
        }

        @Override
        public IModelPart getCloakPart() {
            return cloak;
        }

        @Override
        public IModelPart getJacketPart() {
            return jacket;
        }

        @Override
        public IModelPart getLeftSleevePart() {
            return leftSleeve;
        }

        @Override
        public IModelPart getRightSleevePart() {
            return rightSleeve;
        }

        @Override
        public IModelPart getLeftPantsPart() {
            return leftPants;
        }

        @Override
        public IModelPart getRightPantsPart() {
            return rightPants;
        }
    }

    public static class Container<P> {

        protected IModelBabyPose babyPose;

        protected final Function<P, IModelPart> transformer;
        protected final ArrayList<IModelPart> values = new ArrayList<>();
        protected final HashMap<String, IModelPart> parts = new HashMap<>();

        public Container(Function<P, IModelPart> transformer) {
            this.transformer = transformer;
        }

        public void put(String name, P part) {
            IModelPart holder = transformer.apply(part);
            parts.put(name, holder);
            values.add(holder);
        }

        public void unnamed(Iterable<P> parts) {
            for (P part : parts) {
                values.add(transformer.apply(part));
            }
        }

        public IModelBabyPose getBabyPose() {
            return babyPose;
        }
    }
}
