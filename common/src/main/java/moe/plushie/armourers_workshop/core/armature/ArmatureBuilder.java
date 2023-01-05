package moe.plushie.armourers_workshop.core.armature;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.client.IJoint;
import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.core.armature.core.AfterTransformModifier;
import moe.plushie.armourers_workshop.core.armature.core.DefaultBabyJointModifier;
import moe.plushie.armourers_workshop.core.armature.core.DefaultSkirtJointModifier;
import moe.plushie.armourers_workshop.core.armature.core.DefaultWingJointModifier;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Function;

@SuppressWarnings("unused")
public abstract class ArmatureBuilder {

    private static final ImmutableMap<String, ArmatureModifier> FIXED_MODIFIERS = ImmutableMap.<String, ArmatureModifier>builder()
            .put("armourers_workshop:baby_head_apt", new DefaultBabyJointModifier())
            .put("armourers_workshop:body_to_skirt", new DefaultSkirtJointModifier())
            .put("armourers_workshop:body_to_wing", new DefaultWingJointModifier())
            .build();

    private static final ImmutableMap<String, Function<IDataPackObject, ArmatureModifier>> PARAMETERIZED_MODIFIERS = ImmutableMap.<String, Function<IDataPackObject, ArmatureModifier>>builder()
            .build();

    protected ResourceLocation parent;

    protected final ResourceLocation name;
    protected final ArrayList<IEntityType<?>> entities = new ArrayList<>();
    protected final HashMap<IJoint, Collection<ArmatureModifier>> jointModifiers = new HashMap<>();
    protected final HashMap<IJoint, Collection<ArmatureModifier>> transformModifiers = new HashMap<>();

    public ArmatureBuilder(ResourceLocation name) {
        this.name = name;
        // must init armatures if needs.
        Armatures.init();
    }

    public void load(Collection<ArmatureBuilder> builders) {
        // merge all builder.
        builders.forEach(builder -> {
            jointModifiers.putAll(builder.jointModifiers);
            transformModifiers.putAll(builder.transformModifiers);
            // the entities should not be inherited.
            entities.clear();
            entities.addAll(builder.entities);
        });
    }

    public void load(IDataPackObject object) {
        // read all joint
        object.get("joint").entrySet().forEach(it -> {
            IJoint joint = Armatures.BIPPED.searchJointByName(it.getKey());
            if (joint != null) {
                jointModifiers.put(joint, _parseModelModifiers(it.getValue()));
            }
        });
        object.get("transform").ifPresent(it -> {
            it.get("translate").entrySet().forEach(it2 -> _parseTranslateModifiers(it2.getKey(), it2.getValue()));
            it.get("scale").entrySet().forEach(it2 -> _parseScaleModifiers(it2.getKey(), it2.getValue()));
            it.get("rotate").entrySet().forEach(it2 -> _parseRotateModifiers(it2.getKey(), it2.getValue()));
        });
        object.get("entities").allValues().forEach(it -> entities.add(IEntityType.of(it.stringValue())));
        object.get("parent").ifPresent(it -> {
            parent = new ResourceLocation(it.stringValue());
        });
    }

    public ITransformf[] build(IModelHolder<?> model) {
        HashMap<IJoint, Collection<ArmatureModifier>> modifiers = new HashMap<>();
        jointModifiers.forEach((joint, modifiers1) -> modifiers.computeIfAbsent(joint, k -> new ArrayList<>()).addAll(modifiers1));
        transformModifiers.forEach((joint, modifiers1) -> modifiers.computeIfAbsent(joint, k -> new ArrayList<>()).addAll(modifiers1));
        JointTransformBuilder builder = JointTransformBuilder.of(Armatures.BIPPED);
        modifiers.forEach((joint, modifiers1) -> builder.put(joint, buildTransform(modifiers1, model)));
        return builder.build();
    }

    public ITransformf buildTransform(Collection<ArmatureModifier> modifiers, IModelHolder<?> model) {
        ITransformf transform = ITransformf.NONE;
        for (ArmatureModifier modifier : modifiers) {
            transform = modifier.apply(transform, model);
        }
        return transform;
    }

    public abstract Collection<ArmatureModifier> getTargets(IDataPackObject object);

    public List<IEntityType<?>> getEntities() {
        return entities;
    }

    public ResourceLocation getParent() {
        return parent;
    }

    public ResourceLocation getName() {
        return name;
    }

