package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModel;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IPlayerModel;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
import moe.plushie.armourers_workshop.compatibility.AbstractModelPartRegistries;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ModelHolder implements IModel {

    private static final HashMap<Class<?>, Entry<?, ?>> ENTRIES = new HashMap<>();

    private final PartSet table = new PartSet();
    private final DataStorage storage = new DataStorage();

    public ModelHolder(Model model, Consumer<PartSet> provider) {
        provider.accept(table);
    }

    @FunctionalInterface
    public interface Factory<V extends Model, M extends IModel> {

        M create(V model, Consumer<PartSet> table);
    }

    public static <V extends Model, M extends IModel> M ofNullable(V model) {
        if (model != null) {
            return of(model);
        }
        return null;
    }

    public static <M extends IModel> M of(Model model) {
        ISkinDataProvider provider = ObjectUtils.safeCast(model, ISkinDataProvider.class);
        if (provider != null) {
            M holder = provider.getSkinData();
            if (holder != null) {
                return holder;
            }
            holder = createHolder(model);
            provider.setSkinData(holder);
            return holder;
        }
        // If the model can't support the cache, we create it every time,
        // although it causes performance degradation, but it works fine.
        return createHolder(model);
    }

    private static <V extends Model, M extends IModel> M createHolder(V model) {
        ArrayList<BiConsumer<V, PartSet>> builders = new ArrayList<>();
        Factory<V, M> factory = null;
        Class<?> clazz = model.getClass();
        while (clazz != Object.class) {
            Entry<?, ?> entry = ENTRIES.get(clazz);
            if (entry != null) {
                Entry<V, M> entry1 = ObjectUtils.unsafeCast(entry);
                builders.add(entry1.builder);
                if (factory == null) {
                    factory = entry1.factory;
                }
            }
            clazz = clazz.getSuperclass();
        }
        if (factory == null) {
            Factory<V, ModelHolder> factory1 = ModelHolder::new;
            factory = ObjectUtils.unsafeCast(factory1);
        }
        return factory.create(model, it -> builders.forEach(builder -> builder.accept(model, it)));
    }

    @Override
    public <V> V getAssociatedObject(IAssociatedContainerKey<V> key) {
        return storage.getAssociatedObject(key);
    }

    @Override
    public <V> void setAssociatedObject(IAssociatedContainerKey<V> key, V value) {
        storage.setAssociatedObject(key, value);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public IModelPart getPart(String name) {
        return table.get(name);
    }

    @Override
    public Collection<IModelPart> getAllParts() {
        return table.values();
    }

    public static class EntityStub extends ModelHolder {

        private final EntityModel<?> model;
        private final Transform transform;

        public EntityStub(EntityModel<?> model, Consumer<PartSet> provider) {
            super(model, provider);
            this.model = model;
            this.transform = AbstractModelPartRegistries.transform(model);
        }

        @Override
        public boolean isBaby() {
            return model.young;
        }

        @Override
        public float getBabyScale() {
            return transform.scale;
        }

        @Override
        public IVector3f getBabyOffset() {
            return transform.offset;
        }
    }

    public static class HumanoidStub extends EntityStub implements IHumanoidModel {

        private final IModelPart hat;
        private final IModelPart head;
        private final IModelPart body;
        private final IModelPart leftArm;
        private final IModelPart rightArm;
        private final IModelPart leftLeg;
        private final IModelPart rightLeg;

        public HumanoidStub(EntityModel<?> model, Consumer<PartSet> provider) {
            super(model, provider);
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

    public static class PlayerStub extends HumanoidStub implements IPlayerModel {

        private final IModelPart leftSleeve;
        private final IModelPart rightSleeve;
        private final IModelPart leftPants;
        private final IModelPart rightPants;
        private final IModelPart jacket;

        public PlayerStub(EntityModel<?> model, Consumer<PartSet> provider) {
            super(model, provider);
            this.leftSleeve = IPlayerModel.super.getLeftSleevePart();
            this.rightSleeve = IPlayerModel.super.getRightSleevePart();
            this.leftPants = IPlayerModel.super.getLeftPantsPart();
            this.rightPants = IPlayerModel.super.getRightPantsPart();
            this.jacket = IPlayerModel.super.getJacketPart();
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

        @Override
        public IModelPart getJacketPart() {
            return jacket;
        }
    }

    public static class Transform {

        public final float scale;
        public final Vector3f offset;

        public Transform(float scale, Vector3f offset) {
            this.scale = scale;
            this.offset = offset;
        }
    }

    public static class Entry<T extends Model, M extends IModel> {

        Class<T> clazz;
        Factory<T, M> factory;
        BiFunction<T, Consumer<PartSet>, IModel> provider;
        BiConsumer<T, PartSet> builder;

        Entry(Class<T> clazz, Factory<T, M> factory, BiConsumer<T, PartSet> builder) {
            this.clazz = clazz;
            this.factory = factory;
            this.builder = builder;
        }
    }

    public static class PartSet {

        final ArrayList<IModelPart> values = new ArrayList<>();
        final HashMap<String, IModelPart> parts = new HashMap<>();

        public void put(String name, ModelPart part) {
            IModelPart holder = ModelPartHolder.of(part);
            parts.put(name, holder);
            values.add(holder);
        }

        public void unnamed(Iterable<ModelPart> parts) {
            for (ModelPart part : parts) {
                values.add(ModelPartHolder.of(part));
            }
        }

        public Collection<IModelPart> values() {
            return values;
        }

        public IModelPart get(String name) {
            return parts.get(name);
        }
    }

    public static <T extends Model> void register(Class<T> clazz, BiConsumer<T, PartSet> builder) {
        register(clazz, null, builder);
    }

    public static <T extends Model> void register(Class<T> clazz, Factory<T, IModel> factory, BiConsumer<T, PartSet> builder) {
        ENTRIES.put(clazz, new Entry<>(clazz, factory, builder));
    }

    public static <T extends Model> void registerOptional(Class<T> clazz, Factory<T, IModel> factory, BiConsumer<T, PartSet> builder) {
        if (clazz != null) {
            register(clazz, factory, builder);
        }
    }
}
