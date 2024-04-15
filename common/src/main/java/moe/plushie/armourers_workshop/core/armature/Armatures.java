package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.StreamUtils;
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
    public static final Armature MINECART = Builder.named("minecart");

    public static final Armature ANY = Builder.named("any");
    public static final Armature HAND = Builder.named("hand");

    @Nullable
    public static Armature byName(ResourceLocation registryName) {
        return NAMED_ARMATURES.get(registryName);
    }

    public static Armature byType(ISkinType skinType) {
        return LINKED_ARMATURES.getOrDefault(skinType, ANY);
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
        private final LinkedHashMap<Joint, String> jointParents = new LinkedHashMap<>();

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
                IDataPackObject value = it.getValue();
                Joint joint = new Joint(it.getKey());
                namedJoints.put(joint.getName(), joint);
                loadType(value.get("id"), SkinPartTypes::byName, partType -> {
                    if (partType != null) {
                        linkedJoints.put(partType, joint);
                    } else {
                        wildcardJoint = joint;
                    }
                });
                jointShapes.put(joint, ArmatureSerializers.readShape(value.get("cube")));
                jointTransforms.put(joint, ArmatureSerializers.readTransform(value.get("transform"))::apply);
                jointParents.put(joint, value.get("parent").stringValue());
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

        private Armature build(String name) {
            jointParents.forEach((child, parentName) -> child.setParent(namedJoints.get(parentName)));
            Armature armature = new Armature(namedJoints, jointTransforms, linkedJoints, wildcardJoint, jointShapes);
            ResourceLocation registryName = ModConstants.key(name);
            ModLog.debug("Registering Armature '{}'", registryName);
            NAMED_ARMATURES.put(registryName, armature);
            skinTypes.forEach(it -> LINKED_ARMATURES.put(it, armature));
            return armature;
        }
    }
}