    private Collection<ArmatureModifier> _parseModelModifiers(IDataPackObject object) {
        switch (object.type()) {
            case STRING: {
                return getTargets(object);
            }
            case DICTIONARY: {
                ArrayList<ArmatureModifier> modifiers = new ArrayList<>();
                modifiers.addAll(getTargets(object));
                modifiers.addAll(_parseTransformModifiers(object, AfterTransformModifier::new));
                modifiers.addAll(_parseModifiers(object.get("modifier")));
                return modifiers;
            }
            default: {
                // sorry, but we can't support this format yet.
                return Collections.emptyList();
            }
        }
    }

    private Collection<ArmatureModifier> _parseModifiers(IDataPackObject object) {
        switch (object.type()) {
            case ARRAY: {
                ArrayList<ArmatureModifier> modifiers = new ArrayList<>();
                object.allValues().forEach(it -> {
                    ArmatureModifier modifier = FIXED_MODIFIERS.get(it.stringValue());
                    if (modifier != null) {
                        modifiers.add(modifier);
                    }
                });
                return modifiers;
            }
            case DICTIONARY: {
                ArrayList<ArmatureModifier> modifiers = new ArrayList<>();
                object.entrySet().forEach(it -> {
                    Function<IDataPackObject, ArmatureModifier> builder = PARAMETERIZED_MODIFIERS.get(it.getKey());
                    if (builder != null) {
                        modifiers.add(builder.apply(it.getValue()));
                        return;
                    }
                    ArmatureModifier modifier = FIXED_MODIFIERS.get(it.getKey());
                    if (modifier != null) {
                        modifiers.add(modifier);
                    }
                });
                return modifiers;
            }
            default: {
                // sorry, but we can't support this format yet.
                return Collections.emptyList();
            }
        }
    }

    private Collection<ArmatureModifier> _parseTransformModifiers(IDataPackObject object, TransformBuilder builder) {
        Vector3f translate = _parseVector(object.get("translate"), Vector3f.ZERO);
        Vector3f scale = _parseVector(object.get("scale"), Vector3f.ONE);
        Vector3f rotate = _parseVector(object.get("rotate"), Vector3f.ZERO);
        if (translate.equals(Vector3f.ZERO) && scale.equals(Vector3f.ONE) && rotate.equals(Vector3f.ZERO)) {
            return Collections.emptyList();
        }
        return Collections.singleton(builder.apply(translate, scale, rotate));
    }

    private Vector3f _parseVector(IDataPackObject object, Vector3f defaultValue) {
        switch (object.type()) {
            case ARRAY: {
                if (object.size() != 3) {
                    break;
                }
                float f1 = object.at(0).numberValue().floatValue();
                float f2 = object.at(1).numberValue().floatValue();
                float f3 = object.at(2).numberValue().floatValue();
                return new Vector3f(f1, f2, f3);
            }
            case DICTIONARY: {
                float f1 = object.get("x").numberValue().floatValue();
                float f2 = object.get("y").numberValue().floatValue();
                float f3 = object.get("z").numberValue().floatValue();
                return new Vector3f(f1, f2, f3);
            }
            default: {
                break;
            }
        }
        return defaultValue;
    }

    private void _parseTranslateModifiers(String name, IDataPackObject object) {
        Vector3f value = _parseVector(object, Vector3f.ZERO);
        if (value.equals(Vector3f.ZERO)) {
            return;
        }
        _addTransformModifier(name, new AfterTransformModifier(value, Vector3f.ONE, Vector3f.ZERO));
    }


    private void _parseRotateModifiers(String name, IDataPackObject object) {
        Vector3f value = _parseVector(object, Vector3f.ZERO);
        if (value.equals(Vector3f.ZERO)) {
            return;
        }
        _addTransformModifier(name, new AfterTransformModifier(Vector3f.ZERO, Vector3f.ONE, value));
    }

    private void _parseScaleModifiers(String name, IDataPackObject object) {
        Vector3f value = _parseVector(object, Vector3f.ONE);
        if (value.equals(Vector3f.ONE)) {
            return;
        }
        _addTransformModifier(name, new AfterTransformModifier(Vector3f.ZERO, value, Vector3f.ZERO));
    }


    private void _addTransformModifier(String name, ArmatureModifier modifier) {
        // ..
        Collection<IJoint> joints;
        if (name.equals("") || name.equals("*")) {
            joints = Armatures.BIPPED.allJoints();
        } else {
            IJoint joint = Armatures.BIPPED.searchJointByName(name);
            if (joint == null) {
                return;
            }
            joints = Collections.singleton(joint);
        }
        for (IJoint joint : joints) {
            transformModifiers.computeIfAbsent(joint, k -> new ArrayList<>()).add(modifier);
        }
    }

    private interface TransformBuilder {
        ArmatureModifier apply(Vector3f t1, Vector3f t2, Vector3f t3);
    }
}
