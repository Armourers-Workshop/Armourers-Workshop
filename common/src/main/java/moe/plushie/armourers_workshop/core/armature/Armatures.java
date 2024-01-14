package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public class Armatures {

    private static final LinkedHashMap<ISkinType, Armature> LINKED_ARMATURES = new LinkedHashMap<>();
    private static final LinkedHashMap<ResourceLocation, Armature> NAMED_ARMATURES = new LinkedHashMap<>();

    public static final Armature HUMANOID = Builder.named("humanoid");
    public static final Armature HORSE = Builder.named("horse");
    public static final Armature BOAT = Builder.named("boat");

    public static final Armature ANY = Builder.named("any");
    public static final Armature ITEM = Builder.named("item");
    public static final Armature HAND = Builder.named("hand");

    @Nullable
    public static Armature byName(ResourceLocation registryName) {
        return NAMED_ARMATURES.get(registryName);
    }

    public static Armature byType(ISkinType skinType) {
        return LINKED_ARMATURES.getOrDefault(skinType, HUMANOID);
    }

    public static void init() {
    }

    private static class Builder {

        private Joint wildcardJoint;

        private final HashSet<ISkinType> skinTypes = new HashSet<>();
        private final LinkedHashMap<String, Joint> namedJoints = new LinkedHashMap<>();
        private final LinkedHashMap<ISkinPartType, Joint> linkedJoints = new LinkedHashMap<>();
        private final LinkedHashMap<Joint, JointShape> jointShapes = new LinkedHashMap<>();
        private final LinkedHashMap<Joint, IJointTransform> jointTransforms = new LinkedHashMap<>();

        private Builder(String path) {
            ClassLoader loader = this.getClass().getClassLoader();
            InputStream inputStream = loader.getResourceAsStream(path);
            IDataPackObject object = StreamUtils.fromPackObject(inputStream);
            if (object != null) {
                load(object);
            }
        }

        private static Armature named(String name) {
            Builder loader = new Builder("data/armourers_workshop/skin/armatures/" + name + ".json");
            return loader.build(name);
        }

        private void load(IDataPackObject object) {
            object.get("joint").entrySet().forEach(it -> {
                Joint joint = new Joint(it.getKey());
                namedJoints.put(joint.getName(), joint);
                loadType(it.getValue().get("id"), SkinPartTypes::byName, partType -> {
                    if (partType != null) {
                        linkedJoints.put(partType, joint);
                    } else {
                        wildcardJoint = joint;
                    }
                });
                jointShapes.put(joint, parseShape(it.getValue().get("shape")));
                jointTransforms.put(joint, parseTransform(it.getValue().get("transform")));
            });
            loadType(object.get("type"), SkinTypes::byName, skinTypes::add);
        }

        private <T> void loadType(IDataPackObject object, Function<String, T> transformer, Consumer<T> consumer) {
            switch (object.type()) {
                case ARRAY: {
                    object.allValues().forEach(it -> loadType(it, transformer, consumer));
                    return;
                }
                case STRING: {
                    String value = object.stringValue();
                    if (value.isEmpty() || value.equals("*")) {
                        consumer.accept(null);
                        return;
                    }
                    T type = transformer.apply(value);
                    if (type != null) {
                        consumer.accept(type);
                    }
                    return;
                }
                default: {
                    return;
                }
            }
        }

        private JointShape parseShape(IDataPackObject object) {
            if (object.isNull()) {
                return null;
            }
            Vector3f origin = parseVector(object.get("origin"), Vector3f.ZERO);
            Vector3f size = parseVector(object.get("size"), Vector3f.ZERO);
            return new JointShape(origin, size);
        }

        private IJointTransform parseTransform(IDataPackObject object) {
            if (object.isNull()) {
                return IJointTransform.NONE;
            }
            Vector3f translate = parseVector(object.get("translate"), Vector3f.ZERO);
            Vector3f scale = parseVector(object.get("scale"), Vector3f.ONE);
            Vector3f rotation = parseVector(object.get("rotation"), Vector3f.ZERO);
            Vector3f pivot = parseVector(object.get("pivot"), Vector3f.ZERO);
            Vector3f offset = parseVector(object.get("offset"), Vector3f.ZERO);
            SkinTransform transform = SkinTransform.create(translate, rotation, scale, pivot, offset);
            return transform::apply;
        }

        private Vector3f parseVector(IDataPackObject object, Vector3f defaultValue) {
            switch (object.type()) {
                case ARRAY: {
                    if (object.size() != 3) {
                        break;
                    }
                    float f1 = object.at(0).floatValue();
                    float f2 = object.at(1).floatValue();
                    float f3 = object.at(2).floatValue();
                    return new Vector3f(f1, f2, f3);
                }
                case DICTIONARY: {
                    float f1 = object.get("x").floatValue();
                    float f2 = object.get("y").floatValue();
                    float f3 = object.get("z").floatValue();
                    return new Vector3f(f1, f2, f3);
                }
                default: {
                    break;
                }
            }
            return defaultValue;
        }

        private Armature build(String name) {
            Armature armature = new Armature(namedJoints, jointTransforms, linkedJoints, wildcardJoint, jointShapes);
            NAMED_ARMATURES.put(ModConstants.key(name), armature);
            skinTypes.forEach(it -> LINKED_ARMATURES.put(it, armature));
            return armature;
        }
    }
}
