package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.common.IEntityTypeProvider;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.core.armature.core.AfterTransformModifier;
import moe.plushie.armourers_workshop.core.armature.core.DefaultOverriddenArmaturePlugin;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class ArmatureTransformerBuilder {

    protected ResourceLocation parent;
    protected Armature armature;
    protected IDataPackObject contents;

    protected final ResourceLocation name;
    protected final ArrayList<ResourceLocation> models = new ArrayList<>();
    protected final ArrayList<IEntityTypeProvider<?>> entities = new ArrayList<>();
    protected final ArrayList<String> pluginModifiers = new ArrayList<>();
    protected final HashMap<String, Collection<String>> overrideModifiers = new HashMap<>();
    protected final HashMap<IJoint, Collection<JointModifier>> jointModifiers = new HashMap<>();
    protected final HashMap<IJoint, Collection<JointModifier>> transformModifiers = new HashMap<>();

    public ArmatureTransformerBuilder(ResourceLocation name) {
        this.name = name;
    }

    public void load(IDataPackObject object) {
        object.get("parent").ifPresent(it -> {
            parent = new ResourceLocation(it.stringValue());
        });
        object.get("target").ifPresent(it -> {
            armature = Armatures.byName(new ResourceLocation(it.stringValue()));
        });
        contents = object;
        if (armature != null) {
            _parseContent(object);
        }
    }

    public void resolve(Collection<ArmatureTransformerBuilder> hierarchy) {
        _parseContentWithParent(contents, hierarchy);
        hierarchy.forEach(builder -> {
            _mergeTo(builder.jointModifiers, jointModifiers);
            _mergeTo(builder.transformModifiers, transformModifiers);
            _mergeTo(builder.overrideModifiers, overrideModifiers);
            _mergeTo(builder.pluginModifiers, pluginModifiers);
            if (models.isEmpty()) {
                _mergeTo(builder.models, models); // only inherit when empty.
            }
            if (entities.isEmpty()) {
                _mergeTo(builder.entities, entities); // only inherit when empty.
            }
        });
    }

    public ArmatureTransformer build(IModel model) {
        ArrayList<ArmaturePlugin> plugins = new ArrayList<>();
        HashMap<IJoint, ArrayList<JointModifier>> modifiers = new HashMap<>();
        plugins.add(new DefaultOverriddenArmaturePlugin(model, overrideModifiers));
        jointModifiers.forEach((joint, modifiers1) -> modifiers.computeIfAbsent(joint, k -> new ArrayList<>()).addAll(modifiers1));
        transformModifiers.forEach((joint, modifiers1) -> modifiers.computeIfAbsent(joint, k -> new ArrayList<>()).addAll(modifiers1));
        pluginModifiers.forEach(it -> plugins.add(buildPlugin(it)));
        plugins.removeIf(Objects::isNull);
        IModel model1 = buildModel(model, plugins);
        ArmatureTransformer transformer = new ArmatureTransformer(armature, plugins);
        modifiers.forEach((joint, values) -> transformer.put(joint, buildTransform(joint, model1, values)));
        return transformer;
    }

    protected IModel buildModel(IModel model, Collection<ArmaturePlugin> plugins) {
        for (ArmaturePlugin plugin : plugins) {
            model = plugin.apply(model);
        }
        return model;
    }

    protected ArmaturePlugin buildPlugin(String name) {
        Supplier<? extends ArmaturePlugin> builder = ArmatureSerializers.getPlugin(name);
        if (builder != null) {
            return builder.get();
        }
        return null;
    }

    protected IJointTransform buildTransform(IJoint joint, IModel model, Collection<JointModifier> modifiers) {
        IJointTransform transform = IJointTransform.NONE;
        for (JointModifier modifier : modifiers) {
            transform = modifier.apply(joint, model, transform);
        }
        return transform;
    }

    protected abstract JointModifier buildJointTarget(String name);

    public ArrayList<ResourceLocation> getModels() {
        return models;
    }

    public ArrayList<IEntityTypeProvider<?>> getEntities() {
        return entities;
    }

    public ResourceLocation getParent() {
        return parent;
    }

    public ResourceLocation getName() {
        return name;
    }

    private void _parseContentWithParent(IDataPackObject object, Collection<ArmatureTransformerBuilder> hierarchy) {
        // if load has been completed, ignore.
        if (armature != null || object == null) {
            return;
        }
        // Find the last armature.
        for (ArmatureTransformerBuilder builder : hierarchy) {
            if (builder.armature != null) {
                armature = builder.armature;
                break; // this -> this.parent -> this.parent.parent -> ...
            }
        }
        if (armature != null) {
            _parseContent(object);
        }
    }

    private void _parseContent(IDataPackObject object) {
        // read all joint
        object.get("joint").entrySet().forEach(it -> {
            IJoint joint = armature.getJoint(it.getKey());
            if (joint != null) {
                jointModifiers.put(joint, _parseModelModifiers(it.getValue()));
            }
        });
        object.get("transform").ifPresent(it -> {
            it.get("translate").entrySet().forEach(it2 -> _parseTranslateModifiers(it2.getKey(), it2.getValue()));
            it.get("scale").entrySet().forEach(it2 -> _parseScaleModifiers(it2.getKey(), it2.getValue()));
            it.get("rotate").entrySet().forEach(it2 -> _parseRotateModifiers(it2.getKey(), it2.getValue()));
        });
        object.get("override").entrySet().forEach(it -> overrideModifiers.put(it.getKey(), _parseOverrideModifiers(it.getValue())));
        object.get("plugin").allValues().forEach(it -> pluginModifiers.add(it.stringValue()));
        object.get("model").allValues().forEach(it -> models.add(ArmatureSerializers.readResourceLocation(it)));
        object.get("entity").allValues().forEach(it -> entities.add(ArmatureSerializers.readEntityType(it)));

        // clear all contents.
        contents = null;
    }

    private Collection<JointModifier> _parseModelModifiers(IDataPackObject object) {
        switch (object.type()) {
            case STRING: {
                return _parseJointTargets(object);
            }
            case DICTIONARY: {
                ArrayList<JointModifier> modifiers = new ArrayList<>();
                modifiers.addAll(_parseJointTargets(object));
                modifiers.addAll(_parseTransformModifiers(object));
                modifiers.addAll(_parseModifiers(object.get("modifier")));
                return modifiers;
            }
            default: {
                // sorry, but we can't support this format yet.
                return Collections.emptyList();
            }
        }
    }

    private Collection<JointModifier> _parseJointTargets(IDataPackObject object) {
        switch (object.type()) {
            case DICTIONARY: {
                return _parseJointTargets(object.get("target"));
            }
            case STRING: {
                String value = object.stringValue();
                if (!value.isEmpty()) {
                    return Collections.singleton(buildJointTarget(value));
                }
                return Collections.emptyList();
            }
            default: {
                return Collections.emptyList();
            }
        }
    }

    private Collection<JointModifier> _parseModifiers(IDataPackObject object) {
        switch (object.type()) {
            case ARRAY: {
                ArrayList<JointModifier> modifiers = new ArrayList<>();
                object.allValues().forEach(it -> {
                    Supplier<? extends JointModifier> modifier = ArmatureSerializers.getModifier(it.stringValue());
                    if (modifier != null) {
                        modifiers.add(modifier.get());
                    }
                });
                return modifiers;
            }
            case DICTIONARY: {
                ArrayList<JointModifier> modifiers = new ArrayList<>();
                object.entrySet().forEach(it -> {
//                    Function<IDataPackObject, JointModifier> builder = PARAMETERIZED_MODIFIERS.get(it.getKey());
//                    if (builder != null) {
//                        modifiers.add(builder.apply(it.getValue()));
//                        return;
//                    }
                    Supplier<? extends JointModifier> modifier = ArmatureSerializers.getModifier(it.getKey());
                    if (modifier != null) {
                        modifiers.add(modifier.get());
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

    private Collection<JointModifier> _parseTransformModifiers(IDataPackObject object) {
        ITransformf transform = ArmatureSerializers.readTransform(object);
        if (transform.isIdentity()) {
            return Collections.emptyList();
        }
        return Collections.singleton(new AfterTransformModifier(transform));
    }


    private Collection<String> _parseOverrideModifiers(IDataPackObject object) {
        switch (object.type()) {
            case STRING:
                return Collections.singleton(object.stringValue());
            case ARRAY:
                return object.collect(IDataPackObject::stringValue);
            default:
                return Collections.emptyList();
        }
    }

    private void _parseTranslateModifiers(String name, IDataPackObject object) {
        Vector3f value = ArmatureSerializers.readVector(object, Vector3f.ZERO);
        if (value.equals(Vector3f.ZERO)) {
            return;
        }
        ITransformf transform = SkinTransform.createTranslateTransform(value);
        _addTransformModifier(name, new AfterTransformModifier(transform));
    }

    private void _parseRotateModifiers(String name, IDataPackObject object) {
        Vector3f value = ArmatureSerializers.readVector(object, Vector3f.ZERO);
        if (value.equals(Vector3f.ZERO)) {
            return;
        }
        ITransformf transform = SkinTransform.createRotationTransform(value);
        _addTransformModifier(name, new AfterTransformModifier(transform));
    }

    private void _parseScaleModifiers(String name, IDataPackObject object) {
        Vector3f value = ArmatureSerializers.readVector(object, Vector3f.ONE);
        if (value.equals(Vector3f.ONE)) {
            return;
        }
        ITransformf transform = SkinTransform.createScaleTransform(value);
        _addTransformModifier(name, new AfterTransformModifier(transform));
    }

    private void _addTransformModifier(String name, JointModifier modifier) {
        // ..
        Collection<? extends IJoint> joints;
        if (name.equals("") || name.equals("*")) {
            joints = armature.allJoints();
        } else {
            IJoint joint = armature.getJoint(name);
            if (joint == null) {
                return;
            }
            joints = Collections.singleton(joint);
        }
        for (IJoint joint : joints) {
            transformModifiers.computeIfAbsent(joint, k -> new ArrayList<>()).add(modifier);
        }
    }

    private <K, V> void _mergeTo(Map<K, V> other, Map<K, V> result) {
        other.forEach((key, value) -> {
            if (!result.containsKey(key)) {
                result.put(key, value);
            }
        });
    }

    private <V> void _mergeTo(Collection<V> other, Collection<V> result) {
        result.addAll(other);
    }

    private interface ArmatureModifierBuilder {
        JointModifier apply(Vector3f t1, Vector3f t2, Vector3f t3);
    }
}
