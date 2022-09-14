package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModelHolder;
import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.client.model.IPlayerModelHolder;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
import moe.plushie.armourers_workshop.compatibility.AbstractModelPartRegistries;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Environment(value = EnvType.CLIENT)
public class ModelHolder<T extends Model> implements IModelHolder<T> {

    private static final HashMap<Class<?>, Entry<?, ?>> ENTRIES = new HashMap<>();

    private final PartSet table = new PartSet();


    public ModelHolder(T model, Consumer<PartSet> provider) {
        provider.accept(table);
    }

    @FunctionalInterface
    public interface Factory<V extends Model, M extends IModelHolder<V>> {

        M create(V model, Consumer<PartSet> table);
    }

    public static <V extends Model, M extends IModelHolder<V>> M ofNullable(V model) {
        if (model != null) {
            return of(model);
        }
        return null;
    }

    public static <V extends Model, M extends IModelHolder<V>> M of(V model) {
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

    private static <V extends Model, M extends IModelHolder<V>> M createHolder(V model) {
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
            Factory<V, ModelHolder<V>> factory1 = ModelHolder::new;
            factory = ObjectUtils.unsafeCast(factory1);
        }
        return factory.create(model, it -> builders.forEach(builder -> builder.accept(model, it)));
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean isRiding() {
        return false;
    }

    @Override
    public ModelPart getPart(String name) {
        return table.get(name);
    }

    @Override
    public Collection<ModelPart> getAllParts() {
        return table.values();
    }

    public static class EntityStub<T extends EntityModel<?>> extends ModelHolder<T> {

        private final SoftReference<T> model;
        private final Transform transform;

        public EntityStub(T model, Consumer<PartSet> provider) {
            super(model, provider);
            this.model = new SoftReference<>(model);
            this.transform = AbstractModelPartRegistries.transform(model);
        }

        @Override
        public boolean isBaby() {
            return getModel().young;
        }

        @Override
        public boolean isRiding() {
            return getModel().riding;
        }

        @Override
        public float getBabyScale() {
            return transform.scale;
        }

        @Override
        public IVector3f getBabyOffset() {
            return transform.offset;
        }

        private T getModel() {
            return model.get();
        }
    }

    public static class HumanoidStub<T extends EntityModel<?>> extends EntityStub<T> implements IHumanoidModelHolder<T> {

        private final ModelPart hat;
        private final ModelPart head;
        private final ModelPart body;
        private final ModelPart leftArm;
        private final ModelPart rightArm;
        private final ModelPart leftLeg;
        private final ModelPart rightLeg;

        public HumanoidStub(T model, Consumer<PartSet> provider) {
            super(model, provider);
            this.hat = IHumanoidModelHolder.super.getHatPart();
            this.head = IHumanoidModelHolder.super.getHeadPart();
            this.body = IHumanoidModelHolder.super.getBodyPart();
            this.leftArm = IHumanoidModelHolder.super.getLeftArmPart();
            this.rightArm = IHumanoidModelHolder.super.getRightArmPart();
            this.leftLeg = IHumanoidModelHolder.super.getLeftLegPart();
            this.rightLeg = IHumanoidModelHolder.super.getRightLegPart();
        }

        @Override
        public ModelPart getHatPart() {
            return hat;
        }

        @Override
        public ModelPart getHeadPart() {
            return head;
        }

        @Override
        public ModelPart getBodyPart() {
            return body;
        }

        @Override
        public ModelPart getLeftArmPart() {
            return leftArm;
        }

        @Override
        public ModelPart getRightArmPart() {
            return rightArm;
        }

        @Override
        public ModelPart getLeftLegPart() {
            return leftLeg;
        }

        @Override
        public ModelPart getRightLegPart() {
            return rightLeg;
        }
    }

    public static class PlayerStub<T extends EntityModel<?>> extends HumanoidStub<T> implements IPlayerModelHolder<T> {

        private final ModelPart leftSleeve;
        private final ModelPart rightSleeve;
        private final ModelPart leftPants;
        private final ModelPart rightPants;
        private final ModelPart jacket;

        public PlayerStub(T model, Consumer<PartSet> provider) {
            super(model, provider);
            this.leftSleeve = IPlayerModelHolder.super.getLeftSleevePart();
            this.rightSleeve = IPlayerModelHolder.super.getRightSleevePart();
            this.leftPants = IPlayerModelHolder.super.getLeftPantsPart();
            this.rightPants = IPlayerModelHolder.super.getRightPantsPart();
            this.jacket = IPlayerModelHolder.super.getJacketPart();
        }

        @Override
        public ModelPart getLeftSleevePart() {
            return leftSleeve;
        }

        @Override
        public ModelPart getRightSleevePart() {
            return rightSleeve;
        }

        @Override
        public ModelPart getLeftPantsPart() {
            return leftPants;
        }

        @Override
        public ModelPart getRightPantsPart() {
            return rightPants;
        }

        @Override
        public ModelPart getJacketPart() {
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

    public static class Entry<T extends Model, M extends IModelHolder<T>> {

        Class<T> clazz;
        Factory<T, M> factory;
        BiFunction<T, Consumer<PartSet>, IModelHolder<T>> provider;
        BiConsumer<T, PartSet> builder;

        Entry(Class<T> clazz, Factory<T, M> factory, BiConsumer<T, PartSet> builder) {
            this.clazz = clazz;
            this.factory = factory;
            this.builder = builder;
        }

    }

    public static class PartSet {

        final ArrayList<ModelPart> values = new ArrayList<>();
        final HashMap<String, ModelPart> parts = new HashMap<>();

        public void put(String name, ModelPart part) {
            parts.put(name, part);
            values.add(part);
        }

        public void unnamed(Iterable<ModelPart> parts) {
            parts.forEach(values::add);
        }


        public Collection<ModelPart> values() {
            return values;
        }

        public ModelPart get(String name) {
            return parts.get(name);
        }
    }

    public static <T extends Model> void register(Class<T> clazz, BiConsumer<T, PartSet> builder) {
        register(clazz, null, builder);
    }

    public static <T extends Model> void register(Class<T> clazz, Factory<T, IModelHolder<T>> factory, BiConsumer<T, PartSet> builder) {
        ENTRIES.put(clazz, new Entry<>(clazz, factory, builder));
    }
}
