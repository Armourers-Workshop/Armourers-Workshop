package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.utils.JsonSerializer;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public class Armatures {

    private static final LinkedHashMap<SkinType, Armature> LINKED_ARMATURES = new LinkedHashMap<>();
    private static final LinkedHashMap<OpenResourceKey, Armature> NAMED_ARMATURES = new LinkedHashMap<>();

    public static final Armature HUMANOID = Builder.named("humanoid");
    public static final Armature HORSE = Builder.named("horse");
    public static final Armature BOAT = Builder.named("boat");
    public static final Armature MINECART = Builder.named("minecart");

    public static final Armature ANY = Builder.named("any");
    public static final Armature HAND = Builder.named("hand");

    @Nullable
    public static Armature byName(OpenResourceKey registryName) {
        return NAMED_ARMATURES.get(registryName);
    }

    public static Armature byType(SkinType skinType) {
        return LINKED_ARMATURES.getOrDefault(skinType, ANY);
    }

    public static void init() {
    }

    private static class Builder {

        private Joint wildcardJoint;

        private final HashSet<SkinType> skinTypes = new HashSet<>();
        private final LinkedHashMap<String, Joint> namedJoints = new LinkedHashMap<>();
        private final LinkedHashMap<SkinPartType, Joint> linkedJoints = new LinkedHashMap<>();
        private final LinkedHashMap<Joint, JointShape> jointShapes = new LinkedHashMap<>();
        private final LinkedHashMap<Joint, IJointTransform> jointTransforms = new LinkedHashMap<>();
        private final LinkedHashMap<Joint, String> jointParents = new LinkedHashMap<>();

        private Builder(String path) {
            var loader = this.getClass().getClassLoader();
            try (var inputStream = loader.getResourceAsStream(path)) {
                this.load(JsonSerializer.readFromStream(inputStream));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static Armature named(String name) {
            var loader = new Builder("data/armourers_workshop/skin/armatures/" + name + ".json");
            return loader.build(name);
        }

        private void load(IODataObject object) {
            object.get("joint").entrySet().forEach(it -> {
                var value = it.getValue();
                var joint = new Joint(it.getKey());
                namedJoints.put(joint.name(), joint);
                loadType(value.get("id"), SkinPartTypes::byName, partType -> {
                    if (partType != null) {
                        linkedJoints.put(partType, joint);
                    } else {
                        wildcardJoint = joint;
                    }
                });
                var transform = ArmatureSerializers.readTransform(value.get("transform"));
                jointShapes.put(joint, ArmatureSerializers.readShape(value.get("cube")));
                jointTransforms.put(joint, transform::apply);
                jointParents.put(joint, value.get("parent").stringValue());
            });
            loadType(object.get("type"), SkinTypes::byName, skinTypes::add);
        }

        private <T> void loadType(IODataObject object, Function<String, T> transformer, Consumer<T> consumer) {
            switch (object.type()) {
                case ARRAY: {
                    object.allValues().forEach(it -> loadType(it, transformer, consumer));
                    return;
                }
                case STRING: {
                    var value = object.stringValue();
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

        private Armature build(String name) {
            jointParents.forEach((child, parentName) -> child.setParent(namedJoints.get(parentName)));
            var armature = new Armature(namedJoints, jointTransforms, linkedJoints, wildcardJoint, jointShapes);
            var registryName = ModConstants.key(name);
            ModLog.debug("Registering Armature '{}'", registryName);
            NAMED_ARMATURES.put(registryName, armature);
            skinTypes.forEach(it -> LINKED_ARMATURES.put(it, armature));
            return armature;
        }
    }
}
